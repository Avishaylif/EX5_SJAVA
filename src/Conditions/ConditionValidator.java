package Conditions;

import VariablesManegment.Variable;
import VariablesManegment.SymbolsTable;
import VariablesManegment.Variable.Type; // נניח שיש Enum של טייפים
import errors.ValidationException;

/**
 * A validator that checks the validity of a condition in an if/while statement according to the following rules:
 *  - A single boolean expression can be:
 *    1) "true" or "false"
 *    2) A boolean or numeric variable (int/double) that is initialized and not null
 *    3) A numeric constant (positive or negative)
 *  - Multiple expressions can be combined using "||" or "&&", without parentheses
 *  - Nested parentheses or mismatched parentheses are not allowed (e.g., )( ).
 *
 * <p>This class provides functionality to validate the syntax and semantics of conditions
 * in boolean expressions typically found in control structures like `if` and `while` statements.
 */
public class ConditionValidator {

    private final SymbolsTable symbolsTable;

    /**
     * Constructs a ConditionValidator with a given SymbolsTable.
     *
     * @param symbolsTable The symbols table containing the variables and their metadata.
     */
    public ConditionValidator(SymbolsTable symbolsTable) {
        this.symbolsTable = symbolsTable;
    }

    /**
     * Validates whether the given condition string is syntactically and semantically correct.
     *
     * <p>The condition is checked for the following:
     *  - It should not be empty or null.
     *  - It should be split correctly by logical operators (&&, ||).
     *  - Each sub-condition should be a valid boolean expression, numeric constant, or a valid initialized variable.
     *
     * @param condition The condition string to be validated.
     * @throws ValidationException if the condition is invalid.
     */
    public void validateCondition(String condition) throws ValidationException {
        if (condition == null || condition.trim().isEmpty()) {
            throw new ValidationException("Empty or null condition is invalid.");
        }

        // Split the condition into sub-conditions by logical operators
        String[] subConditions = condition.split("(&&|\\|\\|)");

        // Basic check: if the array is empty, there were invalid operators (e.g., leading or trailing operators).
        if (subConditions.length == 0) {
            throw new ValidationException("Invalid condition syntax (possibly leading/trailing operators).");
        }

        // Validate each sub-condition
        for (String subCond : subConditions) {
            String trimmed = subCond.trim();
            if (trimmed.isEmpty()) {
                // Empty sub-condition means there were consecutive operators or an operator at the start or end
                throw new ValidationException("Invalid syntax: empty sub-condition (consecutive operators?)");
            }
            validateSingleCondition(trimmed);
        }

    }

    /**
     * Validates a single atomic condition according to the following rules:
     *  1) It must be a boolean literal ("true" or "false").
     *  2) It must be a numeric constant (positive or negative, including double values).
     *  3) It must be an initialized variable of type boolean, int, or double.
     *
     * @param cond The single condition (sub-expression) to validate.
     * @throws ValidationException if the condition is invalid.
     */
    private void validateSingleCondition(String cond) throws ValidationException {
        // 1. Check for boolean literals (true/false)
        if (cond.equals("true") || cond.equals("false")) {
            return; // valid
        }

        // 2. Check if it's a numeric literal (positive or negative, also supports Double).
        if (isNumericLiteral(cond)) {
            return; // valid
        }

        // 3. If it's not a literal, check if it's an initialized variable with the appropriate type (boolean/int/double).
        Variable var = symbolsTable.getVariable(cond);
        if (var == null) {
            throw new ValidationException("Unknown variable: " + cond);
        }
        if (!var.isInitialized()) {
            throw new ValidationException("Variable '" + cond + "' is not initialized.");
        }
        // Allow boolean or numeric types:
        if (var.getType() != Type.BOOLEAN && !isNumericType(var.getType())) {
            throw new ValidationException("Variable '" + cond + "' is not boolean or numeric.");
        }
        // If we reached here, the condition is valid
    }

    /**
     * Checks if the given string represents a numeric literal (positive or negative).
     * This method parses the string using {@link Double#parseDouble(String)}.
     *
     * @param str The string to check.
     * @return true if the string is a valid numeric literal, false otherwise.
     */
    private boolean isNumericLiteral(String str) {
        str = str.trim();
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the given variable type is numeric (either {@link Type#INT} or {@link Type#DOUBLE}).
     *
     * @param type The variable type to check.
     * @return true if the type is numeric, false otherwise.
     */
    private boolean isNumericType(Type type) {
        return (type == Type.INT || type == Type.DOUBLE);
    }
}
