package com.jeff.fischman.exercise.process.reporting;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.IntStream;

public class BookReportControllerTests {

    // Note that most of these tests are designed to test whether we properly handle
    // the situation at program termination correctly regarding whether or not to
    // print the book one last time. Essentially, if nothing has changed since our last
    // printing, then we wouldn't want to print it then. However, if something has
    // changed since our last printing (i.e. we got more messages), then we would want
    // to print it (this is signified by hasFinalPendingReport() returns true)
    //

    @Test
    public void testWithNoIncCountsThatHasAdditionalChangeToReportReturnsTrue() {
        // This test emulates a situation where we get no messages but should still
        // print a final (empty) book at program shutdown.
        //
        BookReportController sut = new BookReportController(10);
        Assert.assertFalse(sut.isTimeForPeriodicBookReport());
        Assert.assertTrue(sut.hasFinalPendingReport());
    }

    @Test
    public void testHandlingOfOneMessage() {
        // This test emulates a situation where we get no messages but should still
        // print a final (empty) book at program shutdown.
        //
        BookReportController sut = new BookReportController(10);
        Assert.assertFalse(sut.isTimeForPeriodicBookReport());
        sut.incCount();
        Assert.assertFalse(sut.isTimeForPeriodicBookReport());
        Assert.assertTrue(sut.hasFinalPendingReport());
    }


    @Test
    public void testHandlingOf10MessagesIfIsTimeForPeriodicBookReportIsCalledOn10thMessage() {
        BookReportController sut = new BookReportController(10);
        verifyHandlingOfFirst9Messages(sut);
        sut.incCount();
        Assert.assertTrue(sut.isTimeForPeriodicBookReport());
        // if we don't call isTimeForPeriodicBookReport on 10th message, then
        // hasFinalPendingReport() should return true.
        Assert.assertFalse(sut.hasFinalPendingReport());
    }

    @Test
    public void testHandlingOf10MessagesIfIsTimeForPeriodicBookReportNotCalledOn10thMessage() {
        BookReportController sut = new BookReportController(10);
        verifyHandlingOfFirst9Messages(sut);
        sut.incCount();
        // if we don't call isTimeForPeriodicBookReport on 10th message, then
        // hasFinalPendingReport() should return true.
        Assert.assertTrue(sut.hasFinalPendingReport());
    }

    @Test
    public void testHandlingOf11thMessage() {
        BookReportController sut = new BookReportController(10);
        verifyHandlingOfFirst10Messages(sut);


        sut.incCount(); // Not time yet for periodic book report
        Assert.assertFalse(sut.isTimeForPeriodicBookReport());

        // But if the program ended now, we would do one last final pending Report
        Assert.assertTrue(sut.hasFinalPendingReport());
    }

    private void verifyHandlingOfFirst10Messages(BookReportController sut) {
        verifyHandlingOfFirst9Messages(sut);

        // now do one more increment which should make it "time" for the report.
        sut.incCount();
        Assert.assertTrue(sut.isTimeForPeriodicBookReport());

        // Verify that if the program ended now, there would be nothing 'leftover' to
        // print at program's end since all messages were accounted for in our last
        // printing of the book.
        Assert.assertFalse(sut.hasFinalPendingReport());

    }

    private void verifyHandlingOfFirst9Messages(BookReportController sut) {
        IntStream.range(0, 9).forEach(i -> {
            sut.incCount();
            Assert.assertFalse(sut.isTimeForPeriodicBookReport());
        });
    }
}