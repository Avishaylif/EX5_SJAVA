    package Methods;

    import Conditions.ConditionValidator;
    import VariablesManegment.SymbolsTable;
    import VariablesManegment.Variable;
    import VariablesManegment.VariableValidator;
    import java.util.List;
    import java.util.Map;


    /**
     * Validates methods by ensuring correct variable declarations, assignments, function calls,
     * and condition handling within different scopes.
     *
     * <p>The {@code MethodValidator} class utilizes various validators and a symbol table to
     * analyze and validate the structure and content of methods. It processes each method
     * by opening appropriate scopes, validating parameters, and iterating through each line
     * of the method body to perform necessary validations.</p>
     *
     */
    public class MethodValidator {


        /**
         * A map associating method names to their corresponding {@link MethodData} objects.
         * Used to retrieve method parameters and bodies for validation.
         */
        private final Map<String, MethodData> methods;

        /** Parser to analyze and categorize lines within method bodies. */
        private final MethodParser methodParser;

        /** Symbol table managing variable scopes and declarations within methods. */
        private final SymbolsTable symbolsTable;

        /** Validator for function calls within methods. */
        private final FunctionCallValidator functionCallValidator;
        /** Validator for conditions (e.g., if statements) within methods. */
        private final ConditionValidator conditionValidator;

        /** Validator for variable declarations and assignments within methods. */
        private final VariableValidator variableValidator;

        /**
         * Constructs a new {@code MethodValidator} with the specified dependencies.
         *
         * @param methods                a map of method names to {@link MethodData} objects
         * @param symbolsTable           the symbol table managing variable scopes
         * @param functionCallValidator  the validator for function calls
         * @param conditionValidator     the validator for conditions
         * @param variableValidator      the validator for variables
         */
        public MethodValidator(Map<String, MethodData> methods,
                               SymbolsTable symbolsTable,
                               FunctionCallValidator functionCallValidator,
                               ConditionValidator conditionValidator,
                               VariableValidator variableValidator) {
            this.methodParser = new MethodParser();
            this.methods = methods;
            this.symbolsTable = symbolsTable;
            this.functionCallValidator = functionCallValidator;
            this.conditionValidator = conditionValidator;
            this.variableValidator = variableValidator;
        }

        /**
         * Validates all methods present in the {@code methods} map.
         *
         * <p>Iterates through each {@link MethodData} object and invokes {@link #validateMethod(MethodData)}
         * to perform individual method validations.</p>
         *
         * @throws Exception if any method validation fails
         */
        public void validateAllMethods() throws Exception {
            for (MethodData methodData : methods.values()) {
                validateMethod(methodData);
            }
        }

        /**
         * Validates a single method by performing the following steps:
         * <ol>
         *     <li>Opens a new scope and adds method parameters as local variables.</li>
         *     <li>Parses the method body and categorizes each line.</li>
         *     <li>Iterates through each line, performing validations based on line type.</li>
         *     <li>Ensures all opened scopes are properly closed at the end of the method.</li>
         * </ol>
         *
         * @param methodData the {@link MethodData} object representing the method to validate
         * @throws Exception if any validation step fails, such as unmatched braces or invalid lines
         */
        public void validateMethod(MethodData methodData) throws Exception {
            // 1. Open a new scope for method parameters
            symbolsTable.openScope();
            // Add method parameters as local variables
            for (Variable param : methodData.getMethodParameters()) {
                symbolsTable.addVariable(param);
                // Assumes parameters are correctly constructed (name, type, isFinal, isInitialized, etc.)
            }

            // 2. Retrieve the method body and parse line types
            List<String> body = methodData.getBody();
            List<MethodParser.LineType> lineTypes = methodParser.parseMethod(body);

            int blockDepth = 1; // Counter for nested blocks

            // 3. Iterate through each line of the method body
            for (int i = 0; i < body.size(); i++) {
                String line = body.get(i).trim();
                MethodParser.LineType lineType = lineTypes.get(i);

                switch (lineType) {
                    case FUNCTION_CALL:
                        // 3. Iterate through each line of the method body
                        functionCallValidator.validateFunctionCall(extractFunctionName(line),
                                extractArguments(line));
                        break;

                    case CONDITION_START:
                        // Example line: "if (x > 5) {"
                        String conditionExpr = extractConditionExpression(line);
                        conditionValidator.validateCondition(conditionExpr);
                        //Open a new scope for the condition block
                        symbolsTable.openScope();
                        blockDepth++;
                        break;

                    case END_BLOCK:
                        // Close the current scope
                        if (blockDepth == 0) {
                            throw new Exception("Unmatched closing brace for condition.");
                        }
                        symbolsTable.closeScope();
                        blockDepth--;
                        break;

                    case VARIABLE_DECLARATION:
                    case VARIABLE_ASSIGNMENT:
                        // Handle variable declaration or assignment
                        variableValidator.handleDeclarationOrAssignment(line);
                        break;
                    case RETURN_STATEMENT:
                        break;
                    default:
                        // Unknown or invalid line
                        throw new Exception("Unknown or invalid line: " + line);
                }
            }

            // 4. Ensure all opened blocks are closed
            if (blockDepth != 0) {
                throw new Exception("Unclosed block(s) in method: " + methodData.getMethodName());
            }

        }


        // -----------------------------------------------------------
        // Helper Methods for Extracting Information from Lines
        // ---------------------------------------------------------

        /**
         * Extracts the function name from a function call line.
         *
         * @param line the line containing the function call
         * @return the name of the function being called
         */
        private String extractFunctionName(String line) {
            // Assumes MethodParser has already validated the line
            int idx = line.indexOf('(');
            if (idx < 0) return line;
            return line.substring(0, idx).trim();
        }


        /**
         * Extracts the arguments from a function call line.
         *
         * @param line the line containing the function call
         * @return a string representing the arguments within the parentheses
         */
        private String extractArguments(String line) {
            // Example: "foo(1,2)" -> returns "1,2"
            int start = line.indexOf("(");
            int end = line.lastIndexOf(")");
            if (start < 0 || end < 0 || end <= start) return "";
            return line.substring(start + 1, end).trim();
        }

        /**
         * Extracts the condition expression from a condition start line.
         *
         * @param line the line containing the condition (e.g., "if (x > 5) {")
         * @return the condition expression within the parentheses
         */
        private String extractConditionExpression(String line) {
            // Example: "if (x > 5) {"
            int open = line.indexOf("(");
            int close = line.lastIndexOf(")");
            if (open < 0 || close < 0 || close <= open) {
                return ""; // Alternatively, throw an exception
            }
            return line.substring(open + 1, close).trim();
        }
    }


