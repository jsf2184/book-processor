package com.jeff.fischman.exercise.process.verification;

import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.Trade;

public interface ExpectedMessageConsumer {
    void addTradeExpectation(Trade trade);
    void addChangedOrderExpectation(Order order);
}
