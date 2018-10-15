package com.jeff.fischman.exercise.error;

public enum ErrorType {
    CorruptedMessage("a"),
    DuplicatedOrderId("b"),
    UnmatchedTrade("c"),
    UnkownRemoveOrderId("d"),
    TradeUnreported("e"),
    MissingOrBadFields("f"),
    BadRemovalDetails("g"),
    OrderChangeUnreported("h"),
    IllegalModificationChanges("i");

    String _abbrev;

    ErrorType(String abbrev) {
        _abbrev = abbrev;
    }

    public String getAbbrev() {
        return _abbrev;
    }
}
