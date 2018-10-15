package com.jeff.fischman.exercise.messages;

import java.math.BigDecimal;
import java.util.Objects;

@SuppressWarnings("unused")
public class OrderDetails  {

    private long _orderid;
    private Side _side;
    private long _quantity;
    private BigDecimal _price;


    public OrderDetails(long orderid,
                        Side side,
                        long quantity,
                        BigDecimal price)
    {
        _orderid = orderid;
        _side = side;
        _quantity = quantity;
        _price = price;
    }

    public OrderDetails cloneAndModifyQty(long modifiedQty) {
        OrderDetails res = new OrderDetails(_orderid,
                                            _side,
                                            modifiedQty,
                                            _price);
        return res;
    }

    public long getOrderid() {
        return _orderid;
    }

    public Side getSide() {
        return _side;
    }

    public long getQuantity() {
        return _quantity;
    }

    public BigDecimal getPrice() {
        return _price;
    }

    public long decQuantity(long delta) {
        _quantity -= delta;
        return _quantity;
    }

    public static long getMinQty(OrderDetails o1, OrderDetails o2) {
        return Math.min(o1.getQuantity(), o2.getQuantity());
    }

    public boolean isSimpleModification(OrderDetails other) {
        boolean res = _orderid == other._orderid &&
                      _price.equals(other._price) &&
                      _side == other._side;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetails that = (OrderDetails) o;
        return _orderid == that._orderid &&
                _quantity == that._quantity &&
                _side == that._side &&
                Objects.equals(_price, that._price);
    }

}
