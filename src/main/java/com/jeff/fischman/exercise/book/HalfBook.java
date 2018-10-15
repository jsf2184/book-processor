package com.jeff.fischman.exercise.book;

import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.Action;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.messages.Side;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageConsumer;
import com.jeff.fischman.exercise.utility.StringUtility;

import java.math.BigDecimal;
import java.util.*;

public class HalfBook {
    private Side _side;
    private TreeMap<BigDecimal, Level> _levels;

    public HalfBook(Side side) {
        _side = side;
        _levels = new TreeMap<>();
    }


    public Node<OrderDetails> addOrder(OrderDetails  orderDetails) {
        // Find or add a new level
        Level level = _levels.computeIfAbsent(orderDetails.getPrice(), p -> new Level(_side, p));
        Node<OrderDetails> res = level.addNode(orderDetails);
        return res;
    }

    public void rmvOrder(Node<OrderDetails> node) {
        OrderDetails orderDetails = node.getData();
        BigDecimal price = orderDetails.getPrice();
        Level level = _levels.get(price);
        if (level == null) {
            throw new RuntimeException(String.format("Could not find level for price: %s",
                                                     StringUtility.formatPrice(price)));
        }
        level.rmvNode(node);
        if (level.isEmpty()) {
            // we should remove the level since it has no more orders.
            _levels.remove(price);
        }
    }

    public boolean isEmpty() {
        return  _levels.isEmpty();
    }

    public Level getBest() {
        if (_levels.isEmpty()) {
            return null;
        }
        Map.Entry<BigDecimal, Level> bestEntry;
        if (_side == Side.Buy) {
            bestEntry = _levels.lastEntry();
        } else {
            bestEntry = _levels.firstEntry();
        }
        return bestEntry.getValue();
    }

    public void genExpectedMatchMessages(OrderDetails orderDetails,
                                         ExpectedMessageConsumer expectedMessageConsumer)
    {
        NavigableSet<BigDecimal> keySet;
        if (_side == Side.Buy) {
            keySet = _levels.descendingKeySet();
        } else {
            keySet = _levels.navigableKeySet();
        }
        long startingQty = orderDetails.getQuantity();
        long remainingQty = startingQty;
        Iterator<BigDecimal> iterator = keySet.iterator();

        // Loop thru our levels looking for matches and trying to generate
        //    expected trades
        //    modified orders
        //    cancelled orders
        //
        while (remainingQty > 0 && iterator.hasNext()) {
            BigDecimal price = iterator.next();
            Level level = _levels.get(price);
            if (!level.tradeMatch(orderDetails)) {
                break;
            }
            remainingQty -= level.genExpectedMatchMessages(remainingQty, expectedMessageConsumer);
        }

        if (startingQty > remainingQty) {
            // We consumed some or all of the order's quantity by comparing it to our levels.
            Order changedOrder;
            if (remainingQty == 0) {
                // generate one more expected cancel
                changedOrder = new Order(Action.Remove, orderDetails);
            } else {
                changedOrder = new Order(Action.Modify, orderDetails.cloneAndModifyQty(remainingQty));
            }
            expectedMessageConsumer.addChangedOrderExpectation(changedOrder);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(_side.getReportLabel() + ":\n");
        NavigableSet<BigDecimal> keySet = _levels.descendingKeySet();
        for (BigDecimal price : keySet) {
            Level level = _levels.get(price);
            sb.append(level.format());
            sb.append("\n");
        }

//        _levels.values().forEach(l -> {
//            sb.append(l.format());
//            sb.append("\n");
//        });
        return sb.toString();
    }
}
