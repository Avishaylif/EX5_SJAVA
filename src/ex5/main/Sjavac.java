package ex5.main;


import Conditions.ConditionValidator;
import Methods.FunctionCallValidator;
import Methods.MethodData;
import Methods.MethodValidator;
import VariablesManegment.SymbolsTable;
import VariablesManegment.Variable;
import VariablesManegment.VariableValidator;
import parser.SJavaFileParser;
import rules.Variables;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import parser.VariablesAndMethodsParser;

public class Sjavac {
    private static final String VALID_PATH = "^([a-zA-Z]:\\\\|/)?([^<>:\"|?*\\r\\n]+/)*([^<>:\"|?*\\r\\n]+)?$";


    public static void main(String[] args) throws Exception {
        if (!validFile(args)) {
            System.out.println(1);
            System.exit(0);
        }

        String sourceFileName = args[0];
        List<String> lines = SJavaFileParser.readFileToList(sourceFileName);
        if (SJavaFileParser.INVALID_LINES > 0) {
            System.out.println("Nums invalid lines: " + SJavaFileParser.INVALID_LINES);
            System.out.println("invalid line");
            System.exit(0);
        }

        VariablesAndMethodsParser variablesAndMethodsParser = new VariablesAndMethodsParser();
        variablesAndMethodsParser.parseLines(lines);
        List<String> globalVariables = variablesAndMethodsParser.getGlobalVariables();
        Map<String, MethodData> methods = variablesAndMethodsParser.getMethods();
        SymbolsTable symbolsTable = new SymbolsTable();

        VariableValidator variableValidator = new VariableValidator(globalVariables,symbolsTable);
        FunctionCallValidator functionCallValidator = new FunctionCallValidator(methods, variableValidator);
        ConditionValidator conditionValidator = new ConditionValidator(symbolsTable);

        MethodValidator methodValidator = new MethodValidator(methods,
                symbolsTable,
                functionCallValidator,
                conditionValidator,
                variableValidator);
        //try {
            methodValidator.validateAllMethods();
        //} catch (Exception e) {
        //    System.out.println(e.getMessage());
        //    System.out.println("1");
        //}


        }

        //TEST
        /**
        SymbolsTable symbolsTable = variableValidator.getSymbolsTable();
        for (Map<String, Variable> scope : symbolsTable) {

            System.out.println("scope: ");
            for (Map.Entry<String, Variable> entry : scope.entrySet()) {
                String varName = entry.getKey();
                Variable var = entry.getValue();
                System.out.println("  " + varName + " = " + var.toString());

            }
        }
         */


        /*
        Variables variables = new Variables();
        if (!variables.Variables(lines)) {
            System.out.println(3);
            System.exit(0);
        }
        System.out.println(0);

         */


    public static boolean validFile(String[] sourceFileName) {
        if (sourceFileName.length != 1) {
            return false;
        }
        return Pattern.matches(VALID_PATH, sourceFileName[0]);
    }
}