package com.jeff.fischman.exercise.messages;

import java.math.BigDecimal;
import java.util.Objects;

@SuppressWarnings("unused")
public class Order implements Message {

    private Action _action;
    private OrderDetails _orderDetails;

    @Override
    public MsgType getMsgType() {
        return MsgType.Order;
    }

    public Order(Action action,
                 long orderid,
                 Side side,
                 long quantity,
                 BigDecimal price)
    {
        this(action, new OrderDetails(orderid, side, quantity, price));
    }

    public Order(Action action,  OrderDetails orderDetails) {
        _action = action;
        _orderDetails = orderDetails;
    }

    public Action getAction() {
        return _action;
    }

    public long getOrderid() {
        return _orderDetails.getOrderid();
    }

    public Side getSide() {
        return _orderDetails.getSide();
    }

    @SuppressWarnings("WeakerAccess")
    public long getQuantity() {
        return _orderDetails.getQuantity();
    }

    public BigDecimal getPrice() {
        return _orderDetails.getPrice();
    }

    public OrderDetails getOrderDetails() {
        return _orderDetails;
    }

    @Override
    public void invokeHandlerMethod(MessageHandler messageHandler) {
        messageHandler.onOrder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return _action == order._action &&
                Objects.equals(_orderDetails, order._orderDetails);
    }

}
