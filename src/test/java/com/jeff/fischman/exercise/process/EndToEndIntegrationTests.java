package com.jeff.fischman.exercise.process;

import com.jeff.fischman.exercise.args.CmdOption;
import com.jeff.fischman.exercise.args.RuntimeOptions;
import com.jeff.fischman.exercise.bootstrap.Bootstrapper;
import com.jeff.fischman.exercise.utility.CannedData;
import com.jeff.fischman.exercise.utility.Printer;
import com.jeff.fischman.exercise.utility.StringUtility;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// **********************************************************************************
// Look in class CannedData to see the input and the corresponding expected output
// associated with each of these tests.
// **********************************************************************************
//
public class EndToEndIntegrationTests {
    // This test is one that matches the sample in the problem write-up

    @Test
    public void integrationTestWithWriteupSampleData() {
        runFullIntegrationTest(CannedData._writeupSampleInput,
                               CannedData._writeupSampleOutput);
    }

    private List<String> _emptyListOutput = Arrays.asList(
            "Output",
            "------",
            "SELLS:",
            "BUYS:",
            "ERRORS:"
    );

    @Test
    public void integrationTestWithEmptyInput() {
        runFullIntegrationTest(new ArrayList<>(),
                               _emptyListOutput);
    }

    private List<String> _removeScenarioInput = Arrays.asList(
            "A,100000,S,1,1075",   // 0
            "A,100001,B,1,1000",   // 1
            "X,100001,B,1,1000"    // 2
    );

    private List<String> _removeScenarioOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "NAN",                 // 2
            "SELLS:",              // Finish
            "1075,1",
            "BUYS:",
            "ERRORS:"
    );


    @Test
    public void integrationTestWithRemoveScenarioInput() {
        runFullIntegrationTest(_removeScenarioInput,
                               _removeScenarioOutput);
    }

    private List<String> _modifyScenarioInput = Arrays.asList(
            "A,100000,S,2,1075",   // 0
            "A,100001,B,1,1000",   // 1
            "M,100000,S,1,1075"    // 2
    );

    private List<String> _modifyScenarioOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "1037.5",              // 2
            "SELLS:",              // Finish
            "1075,1",
            "BUYS:",
            "1000,1",
            "ERRORS:"
    );


    @Test
    public void integrationTestWithModifyScenarioInput() {
        runFullIntegrationTest(_modifyScenarioInput,
                               _modifyScenarioOutput);
    }

    // Since 0 is an illegal quantity, this gets treated like a parse error
    private List<String> _modifyToZeroScenarioInput = Arrays.asList(
            "A,100000,S,2,1075",   // 0
            "A,100001,B,1,1000",   // 1
            "M,100000,S,0,1075"    // 2
    );

    private List<String> _modifyToZeroScenarioOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "1037.5",              // 2
            "SELLS:",              // Finish
            "1075,2",
            "BUYS:",
            "1000,1",
            "ERRORS:",
            "f,1"
    );


    @Test
    public void integrationTestWithModifyToZeroScenarioInput() {
        runFullIntegrationTest(_modifyToZeroScenarioInput,
                               _modifyToZeroScenarioOutput);
    }


    private List<String> _tradeScenarioInput = Arrays.asList(
            "A,100000,S,1,1075",   // 0
            "A,100001,B,1,1000",   // 1
            "A,100002,B,9,1000",   // 2
            "A,100003,S,6,1050",   // 3
            "A,100004,S,6,1000",   // 4
            "T,1,1000",            // 5
            "T,5,1000",            // 6
            "X,100001,B,1,1000",   // 7
            "M,100002,B,4,1000",   // 8
            "X,100004,S,6,1000"    // 9
            );

    private List<String> _tradeScenarioOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "1037.5",              // 2
            "1025",                // 3
            "1000",                // 4
            "1000",                // 5
            "1@1000",              // 5
            "1000",                // 6
            "6@1000",              // 6
            "1000",                // 7
            "1000",                // 8
            "1025",                // 9
            "SELLS:",              // Finish
            "1075,1",
            "1050,6",
            "BUYS:",
            "1000,4",
            "ERRORS:"
    );


    @Test
    public void integrationTestWithTradeScenarioInput() {
        runFullIntegrationTest(_tradeScenarioInput,
                               _tradeScenarioOutput);
    }


    private List<String> _buyConsumesSellsMissingMatchMsgsInput = Arrays.asList(
            "A,3,S,2,12",
            "A,4,S,3,12",
            "A,1,S,2,11",
            "A,2,S,3,11",
            "A,5,B,6,12"
    );

    private List<String> _buyConsumesSellsMissingMatchMsgsOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "NAN",
            "NAN",
            "NAN",
            "11.5",
