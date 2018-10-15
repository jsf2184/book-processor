package com.jeff.fischman.exercise.messages;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class OrderDetailsTests {
    @Test
    public void testCloneAndModifyQty() {
        OrderDetails original = new OrderDetails(1L, Side.Buy, 20L, new BigDecimal("10.5"));
        OrderDetails modifiedDetails = original.cloneAndModifyQty(18L);
        Assert.assertEquals(new OrderDetails(1L, Side.Buy, 18L, new BigDecimal("10.5")),
                            modifiedDetails);

    }
}
