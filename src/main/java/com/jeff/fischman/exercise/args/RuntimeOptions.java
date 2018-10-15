package com.jeff.fischman.exercise.args;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RuntimeOptions {
    private Map<CmdOption, Boolean> _options;

    public RuntimeOptions() {
        _options = new HashMap<>();
        Arrays.stream(CmdOption.values()).forEach(v -> _options.put(v, false));
    }

    public void setOption(CmdOption cmdOption) {
        _options.put(cmdOption, true);
    }
    public boolean getOption(CmdOption cmdOption) {
        return _options.get(cmdOption);
    }
}
