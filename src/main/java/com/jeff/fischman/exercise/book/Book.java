package com.jeff.fischman.exercise.book;

import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.messages.Side;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageConsumer;
import com.jeff.fischman.exercise.utility.StringUtility;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Book {
    public static final String NanString = "NAN\n";
    private final ExpectedMessageConsumer _expectedMessageConsumer;
    private HalfBook[] _halfBooks;

    public Book(ExpectedMessageConsumer expectedMessageConsumer) {
        this(expectedMessageConsumer, new HalfBook(Side.Buy), new HalfBook(Side.Sell));
    }

    @SuppressWarnings("WeakerAccess")
    public Book(ExpectedMessageConsumer expectedMessageConsumer,
                HalfBook buyHalf,
                HalfBook sellHalf)
    {
        _expectedMessageConsumer = expectedMessageConsumer;
        _halfBooks = new HalfBook[2];
        _halfBooks[Side.Buy.ordinal()] = buyHalf;
        _halfBooks[Side.Sell.ordinal()] = sellHalf;
    }
    
    public Node<OrderDetails> add(OrderDetails orderDetails) {
        Side side = orderDetails.getSide();
        HalfBook halfBook = _halfBooks[side.ordinal()];
        Node<OrderDetails> node = halfBook.addOrder(orderDetails);

        // When a new order is entered on one 'side' it may create a match with the other side.
        HalfBook otherHalfBook = _halfBooks[side.getOpposite().ordinal()];
        otherHalfBook.genExpectedMatchMessages(orderDetails, _expectedMessageConsumer);
        return node;
    }

    public void rmvOrder(Node<OrderDetails> node) {
        Side side = node.getData().getSide();
        HalfBook halfBook = _halfBooks[side.ordinal()];
        halfBook.rmvOrder(node);
    }


    public String getMidquoteString() {
        Level bestBuyLevel = _halfBooks[Side.Buy.ordinal()].getBest();
        Level bestSellLevel = _halfBooks[Side.Sell.ordinal()].getBest();

        if (bestBuyLevel == null || bestSellLevel == null) {
            return NanString;
        }
        BigDecimal sum = bestBuyLevel.getPrice().add(bestSellLevel.getPrice());
        BigDecimal avg = sum.divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
        String res = StringUtility.formatPrice(avg) + "\n";
        return res;
    }

    public String toString() {
        String res = _halfBooks[Side.Sell.ordinal()].toString() + _halfBooks[Side.Buy.ordinal()].toString();
        return res;
    }
}
