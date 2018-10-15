package com.jeff.fischman.exercise.messages;

import java.util.HashMap;
import java.util.Map;

public enum Side {

    Buy("B", "BUYS"),
    Sell("S", "SELLS");

    Side(String abbrev, String reportLabel) {
        _abbrev = abbrev;
        _reportLabel = reportLabel;
    }

    String _abbrev;
    String _reportLabel;
    Side _opposite;

    static Map<String, Side> _map;

    static {
        _map = new HashMap<>();
        for (Side side : Side.values()) {
            _map.put(side._abbrev, side);
        }
        Buy._opposite = Sell;
        Sell._opposite = Buy;
    }

    public static Side getSide(String abbrev) {
        Side res = _map.get(abbrev);
        return res;
    }

    public String getAbbrev() {
        return _abbrev;
    }

    public String getReportLabel() {
        return _reportLabel;
    }

    public Side getOpposite() {
        return _opposite;
    }
}