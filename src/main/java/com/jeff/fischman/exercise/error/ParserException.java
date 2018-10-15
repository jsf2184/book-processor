package com.jeff.fischman.exercise.error;

public class ParserException extends Exception {
    private ErrorType _errorType;

    public ParserException(ErrorType errorType) {
        _errorType = errorType;
    }

    public ErrorType getErrorType() {
        return _errorType;
    }
}
