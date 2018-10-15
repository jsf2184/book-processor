package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.OrderDetails;

import java.util.HashMap;
import java.util.Map;

// A simple wrapper around a HashMap that is useful because it is easier to
// mock for testing purposes.
//
public class OrderMap {
    private Map<Long, Node<OrderDetails>> _map;

    public OrderMap() {
        _map = new HashMap<>();
    }

    public Node<OrderDetails> get(long orderId) {
        Node<OrderDetails> res = _map.get(orderId);
        return res;
    }

    public void put(long orderId, Node<OrderDetails> node) {
        _map.put(orderId, node);
    }

    public Node<OrderDetails>  remove(long orderId) {
        Node<OrderDetails> res = _map.remove(orderId);
        return res;
    }

}
