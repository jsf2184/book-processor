package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.Action;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;

public class ModifyOrderProcessor {

    private boolean _complexModificationsAllowed;
    private ErrorCounts _errorCounts;
    private ExpectedMessageStore _expectedMessageStore;
    private OrderMap _orderMap;
    private RemoveOrderProcessor _removeOrderProcessor;
    private AddOrderProcessor _addOrderProcessor;

    public ModifyOrderProcessor(boolean complexModificationsAllowed,
                                ErrorCounts errorCounts,
                                ExpectedMessageStore expectedMessageStore,
                                OrderMap orderMap,
                                RemoveOrderProcessor removeOrderProcessor,
                                AddOrderProcessor addOrderProcessor)
    {
        _complexModificationsAllowed = complexModificationsAllowed;
        _errorCounts = errorCounts;
        _expectedMessageStore = expectedMessageStore;
        _orderMap = orderMap;
        _removeOrderProcessor = removeOrderProcessor;
        _addOrderProcessor = addOrderProcessor;
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
        if (oldDetails.isSimpleModification(orderDetails)) {
            // Simply replace the details in its existing location
            node.setData(orderDetails);

            // Record the fact that we got this modified order so if there was an expectation
            // it can be marked as met.
            //
            if (_expectedMessageStore.hasMissingChangedOrders()) {
                _expectedMessageStore.onActualChangedOrder(order);
            }
        } else if (_complexModificationsAllowed){
            // This modification is more significant. It involves more that just a qty change.
            // It means that either the Side changed or the price changed. In either of these
            // situations, we need to remove the old order from its current location and
            // re-insert it as a new order.
            //
            Order removalOrder = new Order(Action.Remove, oldDetails);
            _removeOrderProcessor.process(removalOrder);

            // And now create a new order to add in at the new location.
            Order addOrder = new Order(Action.Add, orderDetails);
            _addOrderProcessor.process(addOrder);

        } else {
            // This was a major change to the order (i.e. price or side changed) and we are running in a mode that
            // forbids this type of order change.
            //
            _errorCounts.inc(ErrorType.IllegalModificationChanges);
        }
    }
}
