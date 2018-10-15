package com.jeff.fischman.exercise.messages;

import java.math.BigDecimal;
import java.util.Objects;

public class Trade implements Message {
    private long _quantity;
    private BigDecimal _price;


    public Trade(long quantity, BigDecimal price) {
        _quantity = quantity;
        _price = price;
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.Trade;
    }

    public long getQuantity() {
        return _quantity;
    }

    public BigDecimal getPrice() {
        return _price;
    }

    @Override
    public void invokeHandlerMethod(MessageHandler messageHandler) {
        messageHandler.onTrade(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return Objects.equals(_quantity, trade._quantity) &&
                Objects.equals(_price, trade._price);
    }

}
