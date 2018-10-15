package com.jeff.fischman.exercise.book;

import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.messages.Side;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageConsumer;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class BookTests {
    // Note there is pretty extensive testing within HalfBook so here we will
    // focus mainly on how the Book class handles the basics of properly using
    // its dependencies

    private ExpectedMessageConsumer _expectedMessageConsumer;
    private HalfBook _buyHalf;
    private HalfBook _sellHalf;

    @Test
    public void testGetMidPointStringWithNoBestBuyOrSell() {
        Book sut = createSut();
        when(_buyHalf.getBest()).thenReturn(null);
        when(_sellHalf.getBest()).thenReturn(null);
        Assert.assertEquals(Book.NanString, sut.getMidquoteString());
    }

    @Test
    public void testGetMidPointStringWithOnlyBestBuy() {
        Book sut = createSut();
        Level bestBuyLevel = mockLevel("10");
        when(_buyHalf.getBest()).thenReturn(bestBuyLevel);
        when(_sellHalf.getBest()).thenReturn(null);
        Assert.assertEquals(Book.NanString, sut.getMidquoteString());
    }

    @Test
    public void testGetMidPointStringWithOnlyBestSell() {
        Book sut = createSut();
        Level bestSellLevel = mockLevel("11");
        when(_buyHalf.getBest()).thenReturn(null);
        when(_sellHalf.getBest()).thenReturn(bestSellLevel);
        Assert.assertEquals(Book.NanString, sut.getMidquoteString());
    }

    @Test
    public void testGetMidPointStringWithGoodBestSellAndBestBuy() {
        Book sut = createSut();
        Level bestBuyLevel = mockLevel("10");
        Level bestSellLevel = mockLevel("11");
        when(_buyHalf.getBest()).thenReturn(bestBuyLevel);
        when(_sellHalf.getBest()).thenReturn(bestSellLevel);
        Assert.assertEquals("10.5\n", sut.getMidquoteString());
    }

    @Test
    public void testToStringAppendsSideStringsTogether() {
        Book sut = createSut();
        when(_sellHalf.toString()).thenReturn("SellHalf");
        when(_buyHalf.toString()).thenReturn("BuyHalf");
        Assert.assertEquals("SellHalfBuyHalf", sut.toString());
    }

    @Test
    public void testRmvBuyOrder() {
        Book sut = createSut();
        OrderDetails orderDetails = new OrderDetails(1L, Side.Buy, 3L, new BigDecimal(10));
        Node<OrderDetails> node = new Node<>(orderDetails);
        sut.rmvOrder(node);
        verify(_buyHalf, times(1)).rmvOrder(node);
        verify(_sellHalf, times(0)).rmvOrder(node);
    }

    @Test
    public void testRmvSellOrder() {
        Book sut = createSut();
        OrderDetails orderDetails = new OrderDetails(1L, Side.Sell, 3L, new BigDecimal(10));
        Node<OrderDetails> node = new Node<>(orderDetails);
        sut.rmvOrder(node);
        verify(_buyHalf, times(0)).rmvOrder(node);
        verify(_sellHalf, times(1)).rmvOrder(node);
    }

    @Test
    public void testAddSellOrder() {
        Book sut = createSut();
        OrderDetails orderDetails = new OrderDetails(1L, Side.Sell, 3L, new BigDecimal(10));

        Node<OrderDetails> expectedNode = new Node<>(orderDetails);
        when(_sellHalf.addOrder(orderDetails)).thenReturn(expectedNode);

        Node<OrderDetails> addedNode = sut.add(orderDetails);
        Assert.assertSame(expectedNode, addedNode);
        // verify sellHalf interactions
        verify(_sellHalf, times(1)).addOrder(orderDetails);
        verify(_sellHalf, times(0)).genExpectedMatchMessages(orderDetails, _expectedMessageConsumer);

        // verify bufHalf interactions
        verify(_buyHalf, times(0)).addOrder(orderDetails);
        verify(_buyHalf, times(1)).genExpectedMatchMessages(orderDetails, _expectedMessageConsumer);
    }

    @Test
    public void testAddBuyOrder() {
        Book sut = createSut();
        OrderDetails orderDetails = new OrderDetails(1L, Side.Buy, 3L, new BigDecimal(10));

        Node<OrderDetails> expectedNode = new Node<>(orderDetails);
        when(_buyHalf.addOrder(orderDetails)).thenReturn(expectedNode);

        Node<OrderDetails> addedNode = sut.add(orderDetails);
        Assert.assertSame(expectedNode, addedNode);
        // verify buyHalf interactions
        verify(_buyHalf, times(1)).addOrder(orderDetails);
        verify(_buyHalf, times(0)).genExpectedMatchMessages(orderDetails, _expectedMessageConsumer);

        // verify bufHalf interactions
        verify(_sellHalf, times(0)).addOrder(orderDetails);
        verify(_sellHalf, times(1)).genExpectedMatchMessages(orderDetails, _expectedMessageConsumer);
    }


    private Level mockLevel(String bestPrice) {
        if (bestPrice == null) {
            return null;
        }
        Level level = mock(Level.class);
        when(level.getPrice()).thenReturn(new BigDecimal(bestPrice));
        return level;
    }

    private Book createSut() {
        _buyHalf = mock(HalfBook.class);
        _sellHalf = mock(HalfBook.class);
        _expectedMessageConsumer = mock(ExpectedMessageConsumer.class);
        Book sut = new Book(_expectedMessageConsumer, _buyHalf, _sellHalf);
        return sut;
    }

}
