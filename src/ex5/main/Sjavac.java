package ex5.main;


import parser.SJavaFileParser;
import rules.Variables;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class Sjavac {
    private static final String VALID_PATH = "^([a-zA-Z]:\\\\|/)?([^<>:\"|?*\\r\\n]+/)*([^<>:\"|?*\\r\\n]+)?$";


    public static void main(String[] args) throws IOException {
        if (!validFile(args)) {
            System.out.println(1);
            System.exit(0);
        }

        String sourceFileName = args[0];
        List<String> lines = SJavaFileParser.readFileToList(sourceFileName);
        if (SJavaFileParser.INVALID_LINES > 0) {
            System.out.println("Nums invalid lines: " + SJavaFileParser.INVALID_LINES );
            System.out.println("invalid line");
            System.exit(0);
        }
        Variables variables = new Variables();
        if (!variables.Variables(lines)) {
            System.out.println(3);
            System.exit(0);
        }
        System.out.println(0);
    }

    public static boolean validFile(String[] sourceFileName) {
        if (sourceFileName.length != 1) {
            return false;
        }
        return Pattern.matches(VALID_PATH, sourceFileName[0]);
    }
}