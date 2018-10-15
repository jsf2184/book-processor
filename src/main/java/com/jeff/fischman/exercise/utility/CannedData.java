package com.jeff.fischman.exercise.utility;

import java.util.*;

public class CannedData {

    public static List<String> _writeupSampleInput =  Arrays.asList(
            "A,100000,S,1,1075",
            "A,100001,B,9,1000",
            "A,100002,B,30,975",
            "A,100003,S,10,1050",
            "A,100004,B,10,950",
            "A,100005,S,2,1025",
            "A,100006,B,1,1000",
            "X,100004,B,10,950",
            "A,100007,S,5,1025",
            "A,100008,B,3,1050",
            "T,2,1025",
            "T,1,1025",
            "X,100008,B,3,1050",
            "BADMESSAGE",
            "X,100005,S,2,1025",
            "M,100007,S,4,1025"
    );

    public static List<String> _writeupSampleOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "1037.5",
            "1037.5",
            "1025",
            "1025",
            "1012.5",
            "1012.5",
            "1012.5",
            "1012.5",
            "1037.5",
            "SELLS:",
            "1075,1",
            "1050,10",
            "1025,2,5",
            "BUYS:",
            "1050,3",
            "1000,9,1",
            "975,30",
            "1037.5",
            "2@1025",
            "1037.5",
            "3@1025",
            "1012.5",
            "1012.5",
            "1012.5",
            "1012.5",
            "SELLS:",
            "1075,1",
            "1050,10",
            "1025,4",
            "BUYS:",
            "1000,9,1",
            "975,30",
            "ERRORS:",
            "a,1"
    );



}
