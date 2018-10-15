package com.jeff.fischman.exercise.utility;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class StringUtilityTests {
    @Test
    public void testBigDecimalFormats() {
        testFormat("0", "0");
        testFormat("10", "10");
        testFormat("10.1", "10.1");
        testFormat("10.12", "10.12");
        testFormat(".001", "0");
        testFormat(".009", ".01");
        testFormat(".1", ".1");
        testFormat(".12", ".12");
        testFormat(".123", ".12");
        testFormat(".120", ".12");
        testFormat(".126", ".13");

        testFormat("10.153", "10.15");
        testFormat("10.159", "10.16");
    }

    private  void testFormat(String s, String expected) {
        BigDecimal val = new BigDecimal(s);
        String actual = StringUtility.formatPrice(val);
        Assert.assertEquals(expected, actual);
    }
}
