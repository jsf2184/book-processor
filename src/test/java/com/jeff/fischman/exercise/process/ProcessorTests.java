package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.messages.*;
import com.jeff.fischman.exercise.process.reporting.Reporter;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class ProcessorTests {
    private Stream<String> _stream;
    private List<Order> _orders;
    private List<Trade> _trades;
    private Parser _parser;
    private TradeProcessor _tradeProcessor;
    private OrderDistributor _orderDistributor;
    private Reporter _reporter;

    @Test
    public void testProcessInputPerformsProperDelegation() {
        // create a processor whose stream contains 3 orders and 3 trades.
        int numTradesAndOrders = 3;
        Processor sut = createSut(numTradesAndOrders);
        // Now have the sut process its stream
        sut.processMessages();

        // And verify whether the processor passed these trades and orders as expected
        for (int i=0; i<numTradesAndOrders; i++) {
            // for each order and trade, certain things should happen. Use verify to make sure they did.
            verify(_orderDistributor, times(1)).routeToProcessor(_orders.get(i));
            verify(_tradeProcessor, times(1)).process(_trades.get(i));
        }
        // the reporters printPostMessageReport() should have been called once for each trade and order
        verify(_reporter, times(numTradesAndOrders * 2)).printPostMessageReport();
        // and printFinal report should be called exactly once.
        verify(_reporter, times(1)).printFinalReport();
    }

    private Processor createSut(int numTradesAndOrders) {
        _parser = mock(Parser.class);
        _orders = new ArrayList<>();
        _trades = new ArrayList<>();
        List<String> inputList = new ArrayList<>();
        for (int i=0; i<numTradesAndOrders; i++) {
            // set up the parser to take a bogus orderStr and return a order for it.
            String orderStr = "O" + i;
            inputList.add(orderStr);
            Order order = new Order(Action.Add, i+1, Side.Buy, i+i, new BigDecimal(i));
            when(_parser.parse(orderStr)).thenReturn(order);
            _orders.add(order);

            // set up the parser to take a bogus tradeStr and return a trade for it.
            String tradeStr = "T" + i;
            inputList.add(tradeStr);
            Trade trade = new Trade(i, new BigDecimal(i));

            when(_parser.parse(tradeStr)).thenReturn(trade);
            _trades.add(trade);
        }

        _stream = inputList.stream();
        _tradeProcessor = mock(TradeProcessor.class);
        _orderDistributor = mock(OrderDistributor.class);
        _reporter = mock(Reporter.class);

        Processor sut = new Processor(_stream,
                                      _parser,
                                      _tradeProcessor,
                                      _orderDistributor,
                                      _reporter);
        return sut;
    }
}
