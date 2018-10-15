package com.jeff.fischman.exercise.bootstrap;

import com.jeff.fischman.exercise.args.CmdOption;
import com.jeff.fischman.exercise.args.RuntimeOptions;
import com.jeff.fischman.exercise.book.Book;
import com.jeff.fischman.exercise.error.ErrorCounts;
import com.jeff.fischman.exercise.messages.Parser;
import com.jeff.fischman.exercise.process.*;
import com.jeff.fischman.exercise.process.reporting.BookReportController;
import com.jeff.fischman.exercise.process.reporting.Reporter;
import com.jeff.fischman.exercise.process.reporting.TradeReporter;
import com.jeff.fischman.exercise.utility.Printer;
import com.jeff.fischman.exercise.utility.StreamUtility;
import com.jeff.fischman.exercise.process.verification.ExpectedMessageStore;
import com.jeff.fischman.exercise.process.verification.MissingMessageChecker;

import java.util.List;
import java.util.stream.Stream;

public class Bootstrapper {

    private static final int DefaultBookFrequency = 10;
    private Stream<String> _stream;
    private Printer _printer;
    private RuntimeOptions _runtimeOptions;

    public Bootstrapper(Stream<String> stream,
                        Printer printer,
                        RuntimeOptions runtimeOptions)
    {
        _stream = stream;
        _printer = printer;
        _runtimeOptions = runtimeOptions;
    }

    public Processor create() {
        // Build our processor from the ground up, first creating low-level dependencies
        // and working our way up.
        //

        int bookReportFrequency = _runtimeOptions.getOption(CmdOption.abook) ? 1 : DefaultBookFrequency;
        boolean simulateMissedMessages = _runtimeOptions.getOption(CmdOption.genmo);
        boolean permissiveModifications = _runtimeOptions.getOption(CmdOption.cplxm);

        ExpectedMessageStore expectedMessageStore = new ExpectedMessageStore();
        ErrorCounts errorCounts = new ErrorCounts();
        OrderMap orderMap = new OrderMap();

        TradeReporter tradeReporter = new TradeReporter();

        TradeProcessor tradeProcessor = new TradeProcessor(errorCounts,
                                                           expectedMessageStore,
                                                           tradeReporter);

        Book book = new Book(expectedMessageStore);
        AddOrderProcessor addOrderProcessor = new AddOrderProcessor(errorCounts,
                                                                    book,
                                                                    orderMap);

        RemoveOrderProcessor removeOrderProcessor = new RemoveOrderProcessor(errorCounts,
                                                                             expectedMessageStore,
                                                                             book,
                                                                             orderMap);
        ModifyOrderProcessor modifyOrderProcessor = new ModifyOrderProcessor(permissiveModifications,
                                                                             errorCounts,
                                                                             expectedMessageStore,
                                                                             orderMap,
                                                                             removeOrderProcessor,
                                                                             addOrderProcessor);

        OrderDistributor orderDistributor = new OrderDistributor(addOrderProcessor,
                                                                 removeOrderProcessor,
                                                                 modifyOrderProcessor);


        MissingMessageChecker missingMessageChecker = new MissingMessageChecker(simulateMissedMessages,
                                                                                expectedMessageStore,
                                                                                errorCounts,
                                                                                orderDistributor);

        addOrderProcessor.setMissingMessageChecker(missingMessageChecker);

        BookReportController bookReportController = new BookReportController(bookReportFrequency);
        Reporter reporter = new Reporter(errorCounts,
                                         missingMessageChecker,
                                         bookReportController,
                                         tradeReporter,
                                         _printer,
                                         book);

        Processor res = new Processor(_stream,
                                      new Parser(errorCounts),
                                      tradeProcessor,
                                      orderDistributor,
                                      reporter);
        return res;
    }

    public Stream<String> getStream() {
        return _stream;
    }

    public Printer getPrinter() {
        return _printer;
    }

    public static class Main extends Bootstrapper {
        public Main(RuntimeOptions runtimeOptions) {
            super(StreamUtility.getStdinAsStream(),
                  new Printer.StdoutPrinter(),
                  runtimeOptions);
        }
    }

    public static class Test extends  Bootstrapper {

        public Test(List<String> inputList, RuntimeOptions runtimeOptions) {
            super(inputList.stream(), new Printer.CapturePrinter(), runtimeOptions);
        }

        public Printer.CapturePrinter getCapturePrinter() {
            return (Printer.CapturePrinter) getPrinter();
        }
    }

}
