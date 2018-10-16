package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.messages.*;
import com.jeff.fischman.exercise.process.reporting.OutputController;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Processor implements MessageHandler {
    private Stream<String> _stream;
    private Parser _parser;
    private TradeProcessor _tradeProcessor;
    private OrderDistributor _orderDistributor;
    private OutputController _outputController;

    public Processor(Stream<String> stream,
                     Parser parser,
                     TradeProcessor tradeProcessor,
                     OrderDistributor orderDistributor,
                     OutputController outputController)
    {
        _stream = stream;
        _parser = parser;
        _tradeProcessor = tradeProcessor;
        _orderDistributor = orderDistributor;
        _outputController = outputController;
    }

    public void processMessages() {
        // process every message from our input stream
        _stream.forEach(this::processInput);
        // now that we're all done, call our onEndOfData routine for final processing.
        onEndOfData();
    }
    // This method called for every line of input.
    private void processInput(String line) {

        Message message = _parser.parse(line);
        if (message != null) {
            // call onTrade() or onOrder() as appropriate to the 'message' type.
            message.invokeHandlerMethod(this);
        }
        // Perform all the per-message reporting that is required.
        _outputController.printPostMessageReport();
    }

    private void onEndOfData()
    {
        _outputController.printFinalReport();
    }

    @Override
    public void onTrade(Trade trade) {
        _tradeProcessor.process(trade);
    }

    @Override
    public void onOrder(Order order) {
        // Delegate the order to the proper processor.
        _orderDistributor.routeToProcessor(order);
    }
}
