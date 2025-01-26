package Methods;

import VariablesManegment.Variable;

import java.util.List;

public class MethodData {
    private final String methodName;
    private final List<Variable> methodParameters;
    private final List<String> body;

    public MethodData(String methodName,
                           List<Variable> methodParameters,
                      List<String> body) {
        this.methodName = methodName;
        this.methodParameters = methodParameters;
        this.body = body;
    }

    public List<String> getBody() {
        return body;
    }

    public List<Variable> getMethodParameters() {
        return methodParameters;
    }

    public String getMethodName() {
        return methodName;
    }

}
