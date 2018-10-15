package com.jeff.fischman.exercise.book;

import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.*;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageConsumer;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class HalfBookTests {

    private int _orderId;
    private Side _side;

    private  void setup(Side side) {
        _orderId = 0;
        _side = side;
    }
    @Test
    public void testScenarioForBuySide() {
        setup(Side.Buy);
        HalfBook sut = new HalfBook(_side);
        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getBest());

        // Add an order at price 9 with qty 5
        Node<OrderDetails> node_9_5 = sut.addOrder(createOrderDetails("9", 5));
        Assert.assertEquals("BUYS:\n9,5\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("9"), sut.getBest().getPrice());

        // Add an order at price 10 with qty 3
        Node<OrderDetails> node_10_3 = sut.addOrder(createOrderDetails("10", 3));
        Assert.assertEquals("BUYS:\n10,3\n9,5\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("10"), sut.getBest().getPrice());

        // Add an order at price 9 with qty 2
        Node<OrderDetails> node_9_2 = sut.addOrder(createOrderDetails("9", 2));
        Assert.assertEquals("BUYS:\n10,3\n9,5,2\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("10"), sut.getBest().getPrice());

        // Now remove the 9,5 order
        sut.rmvOrder(node_9_5);
        Assert.assertEquals("BUYS:\n10,3\n9,2\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("10"), sut.getBest().getPrice());

        // Now remove the 10,3 order
        sut.rmvOrder(node_10_3);
        Assert.assertEquals("BUYS:\n9,2\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("9"), sut.getBest().getPrice());

        // Now remove the 9,2 order leaving us empty
        sut.rmvOrder(node_9_2);
        Assert.assertEquals("BUYS:\n", sut.toString());
        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getBest());
    }

    @Test
    public void testScenarioForSellSide() {
        setup(Side.Sell);
        HalfBook sut = new HalfBook(_side);
        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getBest());

        // Add an order at price 11 with qty 5
        Node<OrderDetails> node_11_5 = sut.addOrder(createOrderDetails("11", 5));
        Assert.assertEquals("SELLS:\n11,5\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("11"), sut.getBest().getPrice());

        // Add an order at price 10 with qty 3
        Node<OrderDetails> node_10_3 = sut.addOrder(createOrderDetails("10", 3));
        Assert.assertEquals("SELLS:\n11,5\n10,3\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("10"), sut.getBest().getPrice());

        // Add an order at price 11 with qty 2
        Node<OrderDetails> node_11_2 = sut.addOrder(createOrderDetails("11", 2));
        Assert.assertEquals("SELLS:\n11,5,2\n10,3\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("10"), sut.getBest().getPrice());

        // Now remove the 11,5 order
        sut.rmvOrder(node_11_5);
        Assert.assertEquals("SELLS:\n11,2\n10,3\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("10"), sut.getBest().getPrice());

        // Now remove the 10,3 order
        sut.rmvOrder(node_10_3);
        Assert.assertEquals("SELLS:\n11,2\n", sut.toString());
        Assert.assertEquals(new  BigDecimal("11"), sut.getBest().getPrice());

        // Now remove the 11,2 order leaving us empty
        sut.rmvOrder(node_11_2);
        Assert.assertEquals("SELLS:\n", sut.toString());
        Assert.assertTrue(sut.isEmpty());
        Assert.assertNull(sut.getBest());
    }

    @Test
    public void testNewBuyOrderProperlyConsumesSellsOnMatch() {
        // First build a SellSide HalfBook
        HalfBook sellHalf = new HalfBook(Side.Sell);
        // Build out the sellHalf like this
        // SELLS
        // 12,2,3
        // 11,2,3
        OrderDetails o1Details = createOrderDetails("11", 2, Side.Sell);
        OrderDetails o2Details = createOrderDetails("11", 3, Side.Sell);
        OrderDetails o3Details = createOrderDetails("12", 2, Side.Sell);
        OrderDetails o4Details = createOrderDetails("12", 3, Side.Sell);

        sellHalf.addOrder(o1Details);
        sellHalf.addOrder(o2Details);
        sellHalf.addOrder(o3Details);
        sellHalf.addOrder(o4Details);

        ExpectedMessageConsumer ems = mock(ExpectedMessageConsumer.class);

        // Now suppose a buy order comes in at 12, 6
        OrderDetails buyDetails = createOrderDetails("12", 6, Side.Buy);

        // Now give the sellHalf an opportunity to match on this Buy. It will pass its finding onto ems
        sellHalf.genExpectedMatchMessages(buyDetails, ems);

        // Now lets see if the sellHalf told ems what we'd expect it too.
        Order rmvOrder1 = new Order(Action.Remove, o1Details);
        Order rmvOrder2 = new Order(Action.Remove, o2Details);
        Order modOrder3 = new Order(Action.Modify, o3Details.cloneAndModifyQty(1));

        verify(ems,times(1)).addChangedOrderExpectation(rmvOrder1);
        verify(ems,times(1)).addChangedOrderExpectation(rmvOrder2);
        verify(ems,times(1)).addChangedOrderExpectation(modOrder3);

        // There are some trade expectations that would have been created too.
        verify(ems, times(1)).addTradeExpectation(createTrade("11", 2));
        verify(ems, times(1)).addTradeExpectation(createTrade("11", 3));
        verify(ems, times(1)).addTradeExpectation(createTrade("12", 1));

        // Finally, the Buy Order that was consumed by the trade results in a generated
        // remove order
        //
        Order removedBuy = new Order(Action.Remove, buyDetails);
        verify(ems,times(1)).addChangedOrderExpectation(removedBuy);

        verifyNoMoreInteractions(ems);
    }

    @Test
    public void testNewSellOrderProperlyConsumesBuysOnMatch() {
        // First build a BuySide HalfBook
        HalfBook buyHalf = new HalfBook(Side.Buy);
        // Build out the buyHalf like this
        // BUYS
        // 12,2,3
        // 11,2,3
        OrderDetails o1Details = createOrderDetails("12", 2, Side.Buy);
        OrderDetails o2Details = createOrderDetails("12", 3, Side.Buy);
        OrderDetails o3Details = createOrderDetails("11", 2, Side.Buy);
        OrderDetails o4Details = createOrderDetails("11", 3, Side.Buy);

        buyHalf.addOrder(o1Details);
        buyHalf.addOrder(o2Details);
        buyHalf.addOrder(o3Details);
        buyHalf.addOrder(o4Details);

        ExpectedMessageConsumer ems = mock(ExpectedMessageConsumer.class);

        // Now suppose a sell order comes in at 11, 6
        OrderDetails sellDetails = createOrderDetails("11", 6, Side.Sell);

        // Now give the buyHalf an opportunity to match on this Sell. It will pass its finding onto ems
        buyHalf.genExpectedMatchMessages(sellDetails, ems);

        // Now lets see if the buyHalf told ems what we'd expect it too.
        Order rmvOrder1 = new Order(Action.Remove, o1Details);
        Order rmvOrder2 = new Order(Action.Remove, o2Details);
        Order modOrder3 = new Order(Action.Modify, o3Details.cloneAndModifyQty(1));

        verify(ems,times(1)).addChangedOrderExpectation(rmvOrder1);
        verify(ems,times(1)).addChangedOrderExpectation(rmvOrder2);
        verify(ems,times(1)).addChangedOrderExpectation(modOrder3);

        // There are some trade expectations that would have been created too.
        verify(ems, times(1)).addTradeExpectation(createTrade("12", 2));
        verify(ems, times(1)).addTradeExpectation(createTrade("12", 3));
        verify(ems, times(1)).addTradeExpectation(createTrade("11", 1));

        // Finally, the Sell Order that was consumed by the trade results in a generated
        // remove order
        //
        Order removedSell = new Order(Action.Remove, sellDetails);
        verify(ems,times(1)).addChangedOrderExpectation(removedSell);

        verifyNoMoreInteractions(ems);
    }


    private Trade createTrade(String pricStr, long qty) {
        Trade res = new Trade(qty, new BigDecimal(pricStr));
        return res;
    }
    private OrderDetails createOrderDetails(String priceStr, long qty) {
        OrderDetails res = createOrderDetails(priceStr, qty, _side);
        return res;
    }

    private OrderDetails createOrderDetails(String priceStr, long qty, Side side) {
        OrderDetails res = new OrderDetails(++_orderId, side, qty, new BigDecimal(priceStr));
        return res;
    }


}
