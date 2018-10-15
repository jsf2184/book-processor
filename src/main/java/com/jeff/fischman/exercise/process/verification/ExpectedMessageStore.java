package com.jeff.fischman.exercise.process.verification;

import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.Trade;

import java.util.*;
import java.util.stream.Stream;

// Whenever our Book determines that an incoming order should result in a trade, this
// creates expectations of imminent trades, and canceled/modified orders. We store
// these expected messages in this class to we can later check to see if they are actually
// received.
//
public class ExpectedMessageStore implements ExpectedMessageConsumer {
    private Map<Long, Order> _expectedOrders;
    private LinkedList<Trade> _expectedTrades;

    public ExpectedMessageStore() {
        _expectedOrders = new HashMap<>();
        _expectedTrades = new LinkedList<>();
    }

    public void addTradeExpectation(Trade trade) {
        _expectedTrades.add(trade);
    }

    public void addChangedOrderExpectation(Order order) {
        _expectedOrders.put(order.getOrderid(), order);
    }

    // When we get a trade in, see if it was one we expected. If so, our expectation
    // is met so we can removed it from our list of unmatched trade expectations.
    //
    public boolean onActualTrade(Trade trade) {
        Iterator<Trade> iterator = _expectedTrades.iterator();
        while(iterator.hasNext()) {
            Trade entry = iterator.next();
            if (entry.equals(trade)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    // When we get a order in, see if it was one we expected. If so, our expectation
    // is met so we can removed it from our map of unmatched ordered expectations.
    //
    public void onActualChangedOrder(Order order) {
        // Note that given how orders are added to the ExpectedMessageStore, we really don't
        // expect more than order Order for a given orderId. That's because they are added
        // when there is a perceived trade match and the result of that match will be
        // either an expected remove or modify but not both.
        //
        Long orderid = order.getOrderid();
        Order entry = _expectedOrders.get(orderid);
        if (entry == null || !entry.equals(order)) {
            return;
        }
        // Since we got in the exact order we expected, we can remove it.
        _expectedOrders.remove(orderid);
    }

    public boolean hasMissingTrades() {
        return !_expectedTrades.isEmpty();
    }

    public boolean hasMissingChangedOrders() {
        return !_expectedOrders.isEmpty();
    }

    public void clearMissingMessages() {
        _expectedOrders.clear();
        _expectedTrades.clear();
    }

    public int getMissingTradeCount() {
        return  _expectedTrades.size();
    }

    public int getMissingChangedOrderCount() {
        return  _expectedOrders.size();
    }

    public Stream<Order> getMissingChangedOrderStream() {
        ArrayList<Order> expectedOrderList = new ArrayList<>(_expectedOrders.values());
        return expectedOrderList.stream();
    }


}
