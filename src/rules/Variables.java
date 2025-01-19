package rules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables {
    //constants
    private static final String INT_VARIABLE =
            "^(final\\s*)?(int)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s" +
                    "*(=\\s*[+-]?\\d+)?(\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*[+-]?\\d+)?)*\\s*;$";

    private static final String DOUBLE_VARIABLE =
            "^(final\\s*)?(double)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*-?\\d+(\\.\\d+)?|\\s*)(" +
                    "(\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*-?\\d+(\\.\\d+)?)*\\s*)*)\\s*;$";

    private static final String STRING_VARIABLE =
            "^(final\\s*)?(String)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*\"[^\"]*\"|\\s*)(" +
                    "(\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*\"[^\"]*\")*\\s*)*)\\s*;$";

    private static final String BOOLEAN_VARIABLE =
            "^(final\\s*)?(boolean)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*(true|false|[-+]?\\d+(\\.\\d+)?))?" +
                    "(\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*(true|false|[-+]?\\d+(\\.\\d+)?))?\\s*)*\\s*;$";


    private static final String CHAR_VARIABLE =
            "^(final\\s*)?(char)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*'[^\n']{1}'|\\s*)(" +
                    "(\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*'[^\n']{1}')*\\s*)*)\\s*;$";





    private static final String FINAL_VARIABLE = "^\\s*final\\b.*";

    private static final String FINAL_NAME_VARIABLE = "\\bfinal\\s+\\w+\\s+(\\w+)";


    private static final String VARIABLE_TYPE = "^(final|int|double|String|boolean|char)\\b.*";
    private List<String> variablesLines;
    private List<String> finalVariables = new ArrayList<>();
    private List<String> intVariables = new ArrayList<>();
    private List<String> doubleVariables = new ArrayList<>();
    private List<String> stringVariables = new ArrayList<>();
    private List<String> booleanVariables = new ArrayList<>();
    private List<String> charVariables = new ArrayList<>();


    //Constructor
    public Variables() {
    }

    public boolean Variables(List<String> lines) {
        List<String> variablesLines = new ArrayList<>();
        Pattern pattern = Pattern.compile(VARIABLE_TYPE);
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                variablesLines.add(line);
            }
        }
        this.variablesLines = variablesLines;
        this.finalVariables = new ArrayList<>();
        this.intVariables = new ArrayList<>();
        this.doubleVariables = new ArrayList<>();
        this.stringVariables = new ArrayList<>();
        this.booleanVariables = new ArrayList<>();
        this.charVariables = new ArrayList<>();
        sortVariables();
        return validIntVariables() && validDoubleVariables() && validStringVariables() && validBooleanVariables() && validCharVariables() && validFinalVariables();
    }


    public void sortVariables() {
        for (String line : variablesLines) {
            if (line.contains("final")) {
                finalVariables.add(line);
            }
            if (line.contains("int")) {
                intVariables.add(line);
            } else if (line.contains("double")) {
                doubleVariables.add(line);
            } else if (line.contains("String")) {
                stringVariables.add(line);
            } else if (line.contains("boolean")) {
                booleanVariables.add(line);
            } else if (line.contains("char")) {
                charVariables.add(line);
            }
        }
    }

    public boolean validIntVariables() {
        for (String line : intVariables) {
            //syntax check
            Pattern pattern = Pattern.compile(INT_VARIABLE);
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                System.out.println(line);
                System.out.println("Valid int variables error");
                return false;
            }
        }
        return true;
    }

    public boolean validDoubleVariables() {
        for (String line : doubleVariables) {
            // syntax check
            Pattern pattern = Pattern.compile(DOUBLE_VARIABLE);
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                System.out.println("Valid double variables error");
                return false;
            }
        }
        return true;
    }

    public boolean validStringVariables() {
        for (String line : stringVariables) {
            // syntax check
            Pattern pattern = Pattern.compile(STRING_VARIABLE);
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                System.out.println("Valid string variables error");
                return false;
            }
        }
        return true;
    }

    public boolean validBooleanVariables() {
        for (String line : booleanVariables) {
            // syntax check
            Pattern pattern = Pattern.compile(BOOLEAN_VARIABLE);
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                System.out.println("Valid boolean variables error");
                return false;
            }
        }
        return true;
    }

    public boolean validCharVariables() {
        for (String line : charVariables) {
            // syntax check
            Pattern pattern = Pattern.compile(CHAR_VARIABLE);
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                System.out.println("Valid char variables error");
                return false;
            }
        }
        return validFinalVariables();
    }

    public boolean validFinalVariables() {
        for (String line : finalVariables) {
            // syntax check
            Pattern pattern = Pattern.compile(FINAL_VARIABLE);
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                System.out.println("Valid final variables error");
                return false;
            }
        }
        return true;
    }
}

