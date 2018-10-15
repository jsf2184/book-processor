package com.jeff.fischman.exercise.process.reporting;

import com.jeff.fischman.exercise.utility.StringUtility;

import java.math.BigDecimal;

public class TradeReporter {
    private BigDecimal _priorPrice;
    private long _priorQuantity;
    private String _report;

    public TradeReporter() {
        _priorPrice = null;
        _priorQuantity = 0;
    }

    public void onTrade(BigDecimal price, long quantity) {
        if (!price.equals(_priorPrice)) {
            _priorQuantity = 0;
            _priorPrice = price;
        }
        _priorQuantity += quantity;
        _report = String.format("%d@%s\n", _priorQuantity, StringUtility.formatPrice(_priorPrice));
    }

    // A funny getReport() to insure that a given trade is only reported once.
    public String getReport() {
        String res = _report;
        _report = null; // clear _report so each trade will only be reported once.
        return res;
    }

}
