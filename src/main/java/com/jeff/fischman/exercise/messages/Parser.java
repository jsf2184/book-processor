package com.jeff.fischman.exercise.messages;

import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.error.ErrorType;
import com.jeff.fischman.exercise.error.ParserException;

import java.math.BigDecimal;

public class Parser {

    private ErrorCounts _errorCounts;

    public Parser(ErrorCounts errorCounts) {
        _errorCounts = errorCounts;
    }

    public Message parse(String line) {
        Message message = null;
        try {
            message = parseInternal(line);
        } catch (ParserException e) {
            ErrorType errorType = e.getErrorType();
            _errorCounts.inc(errorType);
        }
        return message;
    }

    // A note about the error code encapsulated in ParserException. I'm not
    // positive what kinds of error constitute a "corrupt message (used for errors
    // in category: 'a' vs the errros that should be used in category: 'f'.
    // Basically, I chose corrupted if the code couldn't extract a valid 'Action'
    // and chose category 'f' (represented by enum MissingOrBadFields) for just
    // about everything else.

    private Message parseInternal(String line) throws ParserException {
        if (line == null) {
            throw new ParserException(ErrorType.CorruptedMessage);
        }
        // for resiliency, eliminate any reduncant spaces.
        line = removeSpaces(line);
        String[] parts = line.split(",", -1);
        if (parts.length < 1) {
            // don't even have an action field so 'corrupt.
            throw new ParserException(ErrorType.CorruptedMessage);
        }

        Action action = Action.getAction(parts[0]);
        if (action == null) {
            // invalid/unknown action field value.
            throw new ParserException(ErrorType.CorruptedMessage);
        }

        Message res;
        switch (action.getMsgType()) {
            case Order:
                res = parseOrder(action, parts);
                break;
            case Trade:
                res = parseTrade(parts);
                break;
            default:
                throw new ParserException(ErrorType.CorruptedMessage);
        }
        return res;
    }

    private static Message parseTrade(String[] parts) throws ParserException {
        if (parts.length != 3) {
            throw new ParserException(ErrorType.MissingOrBadFields);
        }
        long qty = parsePositiveLong(parts[1]);
        BigDecimal price = parseNonNegativeBigDecimal(parts[2]);
        Trade res = new Trade(qty, price);
        return res;
    }

    private static Message parseOrder(Action action, String[] parts) throws ParserException {
        if (parts.length != 5) {
            throw new ParserException(ErrorType.MissingOrBadFields);
        }

        long orderId = parsePositiveLong(parts[1]);
        Side side = Side.getSide(parts[2]);
        if (side == null) {
            // don't even have a legit side value, so call it a corrupted message.
            throw new ParserException(ErrorType.CorruptedMessage);
        }
        long qty = parsePositiveLong(parts[3]);
        BigDecimal price = parseNonNegativeBigDecimal(parts[4]);
        Order res = new Order(action, orderId, side, qty, price);
        return res;
    }

    public static String removeSpaces(String s) throws ParserException {
        if (s == null) {
            throw new ParserException(ErrorType.CorruptedMessage);
        }
        String res = s.replaceAll("\\s", "");
        return res;
    }

    public static long parsePositiveLong(String s) throws ParserException {

        if (s == null) {
            throw new ParserException(ErrorType.CorruptedMessage);
        }
        long qty;
        try {
            qty = Long.parseLong(s);
        } catch (Exception ignore) {
            throw new ParserException(ErrorType.CorruptedMessage);
        }
        if (qty <=0) {
            throw new ParserException(ErrorType.MissingOrBadFields);
        }
        return qty;
    }


    public static BigDecimal parseNonNegativeBigDecimal(String s) throws ParserException {
        if (s == null) {
            throw new ParserException(ErrorType.CorruptedMessage);
        }
        BigDecimal res;
        try {
            res = new BigDecimal(s);
        }
        catch (Exception ignore) {
            throw new ParserException(ErrorType.CorruptedMessage);
        }
        if (res.compareTo(new BigDecimal(0)) < 0) {
            throw new ParserException(ErrorType.MissingOrBadFields);
        }
        return res;

}
}
