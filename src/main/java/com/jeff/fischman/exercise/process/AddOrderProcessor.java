package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.book.Book;
import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;
import com.jeff.fischman.exercise.process.verification.MissingMessageChecker;

public class AddOrderProcessor {
    private ErrorCounts _errorCounts;
    private MissingMessageChecker _missingMessageChecker;
    private Book _book;
    private OrderMap _orderMap;

    public AddOrderProcessor(ErrorCounts errorCounts,
                             Book book,
                             OrderMap orderMap)
    {
        _errorCounts = errorCounts;
        _book = book;
        _orderMap = orderMap;
    }

    public void setMissingMessageChecker(MissingMessageChecker missingMessageChecker) {
        _missingMessageChecker = missingMessageChecker;
    }

    public  void process(Order order) {
        OrderDetails orderDetails = order.getOrderDetails();
        long orderid = orderDetails.getOrderid();
        Node<OrderDetails> node = _orderMap.get(orderid);
        if (node != null) {
            _errorCounts.inc(ErrorType.DuplicatedOrderId);
            return;
        }

        // Whenever we get an add, we want to make sure that we hadn't first missed any trades, removes, or
        // modifies the need for which may have been detected on a previous add. Lets check for that here
        // with our _missingMessageChecker
        //
        _missingMessageChecker.checkForMissedMessages();

        // When the book looks at this new order, it will also check to see whether a match exists with
        // the other side of the book. If so, it will add trade(s) and modified or removed orders
        // to our _expectedMessageStore.
        //
        node = _book.add(orderDetails);
        _orderMap.put(orderid, node);
    }

}
