package com.jeff.fischman.exercise;

import com.jeff.fischman.exercise.args.CmdOption;
import com.jeff.fischman.exercise.args.OptionParser;
import com.jeff.fischman.exercise.args.RuntimeOptions;
import com.jeff.fischman.exercise.bootstrap.Bootstrapper;
import com.jeff.fischman.exercise.process.Processor;
import com.jeff.fischman.exercise.utility.CannedData;
import com.jeff.fischman.exercise.utility.Printer;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class App
{
    public static void main( String[] args ) {
        OptionParser optionParser = new OptionParser(args);
        if (!optionParser.parseOptions(args)) {
            return;
        }
        RuntimeOptions runtimeOptions = optionParser.getRuntimeOptions();
        if (runtimeOptions.getOption(CmdOption.canned)) {
            runWithSampleData(runtimeOptions);
        } else {
            runWithStdInput(runtimeOptions);
        }
    }

    public static void runWithSampleData(RuntimeOptions runtimeOptions) {
        List<String> inputList = CannedData._writeupSampleInput;
        Bootstrapper.Test bootstrapper = new Bootstrapper.Test(inputList, runtimeOptions);
        Processor processor = bootstrapper.create();
        processor.processMessages();
        Printer.CapturePrinter printer = bootstrapper.getCapturePrinter();
        System.out.print(printer.getOutput());
    }

    public static void runWithStdInput(RuntimeOptions runtimeOptions) {
        Bootstrapper bootstrapper = new Bootstrapper.Main(runtimeOptions);
        Processor processor = bootstrapper.create();
        processor.processMessages();
    }
}
