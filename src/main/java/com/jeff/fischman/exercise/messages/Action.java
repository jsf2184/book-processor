package com.jeff.fischman.exercise.messages;

import java.util.HashMap;
import java.util.Map;

public enum Action {
    Add("A", MsgType.Order),
    Remove("X", MsgType.Order),
    Modify("M", MsgType.Order),
    Trade("T", MsgType.Trade);

    String _abbrev;
    MsgType _msgType;

    Action(String abbrev, MsgType msgType) {
        _abbrev = abbrev;
        _msgType = msgType;
    }

    static Map<String, Action> _map;

    static {
        _map = new HashMap<>();
        for (Action action : Action.values()) {
            _map.put(action._abbrev, action);
        }
    }

    public static Action getAction(String abbrev) {
        if (abbrev == null) {
            return null;
        }
        Action res = _map.get(abbrev);
        return res;
    }

    public String getAbbrev() {
        return _abbrev;
    }

    public MsgType getMsgType() {
        return _msgType;
    }
}
