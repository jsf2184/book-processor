package com.jeff.fischman.exercise.process.verification;

import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.messages.Order;
import com.jeff.fischman.exercise.process.OrderDistributor;

import java.util.stream.Stream;

public class MissingMessageChecker {

    private ExpectedMessageStore _expectedMessageStore;
    private ErrorCounts _errorCounts;
    private OrderDistributor _orderDistributor;
    private boolean _simulateMissedMessages;

    public MissingMessageChecker(boolean simulateMissedMessages,
                                 ExpectedMessageStore expectedMessageStore,
                                 ErrorCounts errorCounts,
                                 OrderDistributor orderDistributor)
    {
        _simulateMissedMessages = simulateMissedMessages;
        _expectedMessageStore = expectedMessageStore;
        _errorCounts = errorCounts;
        _orderDistributor = orderDistributor;
    }

    public static class Result {
        private int _missingTrades;
        private int _missingOrders;
        private int _generatedOrders;

        public Result(int missingTrades, int missingOrders, int generatedOrders) {
            _missingTrades = missingTrades;
            _missingOrders = missingOrders;
            _generatedOrders = generatedOrders;
        }

        public int getMissingTrades() {
            return _missingTrades;
        }

        public int getMissingOrders() {
            return _missingOrders;
        }

        public int getGeneratedOrders() {
            return _generatedOrders;
        }
    }

    public Result checkForMissedMessages() {
        int missingTrades = _expectedMessageStore.getMissingTradeCount();
        if (missingTrades > 0) {
            // whoops - bad news. CmdOption the errors.
            _errorCounts.inc(ErrorType.TradeUnreported, missingTrades);
        }

        int missingOrders = _expectedMessageStore.getMissingChangedOrderCount();
        int generatedOrders = 0;
        if (missingOrders > 0) {
            // report the missing orders
            _errorCounts.inc(ErrorType.OrderChangeUnreported, missingOrders);

            if (_simulateMissedMessages) {
                // Interestingly, without these missing modify and cancel orders, we have a corrupt book.
                // That is, it has dangling "crossed" orders in the two book halves. To keep things clean,
                // lets simulate the receipt of these orders.
                //
                Stream<Order> missingOrderStream = _expectedMessageStore.getMissingChangedOrderStream();
                missingOrderStream.forEach(_orderDistributor::routeToProcessor);
                generatedOrders = missingOrders;
            }
        }

        if (missingOrders > 0 || missingTrades > 0) {
            // Clear the missing messages since we have already processed/recorded them.
            _expectedMessageStore.clearMissingMessages();
        }
        Result res = new Result(missingTrades, missingOrders, generatedOrders);
        return res;
    }

}
