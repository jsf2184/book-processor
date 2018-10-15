package com.jeff.fischman.exercise.error;

import org.junit.Assert;
import org.junit.Test;


public class ErrorCountsTests {
    @Test
    public void testAFewIncsQueryAndReport() {
        ErrorCounts sut = new ErrorCounts();

        // 3 CorruptedMessages
        sut.inc(ErrorType.CorruptedMessage);
        sut.inc(ErrorType.CorruptedMessage);
        sut.inc(ErrorType.CorruptedMessage);

        sut.inc(ErrorType.UnkownRemoveOrderId);
        sut.inc(ErrorType.UnkownRemoveOrderId);

        sut.inc(ErrorType.MissingOrBadFields);

        // Verify each of our counts
        //    CorruptedMessage = 3
        //    UnknownRemoveOrderId  = 2
        //    MissingOrBadFields = 1

        for (ErrorType errorType : ErrorType.values()) {
            int expectation = 0;
            switch (errorType) {
                case CorruptedMessage:
                    expectation = 3;
                    break;
                case UnkownRemoveOrderId:
                    expectation = 2;
                    break;
                case MissingOrBadFields:
                    expectation = 1;
                    break;
            }
            Assert.assertEquals(expectation, sut.getCount(errorType));
        }

        String expected =
                "ERRORS:\n" +
                "a,3\n" +
                "d,2\n" +
                "f,1\n";

        String actual = sut.getReport();
        Assert.assertEquals(expected, actual);


    }
}
