package Methods;

import java.util.ArrayList;
import java.util.List;

/**
 * A parser class that analyzes a list of method lines and classifies them into different types.
 * The class uses regular expressions to identify various elements of a method such as variable declarations, assignments,
 * function calls, conditional statements, and return statements.
 *
 * <p>The parser can identify and classify the following types of lines:
 * <ul>
 *   <li>VARIABLE_DECLARATION - A line that declares a variable.</li>
 *   <li>VARIABLE_ASSIGNMENT - A line that assigns a value to a variable.</li>
 *   <li>FUNCTION_CALL - A line that represents a function call.</li>
 *   <li>CONDITION_START - A line that starts a conditional block (if/while).</li>
 *   <li>END_BLOCK - A line that ends a block of code (typically denoted by '}').</li>
 *   <li>RETURN_STATEMENT - A line that contains a return statement.</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * MethodParser parser = new MethodParser();
 * List<LineType> lineTypes = parser.parseMethod(methodLines);
 * </pre>
 */
public class MethodParser {

    // Regular expressions for line types
    private static final String VARIABLE_DECLARATION = "^(final\\s+)?(int|double|String|boolean|char)\\s+[a-zA-Z_][a-zA-Z0-9_]*(\\s*=\\s*.+)?;$";
    private static final String VARIABLE_ASSIGNMENT = "^[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*.+;$"; //TODO: check if covers all the options
    private static final String FUNCTION_CALL = "^[a-zA-Z][a-zA-Z0-9_]*\\s*\\(.*\\)\\s*;$";
    private static final String CONDITION = "^(if|while)\\s*\\(.*\\)\\s*\\{?$";
    private static final String END_BLOCK = "^\\}\\s*$";
    private static final String RETURN_STATEMENT = "^return;$";

    /**
     * Enum representing different types of lines that can be encountered in a method.
     */
    public enum LineType {
        VARIABLE_DECLARATION,
        VARIABLE_ASSIGNMENT,
        FUNCTION_CALL,
        CONDITION_START,
        END_BLOCK,
        RETURN_STATEMENT
    }

    /**
     * Parses a list of method lines and classifies them into different line types.
     *
     * @param methodLines A list of strings, each representing a line of code in a method.
     * @return A list of {@link LineType} corresponding to the type of each line in the input.
     * @throws IllegalStateException if the second-to-last line is not a valid return statement.
     */
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

    /**
     * Classifies a single line of code into one of the predefined {@link LineType}s based on regular expression matching.
     *
     * @param line A single line of code to classify.
     * @return The corresponding {@link LineType} for the given line.
     * @throws IllegalStateException if the line does not match any of the recognized patterns.
     */    private LineType classifyLine(String line) {
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
