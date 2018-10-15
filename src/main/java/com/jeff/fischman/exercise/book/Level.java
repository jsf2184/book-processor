package com.jeff.fischman.exercise.book;

// This Level calls holds one level of the book. I.E - It holds the OrderDetails
// for orders at the same price having the same side.

import com.jeff.fischman.exercise.list.DblLinkList;
import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.*;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageConsumer;
import com.jeff.fischman.exercise.utility.StringUtility;

import java.math.BigDecimal;

public class Level {

    private Side _side;
    private BigDecimal _price;
    private DblLinkList<OrderDetails> _orders;

    public Level(Side side, BigDecimal price) {
        _side = side;
        _price = price;
        _orders = new DblLinkList<>();
    }

    public Node<OrderDetails> addNode(OrderDetails orderDetails) {
        Node<OrderDetails> node = _orders.add(orderDetails);
        return node;
    }

    public void rmvNode(Node<OrderDetails> node) {
        _orders.remove(node);
    }

    public boolean isEmpty() {
        return _orders.isEmpty();
    }

    public String format() {
        StringBuilder sb = new StringBuilder(StringUtility.formatPrice(_price));
        _orders.forEach(o -> {
            sb.append(",");
            sb.append(o.getQuantity());
        });
        return sb.toString();
    }

    public Side getSide() {
        return _side;
    }

    public BigDecimal getPrice() {
        return _price;
    }

    // If this level is tradable with the incoming order, return the price it would trade
    // at, otherwise null.
    //
    boolean tradeMatch(OrderDetails orderDetails) {
        boolean match = false;
        switch (_side) {
            case Buy:
                match = orderDetails.getSide() ==  Side.Sell &&
                        orderDetails.getPrice().compareTo(_price) <= 0;
                break;
            case Sell:
                match = orderDetails.getSide() ==  Side.Buy &&
                        orderDetails.getPrice().compareTo(_price) >= 0;
                break;
        }
        return match;
    }

    // returns the qty consumed by the traversal through the level's orders
    public long genExpectedMatchMessages(long maxQty,
                                         ExpectedMessageConsumer expectedMessageConsumer)
    {
        long remainingQty = maxQty;
        Node<OrderDetails> currentNode = _orders.getFirst();

        while (currentNode != null && remainingQty > 0) {
            OrderDetails nodeDetails = currentNode.getData();
            long nodeQty = nodeDetails.getQuantity();

            // first generate an expected trade msg.
            long tradeQty = Math.min(remainingQty, nodeQty);
            Trade expectedTrade = new Trade(tradeQty, _price);
            expectedMessageConsumer.addTradeExpectation(expectedTrade);

            // Now, generate an order that will reflect changes to this order that was in our list.
            // Ultimately this expected order will be either a 'Modify' or a 'Remove' depending on
            // whether it is completely consumed.
            //
            long remainingNodeQty = nodeQty-tradeQty;

            Order changedOrder;
            if (remainingNodeQty > 0) {
                changedOrder = new Order(Action.Modify,
                                         nodeDetails.cloneAndModifyQty(remainingNodeQty));
            } else {
                changedOrder = new Order(Action.Remove, nodeDetails);
            }
            expectedMessageConsumer.addChangedOrderExpectation(changedOrder);

            // Now reduce the input qty by the amount of the trade
            remainingQty -= tradeQty;
            // Advance to the next node
            currentNode = currentNode.getNext();
        }
        long res = maxQty - remainingQty;
        return res;
    }
}
