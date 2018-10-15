package com.jeff.fischman.exercise.error;

import java.util.HashMap;
import java.util.Map;

public class ErrorCounts {
    private Map<ErrorType, Integer> _counts;

    public ErrorCounts() {
        _counts = new HashMap<>();
    }

    public void inc(ErrorType errorType) {
        inc(errorType, 1);
    }

    public void inc(ErrorType errorType, int delta) {
        _counts.put(errorType, getCount(errorType) + delta);
    }


    public int getCount(ErrorType errorType) {
        Integer res = _counts.get(errorType);
        if (res == null) {
            res = 0;
        }
        return res;
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder("ERRORS:\n");
        for (ErrorType errorType : ErrorType.values()) {
            int count = getCount(errorType);
            if (count > 0) {
                sb.append(String.format("%s,%d\n", errorType.getAbbrev(), count));
            }
        }
        return sb.toString();
    }

    public int getTotalErrorCount() {
        int res = 0;
        for (ErrorType errorType : ErrorType.values()) {
            int count = getCount(errorType);
            res += count;
        }
        return res;
    }
}