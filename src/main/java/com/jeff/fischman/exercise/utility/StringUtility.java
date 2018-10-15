package com.jeff.fischman.exercise.utility;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class StringUtility {
    private static final DecimalFormat _priceFormat = new DecimalFormat("0.##");

    public static boolean isNullOrEmpty(String s) {
        return  s == null || s.length() == 0;
    }

    public static String formatPrice(BigDecimal price) {
        String res = _priceFormat.format(price);
        if (res.startsWith("0.")) {
            res = res.substring(1);
        }
        return res;
    }
    public static String toMulitLineString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        list.forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

}
