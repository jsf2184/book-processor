package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.book.Book;
import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.Action;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.messages.Side;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class RemoveOrderProcessorTests {
    private ErrorCounts _errorCounts;
    private Book _book;
    private OrderMap _orderMap;
    private ExpectedMessageStore _expectedMessageStore;

    @Test
    public void testUnknownOrderIdError() {
        RemoveOrderProcessor sut = createSut();
        OrderDetails orderDetails = new OrderDetails(1L, Side.Buy, 10L, new BigDecimal(3));
        Order rmvOrder = new Order(Action.Remove, orderDetails);
        sut.process(rmvOrder);
        Assert.assertEquals(1, _errorCounts.getCount(ErrorType.UnkownRemoveOrderId));
        Assert.assertEquals(1,  _errorCounts.getTotalErrorCount());
    }

    @Test
    public void testCasesWhereRemovalSucceeds() {

        // 4 different tests which are permutations of...
        //   whether the order Qty Details match (true and false)
        //   whether the expectedMessageStore hasMissingChangedOrders to reconcile against (true,false)
        //

        // Qtys match and hasMissingChangedOrders to reconcile against = true
        goodOrderIdRemovalTest(10L, 10L, true);
        // Qtys match and hasMissingChangedOrders to reconcile against = false
        goodOrderIdRemovalTest(10L, 10L, false);
        // Qtys dont match and hasMissingChangedOrders to reconcile against = true
        goodOrderIdRemovalTest(10L, 11L, true);
        // Qtys dont match and hasMissingChangedOrders to reconcile against = false
        goodOrderIdRemovalTest(10L, 11L, false);


    }

    // Utility method helps us test various permutations of a successful removal.
    private void goodOrderIdRemovalTest(long priorQty, long rmvQty, boolean hasMissingChangedOrders) {
        RemoveOrderProcessor sut = createSut();
        OrderDetails priorDetails = new OrderDetails(1L, Side.Buy, priorQty, new BigDecimal(3));
        Node<OrderDetails> priorOrderNode = new Node<>(priorDetails);
        _orderMap.put(1L, priorOrderNode);

        // Have our remove have the wrong size relative to the prior order.
        OrderDetails rmvDetails = new OrderDetails(1L, Side.Buy, rmvQty, new BigDecimal(3));
        Order rmvOrder = new Order(Action.Remove, rmvDetails);

        // instruct our _expectedMessageStore mock what to return for its
        // hasMissingChangedOrders call.
        when(_expectedMessageStore.hasMissingChangedOrders()).thenReturn(hasMissingChangedOrders);

        sut.process(rmvOrder);

        int expectedErrorCount = priorQty == rmvQty ? 0 : 1;
        // Check that the error is recorded
        Assert.assertEquals(expectedErrorCount, _errorCounts.getCount(ErrorType.BadRemovalDetails));
        Assert.assertEquals(expectedErrorCount, _errorCounts.getTotalErrorCount());

        // Verify that the removal took place.
        verify(_book, times(1)).rmvOrder(priorOrderNode);
        // Check that the orderId has been removed from the orderMap
        Assert.assertNull(_orderMap.get(1L));

        // Verify that expectedMessageStore was asked if it hasMissingChangedOrders
        verify(_expectedMessageStore, times(1)).hasMissingChangedOrders();

        // verify expecteMessageStore.onActualChangedOrder() called only if it
        // was told to have missingChangedOrders
        //
        int expectedOnChangedOrderCalls = hasMissingChangedOrders ? 1 : 0;
        verify(_expectedMessageStore, times(expectedOnChangedOrderCalls)).onActualChangedOrder(rmvOrder);


    }

    private RemoveOrderProcessor createSut() {
        _errorCounts = new ErrorCounts();
        _book = mock(Book.class);
        _expectedMessageStore = mock(ExpectedMessageStore.class);
        _orderMap = new OrderMap();
        RemoveOrderProcessor res = new RemoveOrderProcessor(_errorCounts,
                                                      _expectedMessageStore,
                                                      _book,
                                                      _orderMap);
        return res;
    }

}
