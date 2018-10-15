package com.jeff.fischman.exercise.process.reporting;

import com.jeff.fischman.exercise.process.reporting.TradeReporter;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class TradeReporterTests {
    @Test
    public void testScenario() {
        TradeReporter sut = new TradeReporter();
        verify(sut, "1025", 2, "2@1025\n");
        verify(sut, "1025", 1, "3@1025\n");
        verify(sut, "1000", 1, "1@1000\n");
        verify(sut, "1025", 1, "1@1025\n");
        verify(sut, "1025", 11, "12@1025\n");

    }

    private  void verify(TradeReporter sut, String decimalStr, long qty, String expected) {
        sut.onTrade(new BigDecimal(decimalStr), qty);
        String actual = sut.getReport();
        Assert.assertEquals(expected, actual);
        // verify we can only get a report once.
        Assert.assertNull(sut.getReport());
    }
}
