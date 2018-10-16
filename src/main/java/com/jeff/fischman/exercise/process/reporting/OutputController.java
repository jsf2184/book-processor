package com.jeff.fischman.exercise.process.reporting;

import com.jeff.fischman.exercise.book.Book;
import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.process.verification.MissingMessageChecker;
import com.jeff.fischman.exercise.utility.Printer;

public class OutputController {
    private ErrorCounts _errorCounts;
    private MissingMessageChecker _missingMessageChecker;
    private BookReportController _bookReportController;
    private TradeReporter _tradeReporter;
    private Printer _printer;
    private Book _book;

    public OutputController(ErrorCounts errorCounts,
                            MissingMessageChecker missingMessageChecker,
                            BookReportController bookReportController,
                            TradeReporter tradeReporter,
                            Printer printer,
                            Book book)
    {
        _errorCounts = errorCounts;
        _missingMessageChecker = missingMessageChecker;
        _bookReportController = bookReportController;
        _tradeReporter = tradeReporter;
        _printer = printer;
        _book = book;

        // Have the printer print it initial "Output" heading.
        _printer.init();
    }

    public void printPostMessageReport() {
        // After processing each line of input, there is the potential to do some
        // reporting. Start by printing the midQuote value.
        //
        _printer.print(_book.getMidquoteString());

        // See if there is a trade to report on
        String report = _tradeReporter.getReport();
        if (report != null) {
            _printer.print(report);
        }

        // See if it is time to print the book again (i.e. every 10th message)
        _bookReportController.incCount();
        if (_bookReportController.isTimeForPeriodicBookReport()) {
            // yep, time to print out the book.
            _printer.print(_book.toString());
        }
    }

    public void printFinalReport() {

        // Be sure our final error counts reflect any missed trade or order messages that
        // failed to come in at the end. The _missingMessageChecker will do this for us.
        // However, if Remove or Modify messages were missed at the end, we won't simulate
        // them so that the last time we print the book, the book will be in the state that
        // the exchange left it in.

        MissingMessageChecker.Result checkerRes = _missingMessageChecker.checkForMissedMessages();
        int generatedOrders = checkerRes.getGeneratedOrders();
        if (generatedOrders > 0) {
            // If this final check for missed orders caused new orders to be generated, then
            // that would have necessitated one more book change. We need to let the bookReportController
            // know about that so that it will be considered when it decides if it has an additional
            // changeToReport(called below)
            //
            _bookReportController.incCount();
        }

        // Got end of data. Print the book again if there has been a message since
        // the last time we printed the book.
        if (_bookReportController.hasFinalPendingReport()) {
            _printer.print(_book.toString());
        }
        // Print our errorCounts.
        _printer.print(_errorCounts.getReport());
    }
}
