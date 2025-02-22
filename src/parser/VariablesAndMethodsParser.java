package parser;

import Methods.MethodData;
import VariablesManegment.Variable;
import VariablesManegment.VariableValidator;
import errors.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class responsible for parsing global variables and methods from a list of lines.
 */
public class VariablesAndMethodsParser {
    // Regular expressions for global variables, method definitions, and blocks
    private static final String METHOD_DEFINITION =
            "^void\\s+([a-zA-Z][a-zA-Z0-9_]*)\\s*\\((.*?)\\)\\s*\\{\\s*$";//TODO: check
        private static final String CONDITION_OR_LOOP =
                "^\\s*(if|while)\\s*\\(.*\\)\\s*\\{\\s*$"; //TODO: check if posiible to be "{" in a new line
    private static final String END_BLOCK = "\\s*\\}\\s*$";
    private static final String PARAMETER_PATTERN =
            "^(final\\s+)?(int|double|String|boolean|char)\\s+([a-zA-Z_][a-zA-Z0-9_]*)$";

    // Data structures for parsed results
    private final List<String> globalVariables = new ArrayList<>();
    private final Map<String, MethodData> methods = new HashMap<>();


    /**
     * Parses a list of lines to extract global variables and methods.
     *
     * @param lines A list of strings, each representing a line of code in the program.
     * @throws ValidationException if an error occurs during the parsing of the input lines.
     */
    public void parseLines(List<String> lines) throws ValidationException {
        boolean inMethod = false;
        int blockDepth = 0;
        String currentMethod = null;
        List<String> currentMethodLines = new ArrayList<>();
        String methodDefinitionLine = null;
        for (String line : lines) {
            if (line.matches(METHOD_DEFINITION)) {
                if (inMethod) {
                    throw new IllegalStateException("Nested method definitions are not allowed: " + line);
                }
                inMethod = true;
                blockDepth = 1;
                currentMethod = extractMethodName(line);
                currentMethodLines = new ArrayList<>();
                methodDefinitionLine = line;
                //currentMethodLines.add(line);//TODO: could create problems
            } else if (line.matches(CONDITION_OR_LOOP)) {
                if (!inMethod) {
                    throw new IllegalStateException("Condition or loop outside of a method is not allowed: " + line);
                }
                blockDepth++;
                currentMethodLines.add(line);
            } else if (line.matches(END_BLOCK)) {
                if (blockDepth > 0) {
                    blockDepth--;
                    currentMethodLines.add(line);
                    if (blockDepth == 0 && inMethod) {
                        //String methodDefinitionLine = currentMethodLines.get(0);
                        List<Variable> methodParameters = validateAndStoreMethod( methodDefinitionLine);
                        MethodData methodData = new MethodData(currentMethod,methodParameters,currentMethodLines);
                        methods.put(currentMethod, methodData);
                        inMethod = false;
                    }
                } else {

                    throw new IllegalStateException("Unexpected closing block: " + line);
                }
            } else if (inMethod) {
                currentMethodLines.add(line);
            } else if (!line.isBlank() && !inMethod && blockDepth == 0) {
                    globalVariables.add(line);
            }
        }
        if (blockDepth != 0) {
            throw new IllegalStateException("Unclosed block detected.");
        }

    }

    // Extract method name from a method definition line
    private String extractMethodName(String line)  {
        String[] parts = line.split("\\s+|\\(");
        for (int i = 0; i < parts.length; i++) {
            if ("void".equals(parts[i])) {
                return parts[i + 1];
            }
        }
        throw new IllegalStateException("Method with no name"); //TODO: throw exception
    }

    /**
     * Returns the list of global variables parsed from the input lines.
     *
     * @return A list of strings, each representing a global variable declaration.
     */
    public List<String> getGlobalVariables() {
        return globalVariables;
    }
    /**
     * Returns the map of methods parsed from the input lines.
     *
     * @return A map of method names to {@link MethodData} objects.
     */
    public Map<String, MethodData> getMethods() {
        return methods;
    }

    /**
     * Validates and stores a method definition line.
     *
     * @param line A string representing a method definition line.
     * @return A list of {@link Variable} objects representing the method parameters.
     * @throws ValidationException if the method definition is invalid.
     */
    public List<Variable> validateAndStoreMethod(String line) throws ValidationException {
        Pattern pattern = Pattern.compile(METHOD_DEFINITION);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String methodName = matcher.group(1);
            String parameters = matcher.group(2);

            // Validate method name
            //todo: check if number are valid in method name, amf if this checking is necessary at all
            if (!methodName.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
                throw new IllegalStateException("Invalid method name: " + methodName);
            }

            if (methods.containsKey(methodName)){
                throw new IllegalStateException("Duplicates methods are not allowed");
            }
            // Validate and parse parameters
            List<Variable> parameterList = new ArrayList<>();
            if (!parameters.isBlank()) {
                String[] paramArray = parameters.split(",");
                for (String param : paramArray) {
                    Matcher parameterMatcher = Pattern.compile(PARAMETER_PATTERN).matcher(param.trim());
                    if (!parameterMatcher.find()) {
                        //It's failed also at the case "(Parm parm,)"
                        throw new IllegalStateException("Invalid parameter: " + param);
                    }
                    String finalKeyword = parameterMatcher.group(1);
                    String type = parameterMatcher.group(2);
                    String varName = parameterMatcher.group(3);

                    Variable parameter = new Variable(
                            varName,
                            type,
                            finalKeyword != null ? true : false,
                            false,
                           null);
                    parameterList.add(parameter);
                }
            }

            // Store method and its parameters
            return parameterList;
        } else {
            throw new IllegalStateException("Invalid method declaration: " + line);
        }
    }
}
