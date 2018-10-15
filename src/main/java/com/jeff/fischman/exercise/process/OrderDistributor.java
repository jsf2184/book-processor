package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.messages.Order;

public class OrderDistributor {
    private AddOrderProcessor _addOrderProcessor;
    private RemoveOrderProcessor _removeOrderProcessor;
    private ModifyOrderProcessor _modifyOrderProcessor;

    public OrderDistributor(AddOrderProcessor addOrderProcessor,
                            RemoveOrderProcessor removeOrderProcessor,
                            ModifyOrderProcessor modifyOrderProcessor)
    {
        _addOrderProcessor = addOrderProcessor;
        _removeOrderProcessor = removeOrderProcessor;
        _modifyOrderProcessor = modifyOrderProcessor;
    }

    public void routeToProcessor(Order order) {
        // Delegate the order to the proper processor.
        switch (order.getAction()) {
            case Add:
                _addOrderProcessor.process(order);
                break;
            case Remove:
                _removeOrderProcessor.process(order);
                break;
            case Modify:
                _modifyOrderProcessor.process(order);
                break;
        }
    }


}
