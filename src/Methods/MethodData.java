package Methods;

import VariablesManegment.Variable;

import java.util.List;
/**
 * Represents the data structure for a method, encapsulating its name, parameters, and body.
 * This class is used to store information about a method in a program, including its signature (name and parameters)
 * and the body of the method (its code).
 *
 * <p>It provides getter methods to access the method's name, parameters, and body, which are useful for
 * further processing such as method invocation, analysis, or modification.
 */
public class MethodData {
    private final String methodName;
    private final List<Variable> methodParameters;
    private final List<String> body;
    /**
     * Constructs a new MethodData instance.
     *
     * @param methodName The name of the method.
     * @param methodParameters A list of {@link Variable} objects representing the parameters of the method.
     * @param body A list of strings representing the lines of code (the body) of the method.
     */
    public MethodData(String methodName,
                           List<Variable> methodParameters,
                      List<String> body) {
        this.methodName = methodName;
        this.methodParameters = methodParameters;
        this.body = body;
    }

    /**
     * Retrieves the body (lines of code) of the method.
     *
     * @return A list of strings representing the body of the method.
     */
    public List<String> getBody() {
        return body;
    }

    /**
     * Retrieves the parameters of the method.
     *
     * @return A list of {@link Variable} objects representing the method's parameters.
     */
    public List<Variable> getMethodParameters() {
        return methodParameters;
    }
    /**
     * Retrieves the name of the method.
     *
     * @return A string representing the name of the method.
     */
    public String getMethodName() {
        return methodName;
    }

}
