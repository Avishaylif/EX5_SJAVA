package rules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variables {
    //constants

    //regex for type var of S-JAVA(int, double, String, boolean, char)
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

    private static final String VALID_ASSIGMENT = "(\\b[a-zA-Z_][a-zA-Z0-9_]*\\b)(\\s*=\\s*[^,;]*)?";


    //List for each type of variable
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

    /**
     * Check if the variables in the file are valid
     * @param lines
     * @return true if the variables are valid
     */
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
        return validIntVariables() && validDoubleVariables() && validStringVariables() &&
        validBooleanVariables() && validCharVariables() && validFinalVariables() && duplicateVariableName();
    }


    private void sortVariables() {
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

    private boolean validIntVariables() {
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

    private boolean validDoubleVariables() {
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

    private boolean validStringVariables() {
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

    private boolean validBooleanVariables() {
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

    private boolean validCharVariables() {
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

    private boolean validFinalVariables() {
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

    private boolean duplicateVariableName() {
        List<String> variableNames = extractVariableNames(String.valueOf(finalVariables));
        for (String var: variableNames) {
            int count = 0;
            for(String line: variablesLines){
                if(line.contains(var)){
                    count++;
                    if (count > 1) {
                        System.out.println(count);
                        System.out.println(var);
                        System.out.println("Duplicate assignment for final var error");
                        return false;
                    }
                }
            }
        }
        return hasAssignmentForAllVariables(String.valueOf(finalVariables));
    }

    private static List<String> extractVariableNames(String line) {
        List<String> variableNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b");
        Matcher matcher = pattern.matcher(line);
        boolean insideAssignment = false;
        while (matcher.find()) {
            String match = matcher.group();
            if ("final".equals(match) || "int".equals(match) || "double".equals(match) ||
                    "String".equals(match) || "boolean".equals(match) || "char".equals(match)) {
                continue;
            }
            if (insideAssignment && "=".equals(line.substring(matcher.start() - 1, matcher.start()).trim())) {
                insideAssignment = false;
                continue;
            }
            variableNames.add(match);
            insideAssignment = line.charAt(matcher.end()) == '=';
        }
        return variableNames;
    }

    private static boolean hasAssignmentForAllVariables(String line) {
        Pattern pattern = Pattern.compile(VALID_ASSIGMENT);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            if (matcher.group(2) == null) {
                System.out.println("Missing assignment for final var error");
                return false;
            }
        }
        return true;
    }

}

