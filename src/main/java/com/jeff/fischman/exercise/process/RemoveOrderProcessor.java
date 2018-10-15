package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.book.Book;
import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;

public class RemoveOrderProcessor {

    private ErrorCounts _errorCounts;
    private ExpectedMessageStore _expectedMessageStore;
    private Book _book;
    private OrderMap _orderMap;

    public RemoveOrderProcessor(ErrorCounts errorCounts,
                                ExpectedMessageStore expectedMessageStore,
                                Book book,
                                OrderMap orderMap)
    {
        _errorCounts = errorCounts;
        _expectedMessageStore = expectedMessageStore;
        _book = book;
        _orderMap = orderMap;
    }

    public void process(Order order) {
        OrderDetails orderDetails = order.getOrderDetails();
        long orderid = orderDetails.getOrderid();
        Node<OrderDetails> node = _orderMap.get(orderid);
        if (node == null) {
            _errorCounts.inc(ErrorType.UnkownRemoveOrderId);
            return;
        }
        OrderDetails oldDetails = node.getData();
        if (!oldDetails.equals(orderDetails)) {
            _errorCounts.inc(ErrorType.BadRemovalDetails);
        }
        // Regardless of whether the details are right, go ahead with the removal
        _book.rmvOrder(node);
        _orderMap.remove(orderid);

        // Record the fact that we got this removed order so if there was an expectation
        // it can be marked as met.
        //
        if (_expectedMessageStore.hasMissingChangedOrders()) {
            _expectedMessageStore.onActualChangedOrder(order);
        }
    }

}
