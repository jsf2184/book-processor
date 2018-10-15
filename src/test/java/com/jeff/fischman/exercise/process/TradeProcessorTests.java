package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.messages.Trade;
import com.jeff.fischman.exercise.process.reporting.TradeReporter;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class TradeProcessorTests {

    private ErrorCounts _errorCounts;
    private ExpectedMessageStore _expectedMessageStore;
    private TradeReporter _tradeReporter;

    @Test
    public void testWhenTradesNotExpected() {
        TradeProcessor sut = createSut();
        when(_expectedMessageStore.hasMissingTrades()).thenReturn(false);
        Trade trade = new Trade(3L, new BigDecimal(11));
        sut.process(trade);
        verify(_expectedMessageStore, times(1)).hasMissingTrades();
        verify(_expectedMessageStore, times(0)).onActualTrade(trade);
        Assert.assertEquals(1, _errorCounts.getCount(ErrorType.UnmatchedTrade));
        Assert.assertEquals(1, _errorCounts.getTotalErrorCount());
        verifyZeroInteractions(_tradeReporter);
    }

    @Test
    public void testWhenTradesIsExpectedButDoesntMatchOneThatIsExpected() {
        TradeProcessor sut = createSut();
        when(_expectedMessageStore.hasMissingTrades()).thenReturn(true);
        Trade trade = new Trade(3L, new BigDecimal(11));
        when(_expectedMessageStore.onActualTrade(trade)).thenReturn(false);
        sut.process(trade);
        Assert.assertEquals(1, _errorCounts.getCount(ErrorType.UnmatchedTrade));
        Assert.assertEquals(1, _errorCounts.getTotalErrorCount());
        verify(_expectedMessageStore, times(1)).hasMissingTrades();
        verify(_expectedMessageStore, times(1)).onActualTrade(trade);
        verifyZeroInteractions(_tradeReporter);
    }

    @Test
    public void testWhenTradesMatchesExpectedTrade() {
        TradeProcessor sut = createSut();
        when(_expectedMessageStore.hasMissingTrades()).thenReturn(true);
        Trade trade = new Trade(3L, new BigDecimal(11));
        when(_expectedMessageStore.onActualTrade(trade)).thenReturn(true);
        sut.process(trade);
        Assert.assertEquals(0, _errorCounts.getCount(ErrorType.UnmatchedTrade));
        Assert.assertEquals(0, _errorCounts.getTotalErrorCount());
        verify(_expectedMessageStore, times(1)).hasMissingTrades();
        verify(_expectedMessageStore, times(1)).onActualTrade(trade);
        verify(_tradeReporter, times(1)).onTrade(trade.getPrice(), trade.getQuantity());
    }

    private TradeProcessor createSut() {
        _errorCounts = new ErrorCounts();
        _expectedMessageStore = mock(ExpectedMessageStore.class);
        _tradeReporter = mock(TradeReporter.class);

        TradeProcessor sut = new TradeProcessor(_errorCounts,
                                                _expectedMessageStore,
                                                _tradeReporter);
        return sut;
    }

}
