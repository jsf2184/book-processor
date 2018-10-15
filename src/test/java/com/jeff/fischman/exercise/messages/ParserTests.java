package com.jeff.fischman.exercise.messages;

import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.error.ParserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class ParserTests {

    // ---------------------------------------------
    // Simple tests of lower level Parser functions.
    // ---------------------------------------------

    private Parser _sut;
    private ErrorCounts _errorCounts;

    @Before
    public void setup() {
        _errorCounts = new ErrorCounts();
        _sut = new Parser(_errorCounts);
    }

    @Test
    public void testRemoveSpaces() throws ParserException {
        Assert.assertEquals("a,b,c,d,e", Parser.removeSpaces("a   , b,  c , d,  e "));
    }

    @Test
    public void testParseNonNegativeBigDecimalErrorCases() {
        validateParseBigDecimalError(null, ErrorType.CorruptedMessage);
        validateParseBigDecimalError("", ErrorType.CorruptedMessage);
        validateParseBigDecimalError("abc", ErrorType.CorruptedMessage);
        validateParseBigDecimalError("10.3x", ErrorType.CorruptedMessage);
        validateParseBigDecimalError("-10.3", ErrorType.MissingOrBadFields);
    }

    @Test
    public void testParsePositiveLongErrorCases() {
        validateParseLongError(null, ErrorType.CorruptedMessage);
        validateParseLongError("", ErrorType.CorruptedMessage);
        validateParseLongError("abc", ErrorType.CorruptedMessage);
        validateParseLongError("103x", ErrorType.CorruptedMessage);
        validateParseLongError("-103", ErrorType.MissingOrBadFields);
        validateParseLongError("0", ErrorType.MissingOrBadFields);
    }

    @Test
    public void testParseNonNegativeBigDecimalSuccessCases() throws ParserException {
        Assert.assertEquals(new BigDecimal("10.5"), Parser.parseNonNegativeBigDecimal("10.5"));
        Assert.assertEquals(new BigDecimal(105), Parser.parseNonNegativeBigDecimal("105"));
        Assert.assertEquals(new BigDecimal(0), Parser.parseNonNegativeBigDecimal("0"));
    }

    @Test
    public void testParsePositiveLongSuccessCases() throws ParserException {
        Assert.assertEquals(105, Parser.parsePositiveLong("105"));
    }


    // ---------------------------------------------
    // Tests of the Full Parser follow.
    // ---------------------------------------------

    @Test
    public void testParseNullInput() {
        validateParseError(ErrorType.CorruptedMessage, null);
    }

    @Test
    public void testParseOfNonsenseMsg() {
        validateParseError(ErrorType.CorruptedMessage, "Nonsense");
    }

    @Test
    public void testGoodTradeParse() {
        Trade expected = new Trade(567L, new BigDecimal("10.25"));
        Message actual = _sut.parse("T  ,  567, 10.25");
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(0, _errorCounts.getTotalErrorCount());
    }

    @Test
    public void testGoodAddOrderParse() {
        Order expected = new Order(Action.Add, 1234L, Side.Buy, 567L, new BigDecimal("10.25"));
        Message actual = _sut.parse("A  ,  1234, B, 567, 10.25");
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(0, _errorCounts.getTotalErrorCount());
    }

    @Test
    public void testGoodModifyOrderParse() {
        Order expected = new Order(Action.Modify, 1234L, Side.Sell, 567L, new BigDecimal("10.25"));
        Message actual = _sut.parse("M  ,  1234, S, 567, 10.25");
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(0, _errorCounts.getTotalErrorCount());
    }

    @Test
    public void testGoodRemoveOrderParse() {
        Order expected = new Order(Action.Remove, 1234L, Side.Sell, 567L, new BigDecimal("10.25"));
        Message actual = _sut.parse("X  ,  1234, S, 567, 10.25");
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(0, _errorCounts.getTotalErrorCount());
    }

    @Test
    public void testMissingTradeFieldParseStrings() {
        // These tests have one field less than they should.
        validateParseError(ErrorType.MissingOrBadFields, "T,10.25");
        validateParseError(ErrorType.MissingOrBadFields, "T,567");

        // These tests have an empty field between commas.
        validateParseError(ErrorType.CorruptedMessage, "T,,10.25");
        validateParseError(ErrorType.CorruptedMessage, "T,567,");
    }

    @Test
    public void testMissingOrderFieldParseStrings() {
        // These tests have one field less than they should.
        validateParseError(ErrorType.MissingOrBadFields, "X,1234,S,567");
        validateParseError(ErrorType.MissingOrBadFields, "M,1234,S,10.25");
        validateParseError(ErrorType.MissingOrBadFields, "A,1234,567,10.25");
        validateParseError(ErrorType.MissingOrBadFields, "X,S,567,10.25");

        // These tests have an empty field between commas.
        validateParseError(ErrorType.CorruptedMessage, "M,1234,S,567,");
        validateParseError(ErrorType.CorruptedMessage, "A,1234,S,,10.25");

        // Empty 'side' field
        validateParseError(ErrorType.CorruptedMessage, "X,1234,,567,10.25");
        validateParseError(ErrorType.CorruptedMessage, "M,,S,567,10.25");
    }

    @Test
    public void testBadValueInTradeParseField() {
        // These tests have one field less than they should.
        validateParseError(ErrorType.CorruptedMessage, "T,567#,10.25");
        validateParseError(ErrorType.CorruptedMessage, "T,567,10.2b5");
    }

    @Test
    public void testBadValueInOrderParseField(){

        // Bad action field
        // RequirementAmbiguity: For missing or bad enum values we are calling it a corrupted message.
        validateParseError(ErrorType.CorruptedMessage, "Z,1234,S,567,10.25");
        // Bad orderID
        validateParseError(ErrorType.CorruptedMessage, "A,1234X,S,567,10.25");

        // Bad 'side' field
        // RequirementAmbiguity: For missing or bad enum values we are calling it a corrupted message.
        validateParseError(ErrorType.CorruptedMessage, "A,1234,s,567,10.25");

        // Bad Qty
        validateParseError(ErrorType.CorruptedMessage, "A,1234,S,56a7,10.25");
        // Bad Price
        validateParseError(ErrorType.CorruptedMessage, "A,1234,S,567,10n25");
    }

    @Test
    public void testExtraValueInTradeParseString() {
        // These tests have one field less than they should.
        validateParseError(ErrorType.MissingOrBadFields, "T,567,10.25,3");
    }

    @Test
    public void testExtraValueInOrderParseStringFields() {
        // Good except for that extra 3 on the end.
        validateParseError(ErrorType.MissingOrBadFields, "A,1234,S,567,10.25,3");
    }

    private static void validateParseError(ErrorType expectedErrorType, String input) {

        ErrorCounts errorCounts = new ErrorCounts();
        Parser sut = new Parser(errorCounts);
        Message message = sut.parse(input);
        Assert.assertNull(message);
        Assert.assertEquals(1, errorCounts.getTotalErrorCount());
        Assert.assertEquals(1, errorCounts.getCount(expectedErrorType));
    }

    private void validateParseBigDecimalError(String input, ErrorType expectedErrorType) {
        ErrorType actualErrorType = null;
        try {
            BigDecimal res = Parser.parseNonNegativeBigDecimal(input);
        } catch (ParserException e) {
            actualErrorType = e.getErrorType();
        }
        Assert.assertEquals(expectedErrorType, actualErrorType);
    }

    private void validateParseLongError(String input, ErrorType expectedErrorType) {
        ErrorType actualErrorType = null;
        try {
            long res = Parser.parsePositiveLong(input);
        } catch (ParserException e) {
            actualErrorType = e.getErrorType();
        }
        Assert.assertEquals(expectedErrorType, actualErrorType);
    }

}
