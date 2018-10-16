package com.jeff.fischman.exercise.process.reporting;

import com.jeff.fischman.exercise.book.Book;
import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.process.verification.MissingMessageChecker;
import com.jeff.fischman.exercise.utility.Printer;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class OutputControllerTests {
    private ErrorCounts _errorCounts;
    private MissingMessageChecker _missingMessageChecker;
    private BookReportController _bookReportController;
    private TradeReporter _tradeReporter;
    private Printer _printer;
    private Book _book;


    @Test
    public void testPrintPostMessageReportCases() {
        testPrintPostMessageReport(true, true);
        testPrintPostMessageReport(true, false);
        testPrintPostMessageReport(false, true);
        testPrintPostMessageReport(false, false);
    }

    private void testPrintPostMessageReport(boolean tradeReporterReturnsReport,
                                            boolean bookReportControllerReturnValue)
    {
        OutputController sut = createSut();
        String midQuoteString = "midQuoteString";
        String tradeReporterReport = tradeReporterReturnsReport ?
                "tradeReporterReport" :
                null;
        String bookString = "bookString";

        when(_book.getMidquoteString()).thenReturn(midQuoteString);
        when(_book.toString()).thenReturn(bookString);
        when(_tradeReporter.getReport()).thenReturn(tradeReporterReport);
        when(_bookReportController.isTimeForPeriodicBookReport()).thenReturn(bookReportControllerReturnValue);

        sut.printPostMessageReport();

        verify(_printer, times(1)).print(midQuoteString);

        if (tradeReporterReturnsReport) {
            verify(_printer, times(1)).print("tradeReporterReport");
        } else {
            verify(_printer, times(0)).print("tradeReporterReport");
        }
        verify(_bookReportController, times(1)).incCount();
        verify(_bookReportController, times(1)).isTimeForPeriodicBookReport();

        if (bookReportControllerReturnValue) {
            verify(_printer, times(1)).print(bookString);
        } else {
            verify(_printer, times(0)).print(bookString);
        }

    }

    @Test
    public void testPrintFinalReportCases() {
        testPrintFinalReport(1, true);
        testPrintFinalReport(1, false);
        testPrintFinalReport(0, true);
        testPrintFinalReport(0, false);
    }

    private void testPrintFinalReport(int numGeneratedMissingOrders,
                                      boolean bookReportControllerReturnValue)
    {
        OutputController sut = createSut();
        String errorCountsReport = "errorCountsReport";
        String bookString = "bookString";

        MissingMessageChecker.Result checkerRes = new MissingMessageChecker.Result(0,
                                                                                   numGeneratedMissingOrders,
                                                                                   numGeneratedMissingOrders);

        when(_missingMessageChecker.checkForMissedMessages()).thenReturn(checkerRes);
        when(_bookReportController.hasFinalPendingReport()).thenReturn(bookReportControllerReturnValue);
        when(_book.toString()).thenReturn(bookString);
        when(_errorCounts.getReport()).thenReturn(errorCountsReport);

        // invoke the method we are testing
        sut.printFinalReport();

        verify(_missingMessageChecker, times(1)).checkForMissedMessages();
        if (numGeneratedMissingOrders > 0) {
            verify(_bookReportController, times(1)).incCount();
        } else {
            verify(_bookReportController, times(0)).incCount();
        }

        if (bookReportControllerReturnValue) {
            verify(_printer, times(1)).print(bookString);
        } else {
            verify(_printer, times(0)).print(bookString);
        }
        verify(_printer, times(1)).print(errorCountsReport);
    }


    private OutputController createSut() {
        _errorCounts = mock(ErrorCounts.class);
        _missingMessageChecker = mock(MissingMessageChecker.class);
        _bookReportController = mock(BookReportController.class);
        _tradeReporter = mock(TradeReporter.class);
        _printer = mock(Printer.class);
        _book = mock(Book.class);
        OutputController res = new OutputController(_errorCounts,
                                                    _missingMessageChecker,
                                                    _bookReportController,
                                                    _tradeReporter,
                                                    _printer,
                                                    _book);
        return res;
    }
}
