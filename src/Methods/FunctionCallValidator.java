package Methods;

import VariablesManegment.Variable;
import VariablesManegment.VariableValidator;
import errors.ValidationException;

import java.util.List;
import java.util.Map;

public class FunctionCallValidator {

    private final Map<String, MethodData> methods;
    private final VariableValidator variableValidator;

    public FunctionCallValidator(Map<String, MethodData> methods, VariableValidator variableValidator) throws Exception {
        this.methods = methods;
        this.variableValidator = variableValidator;
    }

    // Validate a function call
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