//            "5@11",
//            "1@12",
            "SELLS:",
            "12,2,3",
            "11,2,3",
            "BUYS:",
            "12,6",
            "ERRORS:",
            "e,3",
            "h,4"
    );


    @Test
    public void integrationTestWithBuyConsumesSellsMissingMatchMsgsInput() {
        runFullIntegrationTest(_buyConsumesSellsMissingMatchMsgsInput,
                               _buyConsumesSellsMissingMatchMsgsOutput);
    }


    private List<String> _sellConsumesBuysMissingMatchMsgsInput = Arrays.asList(
            "A,1,B,2,11",
            "A,2,B,3,11",
            "A,3,B,2,12",
            "A,4,B,3,12",
            "A,5,S,6,11"
    );

    private List<String> _sellConsumesBuysMissingMatchMsgsOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "NAN",
            "NAN",
            "NAN",
            "11.5",
            "SELLS:",
            "11,6",
            "BUYS:",
            "12,2,3",
            "11,2,3",
            "ERRORS:",
            "e,3",
            "h,4"
    );


    @Test
    public void integrationTestWithSellConsumesBuyssMissingMatchMsgsInput() {
        runFullIntegrationTest(_sellConsumesBuysMissingMatchMsgsInput,
                               _sellConsumesBuysMissingMatchMsgsOutput);
    }



    private List<String> _buyConsumesSellsMissingMatchMsgsGenmoOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "NAN",
            "NAN",
            "NAN",
            "11.5",
            "SELLS:",
            "12,1,3",
            "BUYS:",
            "ERRORS:",
            "e,3",
            "h,4"
    );

    @Test
    public void integrationTestWithBuyConsumesSellsMissingMatchMsgsInputWithGenmo() {
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.setOption(CmdOption.genmo);
        runFullIntegrationTest(runtimeOptions,
                               _buyConsumesSellsMissingMatchMsgsInput,
                               _buyConsumesSellsMissingMatchMsgsGenmoOutput);
    }

    private List<String> _sellConsumesBuysMissingMatchMsgsGenmoOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "NAN",
            "NAN",
            "NAN",
            "11.5",
            "SELLS:",
            "BUYS:",
            "11,1,3",
            "ERRORS:",
            "e,3",
            "h,4"
    );

    @Test
    public void integrationTestWithSellConsumesBuysMissingMatchMsgsInputWithGenmo() {
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.setOption(CmdOption.genmo);
        runFullIntegrationTest(runtimeOptions,
                               _sellConsumesBuysMissingMatchMsgsInput,
                               _sellConsumesBuysMissingMatchMsgsGenmoOutput);
    }


    private List<String> _buyConsumesSellsGoodInput = Arrays.asList(
            "A,1,S,2,12",    // 0
            "A,2,S,3,12",    // 1
            "A,3,S,2,11",    // 2
            "A,4,S,3,11",    // 3
            "A,5,B,6,12",    // 4
            "T,2,11",        // 5
            "T,3,11",        // 6
            "T,1,12",        // 7
            "X,3,S,2,11",    // 8
            "X,4,S,3,11",    // 9
            "M,1,S,1,12",    // 10
            "X,5,B,6,12"     // 11

    );
    private List<String> _buyConsumesSellsGoodOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",     // 0
            "NAN",     // 1
            "NAN",     // 2
            "NAN",     // 3
            "11.5",    // 4
            "11.5",    // 5
            "2@11",    // 5
            "11.5",    // 6
            "5@11",    // 6
            "11.5",    // 7
            "1@12",    // 7
            "11.5",    // 8
            "12",      // 9
            "SELLS:",  // 9
            "12,2,3",  // 9
            "BUYS:",   // 9
            "12,6",    // 9
            "12",      // 10
            "NAN",     // 11/
            "SELLS:",  // Finish
            "12,1,3",  // Finish
            "BUYS:",   // Finish
            "ERRORS:"  // Finish
    );

    @Test
    public void integrationTestWithBuyConsumesSellsGoodInput() {
        runFullIntegrationTest(_buyConsumesSellsGoodInput,
                               _buyConsumesSellsGoodOutput);
    }

    private List<String> _sellConsumesBuysGoodInput = Arrays.asList(
            "A,1,B,2,11",    // 0
            "A,2,B,3,11",    // 1
            "A,3,B,2,12",    // 2
            "A,4,B,3,12",    // 3
            "A,5,S,6,11",    // 4
            "T,2,12",        // 5
            "T,3,12",        // 6
            "T,1,11",        // 7
            "X,3,B,2,12",    // 8
            "X,4,B,3,12",    // 9
            "M,1,B,1,11",    // 10
            "X,5,S,6,11"     // 11

    );
    private List<String> _sellConsumesBuysGoodOutput = Arrays.asList(
            "Output",
            "------",
            "NAN",     // 0
            "NAN",     // 1
            "NAN",     // 2
            "NAN",     // 3
            "11.5",    // 4
            "11.5",    // 5
            "2@12",    // 5
            "11.5",    // 6
            "5@12",    // 6
            "11.5",    // 7
            "1@11",    // 7
            "11.5",    // 8
            "11",      // 9
            "SELLS:",  // 9
            "11,6",    // 9
            "BUYS:",   // 9
            "11,2,3",  // 9
            "11",      // 10
            "NAN",     // 11/
            "SELLS:",  // Finish
            "BUYS:",   // Finish
            "11,1,3",  // Finish
            "ERRORS:"  // Finish
    );

    @Test
    public void integrationTestWithSellConsumesBuysGoodInput() {
        runFullIntegrationTest(_sellConsumesBuysGoodInput,
                               _sellConsumesBuysGoodOutput);
    }

    private List<String> _parseErrorTypeA_input = Arrays.asList(
            "A,100000,S,1,1075",
            "A,100001,B,9,1000",
            "A,100002,B,1,1000",
            "Q,100002,B,1,1000"    // Bad Action - type 'a' error
    );

    private List<String> _parseErrorTypeA_output = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "1037.5",
            "1037.5",
            "1037.5",
            "SELLS:",
            "1075,1",
            "BUYS:",
            "1000,9,1",
            "ERRORS:",
            "a,1"
    );

    @Test
    public void integrationTestWithTypeA_ParseError() {
        runFullIntegrationTest(_parseErrorTypeA_input, _parseErrorTypeA_output);
    }


    private List<String> _dupOrderIdTypeB_input = Arrays.asList(
            "A,100000,S,1,1075",
            "A,100001,B,9,1000",
            "A,100002,B,1,1000",
            "A,100002,B,1,1025"    // Duplicate order id
    );

    private List<String> _dupOrderIdTypeB_output = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "1037.5",
            "1037.5",
            "1037.5",
            "SELLS:",
            "1075,1",
            "BUYS:",
            "1000,9,1",
            "ERRORS:",
            "b,1"
    );

    @Test
    public void integrationTestWithTypeB_dupOrderIdError() {
        runFullIntegrationTest(_dupOrderIdTypeB_input, _dupOrderIdTypeB_output);
    }


    private List<String> _noTradeMatchTypeC_input = Arrays.asList(
            "A,100000,S,1,1075",
            "A,100001,B,9,1000",
            "A,100002,B,1,1000",
            "T,1,1025"    // Trade with no match
    );

    private List<String> _noTradeMatchTypeC_output = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "1037.5",
            "1037.5",
            "1037.5",
            "SELLS:",
            "1075,1",
            "BUYS:",
            "1000,9,1",
            "ERRORS:",
            "c,1"
    );

    @Test
    public void integrationTestWithTypeC_noTradeMatchError() {
        runFullIntegrationTest(_noTradeMatchTypeC_input, _noTradeMatchTypeC_output);
    }

    private List<String> _noRmvOrderIdTypeD_input = Arrays.asList(
            "A,100000,S,1,1075",
            "A,100001,B,9,1000",
            "A,100002,B,1,1000",
            "X,100003,B,6,1075"    // Rmv with unknown orderid
    );

    private List<String> _noRmvOrderIdTypeD_output = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "1037.5",
            "1037.5",
            "1037.5",
            "SELLS:",
            "1075,1",
            "BUYS:",
            "1000,9,1",
            "ERRORS:",
            "d,1"
    );

    @Test
    public void integrationTestWithTypeD_noRmvOrderIdError() {
        runFullIntegrationTest(_noRmvOrderIdTypeD_input, _noRmvOrderIdTypeD_output);
    }

    private List<String> _missingTradeTypeE_input = Arrays.asList(
            "A,100000,S,1,1075",   // 0
            "A,100001,B,1,1000",   // 1
            "A,100002,B,9,1000",   // 2
            "A,100003,S,6,1050",   // 3
            "A,100004,S,6,1000",   // 4
            //      "T,1,1000",
            //      "T,5,1000",
            "X,100001,B,1,1000",   // 5
            "M,100002,B,4,1000",   // 6
            "X,100004,S,6,1000"    // 7
    );

    private List<String> _missingTradeTypeE_output = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "1037.5",              // 2
            "1025",                // 3
            "1000",                // 4
            "1000",                // 5
            "1000",                // 6
            "1025",                // 7
            "SELLS:",              // Finish
            "1075,1",
            "1050,6",
            "BUYS:",
            "1000,4",
            "ERRORS:",
            "e,2"
    );

    @Test
    public void integrationTestWithTypeE_MissingTrade() {
        runFullIntegrationTest(_missingTradeTypeE_input,
                               _missingTradeTypeE_output);

    }


    private List<String> _parseErrorTypeF_input = Arrays.asList(
            "A,100000,S,1,1075",
            "A,100001,B,9,1000",
            "A,100002,B,1,1000",
            "A,100003,B,1,-1000"    // Negative price - type 'f' error
    );


    private List<String> _parseErrorTypeF_output = Arrays.asList(
            "Output",
            "------",
            "NAN",
            "1037.5",
            "1037.5",
            "1037.5",
            "SELLS:",
            "1075,1",
            "BUYS:",
            "1000,9,1",
            "ERRORS:",
            "f,1"
    );

    @Test
    public void integrationTestWithTypeF_parseError() {
        runFullIntegrationTest(_parseErrorTypeF_input, _parseErrorTypeF_output);
    }

    private List<String> _badRemoveDetailsTypeG_input = Arrays.asList(
            "A,100000,S,1,1075",   // 0
            "A,100001,B,1,1000",   // 1
            "X,100001,B,1,1003"    // 2  Bad Price in Remove
    );

    private List<String> _badRemoveDetailsTypeG_output = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "NAN",                 // 2
            "SELLS:",              // Finish
            "1075,1",
            "BUYS:",               // Note removal still takes place
            "ERRORS:",
            "g,1"                  // But is flagged as a type 'g' error
    );


    @Test
    public void integrationTestWithTypeG_badRemoveDetails() {
        runFullIntegrationTest(_badRemoveDetailsTypeG_input,
                               _badRemoveDetailsTypeG_output);
    }

    private List<String> _missingChangeOrderTypeH_input = Arrays.asList(
            "A,100000,S,1,1075",   // 0
            "A,100001,B,1,1000",   // 1
            "A,100002,B,9,1000",   // 2
            "A,100003,S,6,1050",   // 3
            "A,100004,S,6,1000",   // 4
            "T,1,1000",            // 5
            "T,5,1000"             // 6
//          "X,100001,B,1,1000",   // 7
//          "M,100002,B,4,1000",   // 8
//          "X,100004,S,6,1000"    // 9
    );

    private List<String> _missingChangeOrderTypeH_output = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "1037.5",              // 2
            "1025",                // 3
            "1000",                // 4
            "1000",                // 5
            "1@1000",              // 5
            "1000",                // 6
            "6@1000",              // 6
            "SELLS:",              // Finish
            "1075,1",
            "1050,6",
            "1000,6",
            "BUYS:",
            "1000,1,9",
            "ERRORS:",
            "h,3"
    );

    @Test
    public void integrationTestWithTypeH_missingChangeOrder() {
        // with the missing cleanup Modify and Removes are book is
        // stuck in a bad place.
        //
        runFullIntegrationTest(_missingChangeOrderTypeH_input,
                               _missingChangeOrderTypeH_output);

    }

    // With genmo, the book is cleaned up as if the missing modifys and
    // cancels had been supplied.
    //
    private List<String> _missingChangeOrderWithGenmoTypeH_output = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1037.5",              // 1
            "1037.5",              // 2
            "1025",                // 3
            "1000",                // 4
            "1000",                // 5
            "1@1000",              // 5
            "1000",                // 6
            "6@1000",              // 6
            "SELLS:",              // Finish
            "1075,1",
            "1050,6",
            "BUYS:",
            "1000,4",
            "ERRORS:",
            "h,3"
    );

    @Test
    public void integrationTestWithTypeH_missingChangeOrderWithGenmo() {
        // with the missing cleanup Modify and Removes are book is
        // stuck in a bad place.
        //
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.setOption(CmdOption.genmo);
        runFullIntegrationTest(runtimeOptions,
                               _missingChangeOrderTypeH_input,
                               _missingChangeOrderWithGenmoTypeH_output);

    }


    private List<String> _complexModificationTypeI_input = Arrays.asList(
            "A,100000,S,1,1075",   // 0
            "A,100001,B,1,1025",   // 0
            "M,100000,S,1,1035"    // 1  Price change to 1035
    );

    private List<String> _illegalComplexModificationTypeI_output = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1050",                // 1
            "1050",                // 2
            "SELLS:",              // Finish
            "1075,1",
            "BUYS:",
            "1025,1",
            "ERRORS:",
            "i,1"                  // But is flagged as a type 'g' error
    );

    @Test
    public void integrationTestWithTypeI_illegalComplexModification() {
        // with the missing cleanup Modify and Removes are book is
        // stuck in a bad place.
        //
        runFullIntegrationTest(_complexModificationTypeI_input,
                               _illegalComplexModificationTypeI_output);

    }

    private List<String> _legalComplexModificationWithCplxm_output = Arrays.asList(
            "Output",
            "------",
            "NAN",                 // 0
            "1050",                // 1
            "1030",                // 2
            "SELLS:",              // Finish
            "1035,1",
            "BUYS:",
            "1025,1",
            "ERRORS:"
    );

    @Test
    public void integrationTestWithLegalComplexModification() {
        // with the missing cleanup Modify and Removes are book is
        // stuck in a bad place.
        //
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.setOption(CmdOption.cplxm);
        runFullIntegrationTest(runtimeOptions,
                               _complexModificationTypeI_input,
                               _legalComplexModificationWithCplxm_output);

    }

    private void runFullIntegrationTest(List<String> inputList,
                                        List<String> expectedOutputList)
    {
        runFullIntegrationTest(new RuntimeOptions(),
                               inputList,
                               expectedOutputList);
    }

    private void runFullIntegrationTest(RuntimeOptions runtimeOptions,
                                        List<String> inputList,
                                        List<String> expectedOutputList)
    {
        Bootstrapper.Test bootstrapper = new Bootstrapper.Test(inputList, runtimeOptions);
        Processor processor = bootstrapper.create();
        processor.processMessages();
        Printer.CapturePrinter capturePrinter = bootstrapper.getCapturePrinter();
        String actualOutput = capturePrinter.getOutput();
        String expectedOutput = StringUtility.toMulitLineString(expectedOutputList);
        Assert.assertEquals(expectedOutput, actualOutput);
    }


}
