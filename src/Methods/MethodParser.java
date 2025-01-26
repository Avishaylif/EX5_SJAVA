package Methods;

import java.util.ArrayList;
import java.util.List;


public class MethodParser {

    // Regular expressions for line types
    private static final String VARIABLE_DECLARATION = "^(final\\s+)?(int|double|String|boolean|char)\\s+[a-zA-Z_][a-zA-Z0-9_]*(\\s*=\\s*.+)?;$";
    private static final String VARIABLE_ASSIGNMENT = "^[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*.+;$"; //TODO: check if covers all the options
    private static final String FUNCTION_CALL = "^[a-zA-Z][a-zA-Z0-9_]*\\s*\\(.*\\)\\s*;$";
    private static final String CONDITION = "^(if|while)\\s*\\(.*\\)\\s*\\{?$";
    private static final String END_BLOCK = "^\\}\\s*$";
    private static final String RETURN_STATEMENT = "^return;$";

    public enum LineType {
        VARIABLE_DECLARATION,
        VARIABLE_ASSIGNMENT,
        FUNCTION_CALL,
        CONDITION_START,
        END_BLOCK,
        RETURN_STATEMENT
    }

    // Parse the lines of a method
    public List<LineType> parseMethod(List<String> methodLines) {
        List<LineType> lineTypes = new ArrayList<>();

        for (int i = 0; i < methodLines.size(); i++) {//without the decleration itself. TODO: magic number
            String line = methodLines.get(i).trim();

            if (i == methodLines.size() - 2 && !line.matches(RETURN_STATEMENT)) {//TODO: magic number
                throw new IllegalStateException("The second-to-last line must be 'return;': " + line);
            }

            LineType type = classifyLine(line);
            lineTypes.add(type);
        }

        return lineTypes;
    }

    // Classify a single line
    private LineType classifyLine(String line) {
        if (line.matches(VARIABLE_DECLARATION)) {
            return LineType.VARIABLE_DECLARATION;
        } else if (line.matches(VARIABLE_ASSIGNMENT)) {
            return LineType.VARIABLE_ASSIGNMENT;
        } else if (line.matches(FUNCTION_CALL)) {
            return LineType.FUNCTION_CALL;
        } else if (line.matches(CONDITION)) {
            return LineType.CONDITION_START;
        } else if (line.matches(END_BLOCK)) {
            return LineType.END_BLOCK;
        } else if (line.matches(RETURN_STATEMENT)) {
            return LineType.RETURN_STATEMENT;
        } else {
            throw new IllegalStateException("Unsupported type of line: " + line);
        }
    }


}
