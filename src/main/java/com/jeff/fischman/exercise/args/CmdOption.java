package com.jeff.fischman.exercise.args;

public enum CmdOption {
    genmo,     // generate missing orders
    cplxm,     // complex order modifications are allowed
    abook,    // log all book changes instead of every 10th
    canned    // use canned input rather than read from stdin
}
