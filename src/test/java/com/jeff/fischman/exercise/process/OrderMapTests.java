package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.messages.Side;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class OrderMapTests {
    @Test
    public void testOrderThatWasPutCanBeRetrievedByOrderId() {
        OrderMap sut = new OrderMap();
        OrderDetails orderDetails = new OrderDetails(7L, Side.Buy, 10L, new BigDecimal(99));
        Node<OrderDetails> node = new Node<>(orderDetails);
        sut.put(orderDetails.getOrderid(), node);
        Assert.assertSame(node, sut.get(7L));
        Assert.assertSame(orderDetails, sut.get(7L).getData());
    }

    @Test
    public void testUnknownOrderIdCannotBeRetrievedByOrderId() {
        OrderMap sut = new OrderMap();
        Assert.assertNull(sut.get(7L));
    }

}
