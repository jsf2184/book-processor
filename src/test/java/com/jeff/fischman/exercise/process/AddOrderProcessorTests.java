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
import com.jeff.fischman.exercise.process.verification.MissingMessageChecker;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class AddOrderProcessorTests {
    private ErrorCounts _errorCounts;
    private Book _book;
    private OrderMap _orderMap;
    private MissingMessageChecker _missingMessageChecker;

    @Test
    public void testDuplicateOrderResultsInError()
    {
        AddOrderProcessor sut = createSut();
        // Put an existing/prior order in our _orderMap
        OrderDetails orderDetails = new OrderDetails(1L, Side.Buy, 10L, new BigDecimal(3));
        Node<OrderDetails> priorOrderNode = new Node<>(orderDetails);
        _orderMap.put(1L, priorOrderNode);

        Order newOrder = new Order(Action.Add, orderDetails);
        sut.process(newOrder);
        // Validate expected DuplicatedOrderId error
        Assert.assertEquals(1,  _errorCounts.getCount(ErrorType.DuplicatedOrderId));
        // Validate that is the only error
        Assert.assertEquals(1,  _errorCounts.getTotalErrorCount());
    }

    @Test
    public void testGoodOrderIsPropagatedToDependencies() {
        // This time, no existing order to contend with.
        AddOrderProcessor sut = createSut();
        OrderDetails orderDetails = new OrderDetails(1L, Side.Buy, 10L, new BigDecimal(3));
        Order newOrder = new Order(Action.Add, orderDetails);
        Node<OrderDetails> orderDetailsNode = new Node<>(orderDetails);

        when(_book.add(orderDetails)).thenReturn(orderDetailsNode);
        sut.process(newOrder);
        // should be zero errors
        Assert.assertEquals(0,  _errorCounts.getTotalErrorCount());

        // Validate dependencies utilized as expected.
        verify(_missingMessageChecker, times(1)).checkForMissedMessages();
        verify(_book, times(1)).add(orderDetails);

        // And make sure that the order was put into the map.
        Assert.assertSame(orderDetailsNode, _orderMap.get(1L));

    }

    private AddOrderProcessor createSut() {
        _errorCounts = new ErrorCounts();
        _book = mock(Book.class);
        _orderMap = new OrderMap();
        AddOrderProcessor res = new AddOrderProcessor(_errorCounts,
                                                      _book,
                                                      _orderMap);
        _missingMessageChecker = mock(MissingMessageChecker.class);
        res.setMissingMessageChecker(_missingMessageChecker);
        return res;
    }
}
