package Methods;

import VariablesManegment.Variable;
import VariablesManegment.VariableValidator;
import errors.ValidationException;
import java.util.List;
import java.util.Map;

/**
 * A class responsible for validating function calls.
 */
public class FunctionCallValidator {
    private final Map<String, MethodData> methods;
    private final VariableValidator variableValidator;

    /**
     * Constructs a new {@code FunctionCallValidator} with the specified methods and variable validator.
     *
     * @param methods the methods to validate function calls against
     * @param variableValidator the variable validator to use for validating function call arguments
     * @throws Exception if an error occurs during the validation of the function call
     */
    public FunctionCallValidator
            (Map<String, MethodData> methods, VariableValidator variableValidator) throws Exception {
        this.methods = methods;
        this.variableValidator = variableValidator;
    }

    /**
     * Validates the given function call with the specified function name and arguments.
     *
     * <p>The function call is checked for the following:
     *  - The function name exists in the methods map.
     *  - The number of arguments matches the number of parameters in the function.
     *  - Each argument is a valid value of the expected type.
     *
     * @param functionName the name of the function to validate
     * @param arguments the arguments to validate
     * @throws ValidationException if the function call is invalid
     */
    public void validateFunctionCall(String functionName, String arguments) throws ValidationException {
        MethodData methodData = methods.get(functionName);
        if (methodData==null) {
            throw new IllegalStateException("Function not found: " + functionName);
        }
        List<Variable> expectedParams = methodData.getMethodParameters();
        String[] providedArgs = arguments.isEmpty() ? new String[0] : arguments.split(",");

        if (providedArgs.length != expectedParams.size()) {
            throw new IllegalStateException("Parameter count mismatch for function: " + functionName);
        }

        for (int i = 0; i < providedArgs.length; i++) {
            String providedArg = providedArgs[i].trim();
            Variable parameter = expectedParams.get(i);
            Variable.Type expectedType = parameter.getType();

            variableValidator.parseAndValidateValue(providedArg, expectedType);
        }
    }


}

