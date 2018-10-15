package com.jeff.fischman.exercise.utility;

public interface Printer {
    void print(String s);

    class StdoutPrinter implements  Printer {
        @Override
        public void print(String s) {
            System.out.print(s);
        }
    }

    class CapturePrinter implements  Printer {
        StringBuilder _sb;

        public CapturePrinter() {
            _sb = new StringBuilder();
        }

        @Override
        public void print(String s) {
            _sb.append(s);
        }

        public String getOutput() {
            return _sb.toString();
        }
    }

    default void init() {
        print("Output\n");
        print("------\n");
    }

}
