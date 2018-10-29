package com.jeff.fischman.exercise.process.verification;

import com.jeff.fischman.exercise.messages.Action;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.Side;
import com.jeff.fischman.exercise.messages.Trade;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ExpectedMessageStoreTests {

    @Test
    public void testNewlyCreatedStore() {
        ExpectedMessageStore sut = new ExpectedMessageStore();
        validateEmptyStore(sut);
    }
    
    @Test
    public void testExpectedOrderScenario() {
        ExpectedMessageStore sut = new ExpectedMessageStore();
        Order o1 = new Order(Action.Remove, 1L, Side.Buy, 3L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(o1);
        validateOrderExpectations(sut, 1);
        Order o2 = new Order(Action.Remove, 2L, Side.Buy, 4L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(o2);
        validateOrderExpectations(sut, 2);

        // Now feed in the orders as actuals
        sut.onActualChangedOrder(o1);
        validateOrderExpectations(sut, 1);
        sut.onActualChangedOrder(o2);
        validateOrderExpectations(sut, 0);
    }

    @Test
    public void testExpectedOrderStreamReplayIsntAConcurrentModificationViolation() {
        ExpectedMessageStore sut = new ExpectedMessageStore();
        Order o1 = new Order(Action.Remove, 1L, Side.Buy, 3L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(o1);
        validateOrderExpectations(sut, 1);
        Order o2 = new Order(Action.Remove, 2L, Side.Buy, 4L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(o2);
        validateOrderExpectations(sut, 2);

        Stream<Order> missingChangedOrderStream = sut.getMissingChangedOrderStream();
        // When we go through the stream, we are modifying the underlying map at the same time we
        // are removing elements from it. Prior to using a ConcurrentHashMap in ExpectedMessageStore, this
        // would have caused a ConcurrentModificationException.
        //
        missingChangedOrderStream.forEach(sut::onActualChangedOrder);
        // And make sure they are truly gone.
        Assert.assertEquals(0, sut. getMissingChangedOrderCount());
    }

    @Test
    public void testActualOrderDoesNotMatchExpectedOrderId() {
        ExpectedMessageStore sut = new ExpectedMessageStore();

        Order expected = new Order(Action.Remove, 1L, Side.Buy, 3L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(expected);
        validateOrderExpectations(sut, 1);

        // Lets send in an actual that has the wrong orderid
        Order actual = new Order(Action.Remove, 2L, Side.Buy, 3L, new BigDecimal("10.0"));
        sut.onActualChangedOrder(actual);
        validateOrderExpectations(sut, 1);
    }

    @Test
    public void testActualOrderDoesNotMatchExpectedContent() {
        ExpectedMessageStore sut = new ExpectedMessageStore();

        Order expected = new Order(Action.Remove, 1L, Side.Buy, 3L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(expected);
        validateOrderExpectations(sut, 1);

        // Lets send in an actual that has the wrong quantity
        Order actual = new Order(Action.Remove, 1L, Side.Buy, 4L, new BigDecimal("10.0"));
        sut.onActualChangedOrder(actual);
        validateOrderExpectations(sut, 1);
    }

    // The following test demonstrates our ability to stream unmatched orders. This is a valuable
    // capability as it is necessary if the feed omits expected post-trade orders. To keep our
    // Book for being permanently off, our real-world recourse will be to play the orders that
    // the feed neglected to send.
    //
    @Test
    public void testUnmatchedOrdersCanBeReplayedAndThenCleared() {
        // First feed in expectations.
        ExpectedMessageStore sut = new ExpectedMessageStore();
        Order o1 = new Order(Action.Remove, 1L, Side.Buy, 3L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(o1);
        validateOrderExpectations(sut, 1);
        Order o2 = new Order(Action.Remove, 2L, Side.Buy, 4L, new BigDecimal("10.0"));
        sut.addChangedOrderExpectation(o2);
        validateOrderExpectations(sut, 2);

        // We want to stream the unmet expectations. But first, put the orders we expect
        // to stream in a set so we can check that we got them from the stream.
        //
        Set<Order> expectedStreamOrders = new HashSet<>();
        expectedStreamOrders.add(o1);
        expectedStreamOrders.add(o2);
        Stream<Order> unmatchedOrderStream = sut.getMissingChangedOrderStream();
        unmatchedOrderStream.forEach(expectedStreamOrders::remove);
        Assert.assertEquals(0, expectedStreamOrders.size());

        // Before we clear the unmatched expectations, check the size again.
        validateOrderExpectations(sut, 2);

        // now clear them
        sut.clearMissingMessages();
        validateOrderExpectations(sut, 0);
    }

    @Test
    public void testExpectedTradeScenario() {
        ExpectedMessageStore sut = new ExpectedMessageStore();
        Trade t1 = new Trade(1L, new BigDecimal("10.0"));
        sut.addTradeExpectation(t1);
        validateTradeExpectations(sut, 1);
        Trade t2 = new Trade(2L, new BigDecimal("10.0"));
        sut.addTradeExpectation(t2);
        validateTradeExpectations(sut, 2);

        // Now feed in the trades as actuals
        Assert.assertTrue(sut.onActualTrade(t1));
        validateTradeExpectations(sut, 1);
        Assert.assertTrue(sut.onActualTrade(t2));
        validateTradeExpectations(sut, 0);
    }

    @Test
    public void testActualTradeDoesNotMatchExpectedContent() {
        ExpectedMessageStore sut = new ExpectedMessageStore();

        Trade expected = new Trade(1L, new BigDecimal("10.0"));
        sut.addTradeExpectation(expected);
        validateTradeExpectations(sut, 1);

        // Build an actual with the wrong quantity.
        Trade actual = new Trade(2L, new BigDecimal("10.0"));
        Assert.assertFalse(sut.onActualTrade(actual));
        validateTradeExpectations(sut, 1);

        // And make sure that these unmet trade expectations can be cleard.
        sut.clearMissingMessages();
        validateTradeExpectations(sut, 0);


    }

    private void validateEmptyStore(ExpectedMessageStore sut) {
        validateOrderExpectations(sut, 0);
        validateTradeExpectations(sut, 0);
    }
    
    private void validateOrderExpectations(ExpectedMessageStore sut, int expectedCount) {
        Assert.assertEquals(expectedCount, sut.getMissingChangedOrderCount());
        if (expectedCount == 0) {
            Assert.assertFalse(sut.hasMissingChangedOrders());
        } else {
            Assert.assertTrue(sut.hasMissingChangedOrders());
        }
    }

    private void validateTradeExpectations(ExpectedMessageStore sut, int expectedCount) {
        Assert.assertEquals(expectedCount, sut.getMissingTradeCount());
        if (expectedCount == 0) {
            Assert.assertFalse(sut.hasMissingTrades());
        } else {
            Assert.assertTrue(sut.hasMissingTrades());
        }
    }

}
