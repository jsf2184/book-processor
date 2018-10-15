package com.jeff.fischman.exercise.process.verification;

import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.messages.Action;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.Side;
import com.jeff.fischman.exercise.process.OrderDistributor;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MissingMessageCheckerTests {
    private ExpectedMessageStore _expectedMessageStore;
    private ErrorCounts _errorCounts;
    private OrderDistributor _orderDistributor;

    @Test
    public void testNoMissingOrdersOrMissingTrades() {
        MissingMessageChecker sut = createSut(true, 0, 0);
        MissingMessageChecker.Result result = sut.checkForMissedMessages();
        validateResult(result, 0, 0, 0);
        validateErrorCounts(0, 0);
        verify(_expectedMessageStore, times(1)).getMissingChangedOrderCount();
        verify(_expectedMessageStore, times(1)).getMissingTradeCount();
        verifyZeroInteractions(_orderDistributor);
        verify(_expectedMessageStore, times(0)).clearMissingMessages();
    }

    @Test
    public void testMissingTrades() {
        MissingMessageChecker sut = createSut(true, 0, 3);
        MissingMessageChecker.Result result = sut.checkForMissedMessages();
        validateResult(result, 3, 0, 0);
        validateErrorCounts(3, 0);
        verify(_expectedMessageStore, times(1)).getMissingChangedOrderCount();
        verify(_expectedMessageStore, times(1)).getMissingTradeCount();
        verifyZeroInteractions(_orderDistributor);
        verify(_expectedMessageStore, times(1)).clearMissingMessages();
        verify(_expectedMessageStore, times(0)).getMissingChangedOrderStream();
    }

    @Test
    public void testMissingOrdersWithoutOrderGeneration() {
        MissingMessageChecker sut = createSut(false, 3, 0);
        MissingMessageChecker.Result result = sut.checkForMissedMessages();
        validateResult(result, 0, 3, 0);
        validateErrorCounts(0, 3);
        verify(_expectedMessageStore, times(1)).getMissingChangedOrderCount();
        verify(_expectedMessageStore, times(1)).getMissingTradeCount();
        verifyZeroInteractions(_orderDistributor);
        verify(_expectedMessageStore, times(1)).clearMissingMessages();
        verify(_expectedMessageStore, times(0)).getMissingChangedOrderStream();
    }

    @Test
    public void testMissingOrdersWithOrderGeneration() {
        MissingMessageChecker sut = createSut(true, 2, 0);

        // create the missing orders stream
        List<Order> missingOrderList = new ArrayList<>();
        missingOrderList.add(new Order(Action.Remove, 3L, Side.Buy, 3L, new BigDecimal(100)));
        missingOrderList.add(new Order(Action.Modify, 4L, Side.Buy, 92L, new BigDecimal(100)));
        when(_expectedMessageStore.getMissingChangedOrderStream()).thenReturn(missingOrderList.stream());

        MissingMessageChecker.Result result = sut.checkForMissedMessages();
        validateResult(result, 0, 2, 2);
        validateErrorCounts(0, 2);
        verify(_expectedMessageStore, times(1)).getMissingChangedOrderCount();
        verify(_expectedMessageStore, times(1)).getMissingTradeCount();
        verify(_expectedMessageStore, times(1)).clearMissingMessages();

        verify(_expectedMessageStore, times(1)).getMissingChangedOrderStream();
        // verify missing orders sent to _orderDistributor
        verify(_orderDistributor, times(1)).routeToProcessor(missingOrderList.get(0));
        verify(_orderDistributor, times(1)).routeToProcessor(missingOrderList.get(1));

    }


    private void validateErrorCounts(int expectedTradeUnreportedCount,
                                     int expectedOrderChangeUnreportedCount) {
        Assert.assertEquals(expectedTradeUnreportedCount, _errorCounts.getCount(ErrorType.TradeUnreported));
        Assert.assertEquals(expectedOrderChangeUnreportedCount, _errorCounts.getCount(ErrorType.OrderChangeUnreported));
        Assert.assertEquals(expectedOrderChangeUnreportedCount + expectedTradeUnreportedCount,
                            _errorCounts.getTotalErrorCount());
    }

    private void validateResult(MissingMessageChecker.Result result,
                                int expectedMissingTrades,
                                int expectedMissingOrders,
                                int expectedGeneratedOrders) {
        Assert.assertEquals(expectedMissingTrades, result.getMissingTrades());
        Assert.assertEquals(expectedMissingOrders, result.getMissingOrders());
        Assert.assertEquals(expectedGeneratedOrders, result.getGeneratedOrders());
    }

    private MissingMessageChecker createSut(boolean simulateMissedMessages,
                                            int missingOrderCount,
                                            int missingTradeCount)
    {
        _errorCounts = new ErrorCounts();
        _expectedMessageStore = mock(ExpectedMessageStore.class);
        when(_expectedMessageStore.getMissingChangedOrderCount()).thenReturn(missingOrderCount);
        when(_expectedMessageStore.getMissingTradeCount()).thenReturn(missingTradeCount);
        _orderDistributor = mock(OrderDistributor.class);
        MissingMessageChecker sut = new MissingMessageChecker(simulateMissedMessages,
                                                              _expectedMessageStore,
                                                              _errorCounts,
                                                              _orderDistributor);
        return sut;
    }
}
