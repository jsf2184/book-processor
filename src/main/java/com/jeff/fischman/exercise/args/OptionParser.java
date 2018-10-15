package com.jeff.fischman.exercise.args;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OptionParser {

    private RuntimeOptions _runtimeOptions;

    public OptionParser(String[] args) {
        _runtimeOptions = new RuntimeOptions();
    }

    public boolean parseOptions(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-")) {
                arg = arg.substring(1);
            }
            arg = arg.toLowerCase();
            if (arg.startsWith("h")) {
                printUsage();
                return false;
            }
            try {
                CmdOption cmdOption = CmdOption.valueOf(arg);
                _runtimeOptions.setOption(cmdOption);
            } catch (Exception e) {
                System.err.printf("Illegal command line option: '%s'\n", arg);
                printUsage();
                return false;
            }
        }
        return true;
    }

    public RuntimeOptions getRuntimeOptions() {
        return _runtimeOptions;
    }

    private void printUsage() {
        System.err.println("The program accepts these options");
        System.err.println("  -h:       for help");
        System.err.println("  -genmo:   to have the program automatically generate missing orders");
        System.err.println("  -cplxm:   to allow the program to be allow complex order modifications");
        System.err.println("  -abook:   log all book changes instead of every 10th\n");
        System.err.println("  -canned:  to run the program off of canned data rather than standard input\n");
    }
}
