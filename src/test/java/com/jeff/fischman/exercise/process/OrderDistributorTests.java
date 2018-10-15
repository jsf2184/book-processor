package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.messages.Action;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.Side;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OrderDistributorTests {
    private AddOrderProcessor _addOrderProcessor;
    private RemoveOrderProcessor _removeOrderProcessor;
    private ModifyOrderProcessor _modifyOrderProcessor;

    @Test
    public void testCanDistributeAdd() {
        OrderDistributor sut = createSut();
        Order order = new Order(Action.Add, 1L, Side.Buy, 10L, new BigDecimal(10));
        sut.routeToProcessor(order);
        verify(_addOrderProcessor, times(1)).process(order);
        verify(_removeOrderProcessor, times(0)).process(order);
        verify(_modifyOrderProcessor, times(0)).process(order);
    }
    @Test
    public void testCanDistributeRemove() {
        OrderDistributor sut = createSut();
        Order order = new Order(Action.Remove, 1L, Side.Buy, 10L, new BigDecimal(10));
        sut.routeToProcessor(order);
        verify(_addOrderProcessor, times(0)).process(order);
        verify(_removeOrderProcessor, times(1)).process(order);
        verify(_modifyOrderProcessor, times(0)).process(order);
    }

    @Test
    public void testCanDistributeModify() {
        OrderDistributor sut = createSut();
        Order order = new Order(Action.Modify, 1L, Side.Buy, 10L, new BigDecimal(10));
        sut.routeToProcessor(order);
        verify(_addOrderProcessor, times(0)).process(order);
        verify(_removeOrderProcessor, times(0)).process(order);
        verify(_modifyOrderProcessor, times(1)).process(order);
    }

    private OrderDistributor createSut() {
        // Mock all 3 dependencies
        _addOrderProcessor = mock(AddOrderProcessor.class);
        _removeOrderProcessor = mock(RemoveOrderProcessor.class);
        _modifyOrderProcessor = mock(ModifyOrderProcessor.class);
        OrderDistributor sut = new OrderDistributor(_addOrderProcessor,
                                                    _removeOrderProcessor,
                                                    _modifyOrderProcessor);
        return sut;
    }


}
