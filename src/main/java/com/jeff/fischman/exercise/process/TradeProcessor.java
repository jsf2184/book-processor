package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.messages.Trade;
import com.jeff.fischman.exercise.process.reporting.TradeReporter;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;

public class TradeProcessor {
    private ErrorCounts _errorCounts;
    private ExpectedMessageStore _expectedMessageStore;
    private TradeReporter _tradeReporter;

    public TradeProcessor(ErrorCounts errorCounts,
                          ExpectedMessageStore expectedMessageStore,
                          TradeReporter tradeReporter)
    {
        _errorCounts = errorCounts;
        _expectedMessageStore = expectedMessageStore;
        _tradeReporter = tradeReporter;
    }

    public void process(Trade trade) {
        boolean ok = false;
        if (_expectedMessageStore.hasMissingTrades()) {
            // We were expecting a trade. Let the _expectedMessageStore see if it is what we expected.
            ok = _expectedMessageStore.onActualTrade(trade);
        }
        if (!ok) {
            // We got a trade that we were not expecting. Report this as an error.
            _errorCounts.inc(ErrorType.UnmatchedTrade);
        } else {
            // only report on the trade if it was valid.
            _tradeReporter.onTrade(trade.getPrice(), trade.getQuantity());
        }
    }
}
