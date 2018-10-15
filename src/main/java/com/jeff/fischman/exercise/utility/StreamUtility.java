package com.jeff.fischman.exercise.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.stream.Stream;

public class StreamUtility {
    public static Stream<String> getStdinAsStream() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        Stream<String> res = bufferedReader.lines();
        return res;
    }
}
