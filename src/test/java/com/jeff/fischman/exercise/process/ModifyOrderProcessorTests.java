package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.book.Book;
import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.list.Node;
import com.jeff.fischman.exercise.messages.Action;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.messages.OrderDetails;
import com.jeff.fischman.exercise.messages.Side;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ModifyOrderProcessorTests {
    private ErrorCounts _errorCounts;
    private ExpectedMessageStore _expectedMessageStore;
    private OrderMap _orderMap;
    private RemoveOrderProcessor _removeOrderProcessor;
    private AddOrderProcessor _addOrderProcessor;


    @Test
    public void testUnknownOrderIdError() {
        ModifyOrderProcessor sut = createSut(true);
        OrderDetails orderDetails = new OrderDetails(1L, Side.Buy, 10L, new BigDecimal(3));
        Order modOrder = new Order(Action.Modify, orderDetails);
        sut.process(modOrder);
        Assert.assertEquals(1, _errorCounts.getCount(ErrorType.UnkownRemoveOrderId));
        Assert.assertEquals(1,  _errorCounts.getTotalErrorCount());
    }

    @Test
    public void testCasesWhereComplexModificationFails() {
        testWithValidOrderId(false, true, false);
        testWithValidOrderId(false, true, true);
    }

    @Test
    public void testSimpleModificationSuccessCases() {
        testWithValidOrderId(false, false, false);
        testWithValidOrderId(false, false, true);
        testWithValidOrderId(true, false, false);
        testWithValidOrderId(true, false, true);
    }

    @Test
    public void testComplexModificationSuccessCases() {
        testWithValidOrderId(true, true, false);
        testWithValidOrderId(true, true, true);
    }

    // Utility method helps us test various permutations of a successful removal.
    private void testWithValidOrderId(boolean allowComplexModifications,
                                      boolean createComplexChange,
                                      boolean hasMissingChangedOrders) {

        // Note a simpe modification is one where only the qty changed. A
        ModifyOrderProcessor sut = createSut(allowComplexModifications);

        long priorQty = 2;
        BigDecimal priorPrice = new BigDecimal(10);
        long modQty;
        BigDecimal modPrice;

        if (createComplexChange) {            // mod    prior
            modQty = priorQty;                //  2       2
            modPrice = new BigDecimal(11);    //  11     10
        } else {
            modQty = priorQty -1;             //  1       2
            modPrice = priorPrice;            //  10     10
        }

        OrderDetails priorDetails = new OrderDetails(1L, Side.Buy, priorQty, priorPrice);
        Node<OrderDetails> priorOrderNode = new Node<>(priorDetails);
        _orderMap.put(1L, priorOrderNode);

        // Build the new modified order
        OrderDetails modDetails = new OrderDetails(1L, Side.Buy, modQty, modPrice);
        Order modOrder = new Order(Action.Remove, modDetails);

        // instruct our _expectedMessageStore mock what to return for its
        // hasMissingChangedOrders call.
        when(_expectedMessageStore.hasMissingChangedOrders()).thenReturn(hasMissingChangedOrders);

        sut.process(modOrder);

        // Based on the parameters for this test, determine whether we expect any errors to have been produced.
        int expectedErrorCount = (createComplexChange && !allowComplexModifications) ? 1 : 0;
        // Check that the error is or is not recorded ss expected.
        Assert.assertEquals(expectedErrorCount, _errorCounts.getCount(ErrorType.IllegalModificationChanges));
        Assert.assertEquals(expectedErrorCount, _errorCounts.getTotalErrorCount());

        // Whether or not we expect an error dictates the other things we need to validate
        if (expectedErrorCount > 0) {
            // Because we expect an error, the order processor should not have tried to do the
            // actual modification. First validate that the node is unchanged.
            Assert.assertSame(priorDetails, priorOrderNode.getData());
            // And that we didnt try to pass anything to the expectedMessageStore
            verifyZeroInteractions(_expectedMessageStore);
            verifyZeroInteractions(_addOrderProcessor);
            verifyZeroInteractions(_removeOrderProcessor);
            // Or try to generate any inserts or deletes.
        } else if (createComplexChange){
            // We had a complex change which should produce a remove and an add. Verify we got them
            verify(_removeOrderProcessor, times(1)).process(new Order(Action.Remove, priorDetails));
            verify(_addOrderProcessor, times(1)).process(new Order(Action.Add, modDetails));
        } else {
            // It was a simple modification so the remove and add order processors should not have been involved
            verifyZeroInteractions(_removeOrderProcessor);
            verifyZeroInteractions(_addOrderProcessor);

            // make sure that the node has been modified to have the updated order details.
            Assert.assertSame(modDetails, priorOrderNode.getData());

            // Make sure we called on _expectedMessageStore.hasMissingChangedOrders()
            verify(_expectedMessageStore, times(1)).hasMissingChangedOrders();

            // And if it was supposed to answer yes, that we passed the mod order to it.
            int expectedOnChangedOrderCalls = hasMissingChangedOrders ? 1 : 0;
            verify(_expectedMessageStore, times(expectedOnChangedOrderCalls)).onActualChangedOrder(modOrder);
        }
    }


    private ModifyOrderProcessor createSut(boolean complexModifications) {
        _errorCounts = new ErrorCounts();
        _expectedMessageStore = mock(ExpectedMessageStore.class);
        _orderMap = new OrderMap();
        _removeOrderProcessor = mock(RemoveOrderProcessor.class);
        _addOrderProcessor = mock(AddOrderProcessor.class);

        ModifyOrderProcessor res = new ModifyOrderProcessor(complexModifications,
                                                            _errorCounts,
                                                            _expectedMessageStore,
                                                            _orderMap,
                                                            _removeOrderProcessor,
                                                            _addOrderProcessor);
        return res;
    }

}
