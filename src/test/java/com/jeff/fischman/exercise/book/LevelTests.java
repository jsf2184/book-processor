package com.jeff.fischman.exercise.book;

import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.*;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageConsumer;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LevelTests {

    private static final  BigDecimal price = new BigDecimal(10.5);
    private static final Side side = Side.Buy;
    private long _orderId = 0;

    @Test
    public void testAddRemoveScenario() {
        Level sut = new Level(side, price);
        Assert.assertTrue(sut.isEmpty());

        // Add in quantities 4, 1, 3 in that order
        Node<OrderDetails> node4 = addAndValidate(sut, 4, "10.5,4");
        Node<OrderDetails> node1 = addAndValidate(sut, 1, "10.5,4,1");
        Node<OrderDetails> node3 = addAndValidate(sut, 3, "10.5,4,1,3");

        // Now remove nodes with quantities 3, 4, 1 in that order
        rmvAndValidate(sut, node3, "10.5,4,1");
        rmvAndValidate(sut, node4, "10.5,1");
        rmvAndValidate(sut, node1, "10.5");
    }

    // *******************************************************************************
    // Now some tests pertaining to TradeMatching.
    // *******************************************************************************
    @Test
    public void testTradeMatchCasesWithLevelOnBuySide() {
        // Level is on the Buy Side
        Level sut = new Level(Side.Buy, new BigDecimal("10.5"));

        // Try a SELL order with the same price. Should match.
        OrderDetails orderDetails = new OrderDetails(1L, Side.Sell, 4L, new BigDecimal("10.5"));
        Assert.assertTrue(sut.tradeMatch(orderDetails));

        // Try a SELL order with a lower price. Should match.
        orderDetails = new OrderDetails(1L, Side.Sell, 4L, new BigDecimal("10.4"));
        Assert.assertTrue(sut.tradeMatch(orderDetails));

        // Try a SELL order with a higher price. Should not match.
        orderDetails = new OrderDetails(1L, Side.Sell, 4L, new BigDecimal("10.6"));
        Assert.assertFalse(sut.tradeMatch(orderDetails));

        // Try a BUY order -- should not ever happen and should certainly not match.
        orderDetails = new OrderDetails(1L, Side.Buy, 4L, new BigDecimal("10.5"));
        Assert.assertFalse(sut.tradeMatch(orderDetails));
    }

    @Test
    public void testTradeMatchCasesWithLevelOnSellSide() {
        // Level is on the Sell Side
        Level sut = new Level(Side.Sell, new BigDecimal("10.5"));

        // Try a BUY order with the same price. Should match.
        OrderDetails orderDetails = new OrderDetails( 1L, Side.Buy, 4L, new BigDecimal("10.5"));
        Assert.assertTrue(sut.tradeMatch(orderDetails));

        // Try a BUY order with a higher price. Should match.
        orderDetails = new OrderDetails( 1L, Side.Buy, 4L, new BigDecimal("10.6"));
        Assert.assertTrue(sut.tradeMatch(orderDetails));

        // Try a BUY order with a lower price. Should not match.
        orderDetails = new OrderDetails( 1L, Side.Buy, 4L, new BigDecimal("10.4"));
        Assert.assertFalse(sut.tradeMatch(orderDetails));

        // Try a SELL order -- should not ever happen and should certainly not match.
        orderDetails = new OrderDetails( 1L, Side.Sell, 4L, new BigDecimal("10.5"));
        Assert.assertFalse(sut.tradeMatch(orderDetails));
    }

    // *******************************************************************************
    // And some tests that verify our ability to generate expected trade and order
    // messages that result from a match. For all these tests, we will start with
    // a level that has two orders on it, each with quantity 2.
    // The we will test how that level responds when confronted with various size
    // quantities ranging from size 1 to 5
    // *******************************************************************************

    private Level createAndPopLevelForGenExpectedMsgTests() {
        Level res = new Level(Side.Buy, new BigDecimal(10));
        res.addNode(new OrderDetails(1, Side.Buy, 2, new BigDecimal(10)));
        res.addNode(new OrderDetails(2, Side.Buy, 2, new BigDecimal(10)));
        return res;
    }

    @Test
    public void testGenExpectedMsgWhereIncomingOrderDoesntConsumeFirstLevelEntry() {
        Level sut = createAndPopLevelForGenExpectedMsgTests();
        ExpectedMessageConsumer messageConsumer = mock(ExpectedMessageConsumer.class);
        long consumed = sut.genExpectedMatchMessages(1, messageConsumer);
        Assert.assertEquals(1, consumed);
        // Should generate 1 trade of size 1
        verify(messageConsumer, times(1)).addTradeExpectation(new Trade(1L, new BigDecimal(10)));
        // That first order should be modified to be size 1 instead of 2.
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Modify, 1L, Side.Buy, 1L, new BigDecimal(10)));
    }
    @Test
    public void testGenExpectedMsgWhereIncomingOrderDoesConsumeFirstLevelEntry() {
        Level sut = createAndPopLevelForGenExpectedMsgTests();
        ExpectedMessageConsumer messageConsumer = mock(ExpectedMessageConsumer.class);
        long consumed = sut.genExpectedMatchMessages(2, messageConsumer);
        Assert.assertEquals(2, consumed);

        // Should generate 1 trade of size 2
        verify(messageConsumer, times(1)).addTradeExpectation(new Trade(2L, new BigDecimal(10)));
        // That first order should be removed completely
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Remove, 1L, Side.Buy, 2L, new BigDecimal(10)));
    }

    @Test
    public void testGenExpectedMsgWhereIncomingOrderConsumesFirstAndPartOfSecondEntries() {
        Level sut = createAndPopLevelForGenExpectedMsgTests();
        ExpectedMessageConsumer messageConsumer = mock(ExpectedMessageConsumer.class);
        long consumed = sut.genExpectedMatchMessages(3, messageConsumer);
        Assert.assertEquals(3, consumed);

        // Should generate 2 trades, one of size 2, one of size 1
        verify(messageConsumer, times(1)).addTradeExpectation(new Trade(2L, new BigDecimal(10)));
        verify(messageConsumer, times(1)).addTradeExpectation(new Trade(1L, new BigDecimal(10)));
        // Should generate one remove order and one modify order
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Remove, 1L, Side.Buy, 2L, new BigDecimal(10)));
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Modify, 2L, Side.Buy, 1L, new BigDecimal(10)));
    }


    @Test
    public void testGenExpectedMsgWhereIncomingOrderConsumesFirstAndSecondEntries() {
        Level sut = createAndPopLevelForGenExpectedMsgTests();
        ExpectedMessageConsumer messageConsumer = mock(ExpectedMessageConsumer.class);
        long consumed = sut.genExpectedMatchMessages(4, messageConsumer);
        Assert.assertEquals(4, consumed);

        // Should generate 2 trades each of size 2
        verify(messageConsumer, times(2)).addTradeExpectation(new Trade(2L, new BigDecimal(10)));
        // Should generate two remove orders each of size 2
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Remove, 1L, Side.Buy, 2L, new BigDecimal(10)));
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Remove, 2L, Side.Buy, 2L, new BigDecimal(10)));
    }

    @Test
    public void testGenExpectedMsgWhereIncomingOrderConsumesFirstAndSecondEntriesWithLeftover() {
        Level sut = createAndPopLevelForGenExpectedMsgTests();
        ExpectedMessageConsumer messageConsumer = mock(ExpectedMessageConsumer.class);
        long consumed = sut.genExpectedMatchMessages(5, messageConsumer);
        Assert.assertEquals(4, consumed);

        // Should generate 2 trades each of size 2
        verify(messageConsumer, times(2)).addTradeExpectation(new Trade(2L, new BigDecimal(10)));
        // Should generate two remove orders each of size 2
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Remove, 1L, Side.Buy, 2L, new BigDecimal(10)));
        verify(messageConsumer, times(1)).addChangedOrderExpectation(new Order(Action.Remove, 2L, Side.Buy, 2L, new BigDecimal(10)));
    }

    private Node<OrderDetails> addAndValidate(Level sut,
                                              long qty,
                                              String expectedFormatString)
    {
        Node<OrderDetails> res = addOrder(sut, qty);
        String actualFormatString = sut.format();
        Assert.assertEquals(expectedFormatString, actualFormatString);
        Assert.assertFalse(sut.isEmpty());
        return res;
    }

    private void rmvAndValidate(Level sut,
                        Node<OrderDetails> node,
                        String expectedFormatString)
    {
        sut.rmvNode(node);
        String[] parts = expectedFormatString.split(",");
        if (parts.length <= 1) {
            Assert.assertTrue(sut.isEmpty());
        } else {
            Assert.assertFalse(sut.isEmpty());
        }
        String actualFormatString = sut.format();
        Assert.assertEquals(expectedFormatString, actualFormatString);
    }


    private Node<OrderDetails> addOrder(Level sut, long qty) {

        OrderDetails orderDetails = new OrderDetails(++_orderId, side, qty, price);
        Node<OrderDetails> res = sut.addNode(orderDetails);
        return res;
    }
}
