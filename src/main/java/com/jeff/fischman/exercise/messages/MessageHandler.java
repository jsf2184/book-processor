package com.jeff.fischman.exercise.messages;

public interface MessageHandler {
    void onTrade(Trade trade);
    void onOrder(Order order);

}
