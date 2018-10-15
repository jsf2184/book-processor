package com.jeff.fischman.exercise.process.reporting;

public class BookReportController {
    private final int _reportFrequency;
    private long _msgCount;
    private long _lastReportCount;

    public BookReportController(int reportFrequency) {
        _reportFrequency = reportFrequency;
        _msgCount = 0;
        // set _lastReportCount to -1 to assure correct behavior from
        // hasFinalPendingReport() if incCount() is never called
        //
        _lastReportCount = -1;
    }

    // increment count and indicate whether we are ready to do another report on the book
    public void incCount() {
        _msgCount++;
    }

    public boolean isTimeForPeriodicBookReport() {
        boolean res = ((_msgCount > 0) && ((_msgCount % _reportFrequency) == 0));
        if (res) {
            _lastReportCount = _msgCount;
        }
        return res;
    }
    // call this method on-end-of-data to see if any messages have come in since our last
    // scheduled report.
    //
    public boolean hasFinalPendingReport() {
        boolean res =  _msgCount > _lastReportCount;
        return res;
    }


}
