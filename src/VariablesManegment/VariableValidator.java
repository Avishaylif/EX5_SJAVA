package VariablesManegment;

import java.util.*;
import java.util.regex.*;

import VariablesManegment.Variable.Type;
import errors.ValidationException;

/**
 * Validates and manages variable declarations and assignments within different scopes.
 * Utilizes a {@link SymbolsTable} to handle variable scoping, ensuring variables are declared,
 * initialized, and assigned correctly according to their types and scope rules.
 *
 * <p>The {@code VariableValidator} class provides functionality to:
 * <ul>
 *   <li>Handle variable declarations and assignments.</li>
 *   <li>Validate variable names against naming conventions.</li>
 *   <li>Ensure type compatibility during assignments.</li>
 *   <li>Manage scope entry and exit operations.</li>
 * </ul>
 * </p>
 *
 */
public class VariableValidator {
    /** Symbol table managing variable scopes and declarations. */
    private final SymbolsTable symbolsTable;

    /** Pattern to validate variable names following specific naming conventions. */
    private static final Pattern VARIABLE_NAME_PATTERN =
            Pattern.compile("^[a-zA-Z]|^_[a-zA-Z][a-zA-Z0-9_]*|^[a-zA-Z][a-zA-Z0-9_]*$");

    /**
     * Constructs a new {@code VariableValidator} and processes a list of global variable declarations.
     *
     * @param globalVariables a list of strings representing global variable declarations
     * @param symbolsTable    the symbol table to manage variable scopes and declarations
     * @throws Exception if any variable declaration is invalid or causes a conflict
     */
    public VariableValidator(List<String> globalVariables,SymbolsTable symbolsTable) throws Exception {

        this.symbolsTable = symbolsTable;
        for (String line: globalVariables){
            handleDeclarationOrAssignment(line);
        }
    }

    /**
     * Handles the declaration or assignment of variables based on the provided line of code.
     *
     * <p>This method parses the line to determine if it is a declaration or an assignment.
     * It supports multiple declarations or assignments separated by commas within the same line.</p>
     *
     * @param line the line of code containing variable declarations or assignments
     * @return a list of {@link Variable} instances that were declared or modified
     * @throws Exception if any declaration or assignment is invalid or causes a conflict
     */
    public List<Variable> handleDeclarationOrAssignment(String line) throws Exception {


        // 1) Check if the line starts with 'final'
        boolean isFinal = false;
        if (line.startsWith("final ")) {
            isFinal = true;
            line = line.substring("final ".length()).trim();
        }

        // 2) Extract the type if present
        String typeStr = null;
        String possibleType = extractTypeIfExists(line);
        if (possibleType != null) {
            typeStr = possibleType;
            // מורידים את הטיפוס (למשל "int ") מהשורה
            line = line.substring(possibleType.length()).trim();
        }

        // 3) Remove trailing semicolon if present
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1).trim();
        }

        // 4) Split the line by commas to handle multiple declarations/assignments
        List<Variable> resultVariables = getResultVariables(line, typeStr, isFinal);

        return resultVariables;
    }

    /**
     * Splits the line into individual declarations or assignments and processes each one.
     *
     * @param line     the line of code containing multiple declarations or assignments
     * @param typeStr  the type of the variables if it's a declaration; {@code null} otherwise
     * @param isFinal  {@code true} if the variables are final; {@code false} otherwise
     * @return a list of {@link Variable} instances that were declared or modified
     * @throws Exception if any declaration or assignment is invalid or causes a conflict
     */
    private List<Variable> getResultVariables(String line, String typeStr, boolean isFinal) throws Exception {
        String[] declarations = line.split(",");

        List<Variable> resultVariables = new ArrayList<>();

        for (String decl : declarations) {
            assignOrDeclareVariables(typeStr, isFinal, decl, resultVariables);
        }
        return resultVariables;
    }

    /**
     * Determines whether to declare a new variable or assign a value to an existing one based on the presence of a type.
     *
     * @param typeStr          the type of the variable if it's a declaration; {@code null} otherwise
     * @param isFinal          {@code true} if the variable is final; {@code false} otherwise
     * @param decl             the declaration or assignment string (e.g., "a = 1" or "b")
     * @param resultVariables  the list to which the processed {@link Variable} instances will be added
     * @throws Exception if any declaration or assignment is invalid or causes a conflict
     */
    private void assignOrDeclareVariables(String typeStr, boolean isFinal, String decl, List<Variable> resultVariables) throws Exception {
        decl = decl.trim();
        // Example: decl can be "a = 1" or "b" or "c = a"

        // Check if there is an assignment '=' in the declaration
        String varName;
        String assignmentValue = null;
        int eqIndex = decl.indexOf('=');
        if (eqIndex >= 0) {
            varName = decl.substring(0, eqIndex).trim();
            assignmentValue = decl.substring(eqIndex + 1).trim();
        } else {
            varName = decl.trim();//TODO: illiegal option
        }

        if (typeStr != null) {
            // Declaration of a new variable
            resultVariables.add(
                    handleNewVariableDeclaration(
                            typeStr,
                            varName,
                            assignmentValue,
                            isFinal
                    )
            );
        } else {
            // Assignment to an existing variable
            resultVariables.add(
                    handleExistingVariableAssignment(
                            varName,
                            assignmentValue

                    )
            );
        }
    }

    /**
     * Extracts the variable type from the beginning of the line if it exists.
     *
     * @param line the line of code to extract the type from
     * @return the type as a string if present; {@code null} otherwise
     */
    private String extractTypeIfExists(String line) {
        String[] words = line.trim().split("\\s+");
        if (words.length > 0) {
            String firstWord = words[0];

            // Return the type if the first word matches known types
            switch (firstWord) {
                case "int":
                case "double":
                case "boolean":
                case "char":
                case "String":
                    return firstWord;
                default:
                    return null;  // First word is not a type
            }
        }

        return null;
    }


    /**
     * Handles the declaration of a new variable.
     *
     * @param typeStr         the type of the variable as a string
     * @param varName         the name of the variable
     * @param assignmentValue the value to assign to the variable (can be {@code null} if not initialized)
     * @param isFinal         {@code true} if the variable is final; {@code false} otherwise
     * @return the newly declared {@link Variable} instance
     * @throws Exception if the variable name is invalid, already declared in the current scope,
     *                   or if the assignment value is invalid
     */
    private Variable handleNewVariableDeclaration(String typeStr,
                                                  String varName,
                                                  String assignmentValue,
                                                  boolean isFinal)
            throws Exception {

        // 1) Validate the variable name
        validateVariableName(varName);

        // 2) Ensure the variable is not already declared in the current scope
        if (symbolsTable.isVariableInCurrentScope(varName)) {
            throw new ValidationException("Variable '" + varName + "' already declared in this scope.");
        }

        // 3) Parse the type string to the enum Type
        Type type = parseType(typeStr); // מתודה שממירה מחרוזת ל-type (int, double ...)

        // 4) If the variable is final, ensure it has an initial value
        boolean isInitialized = false;
        Object value = null;
        if (isFinal && assignmentValue == null) {
            throw new ValidationException("Final variable '" + varName + "' must have an initial value.");
        }

        if (assignmentValue != null) {
            // Validate and parse the assignment value
            value = parseAndValidateValue(assignmentValue, type);
            isInitialized = true;
        }

        // 5) Create the Variable instance
        Variable newVar = new Variable(
                varName,
                type,
                isFinal,
                isInitialized,
                value
        );

        // 6) Add the variable to the symbol table
        symbolsTable.addVariable(newVar);

        return newVar;
    }

    /**
     * Handles the assignment of a value to an existing variable.
     *
     * @param varName         the name of the variable to assign a value to
     * @param assignmentValue the value to assign to the variable
     * @return the {@link Variable} instance after assignment
     * @throws Exception if the variable name is invalid, not declared, final and already initialized,
     *                   or if the assignment value is invalid
     */
    private Variable handleExistingVariableAssignment(String varName,
                                                      String assignmentValue)
            throws Exception {
        // 1) Validate the variable name
        validateVariableName(varName);// לא רואה סיבה לבדוק פעמיים

        // 2) Retrieve the variable from the symbol table
        Variable variable = symbolsTable.getVariable(varName);
        if (variable == null) {
            throw new ValidationException("Variable '" + varName + "' is not declared.");
        }
        // 3) Check if the variable is final and already initialized
        if (variable.isFinal()) {
            throw new ValidationException("Cannot assign a value to final variable '" + varName + "'.");
        }

        // 4) Ensure there is an assignment value
        if (assignmentValue == null) {
            throw new ValidationException("No value provided in assignment to '" + varName + "'.");
        }

        // 5) Validate and parse the assignment value
        Object obj = parseAndValidateValue(assignmentValue,variable.getType());
        if (obj==null){
            throw new ValidationException("Value provided is illegal.");
        }
        // 6) Update the variable's value and mark it as initialized
        variable.setValue(obj);
        variable.setInitialized(true);
        return variable;
    }

    /**
     * Validates the variable name against the predefined naming pattern.
     *
     * @param varName the name of the variable to validate
     * @throws ValidationException if the variable name does not match the naming pattern
     */
    private void validateVariableName(String varName) throws ValidationException {
        if (!VARIABLE_NAME_PATTERN.matcher(varName).matches()) {
            throw new ValidationException("Invalid variable name: " + varName);
        }
    }



    /**
     * Parses and validates the value assigned to a variable based on its target type.
     * Handles both literal values and assignments from other variables.
     *
     * @param valueStr   the value as a string to be parsed and validated
     * @param targetType the target {@link Type} of the variable
     * @return the parsed value as an {@link Object} if valid
     * @throws ValidationException if the value is invalid or incompatible with the target type
     */
    public Object parseAndValidateValue(String valueStr, Type targetType) throws ValidationException {
        // Check if the value is an existing variable
        Variable otherVar = symbolsTable.getVariable(valueStr);
        if (otherVar != null) {
            // Assignment from another variable
            if (!isAssignmentCompatible(otherVar.getType(), targetType)) {
                throw new ValidationException("Type mismatch: cannot assign "
                        + otherVar.getType() + " to " + targetType);
            }
            if (!otherVar.isInitialized()) {
                throw new ValidationException("Cannot assign value from uninitialized variable '" + valueStr + "'.");
            }
            return otherVar.getValue(); // Return the value of the other variable
        }

        // Assume the value is a literal and parse accordingly
        return validateInputParameterType(valueStr, targetType);
    }

    /**
     * get the value of the new paremeter, and check if it fits the parameter type.
     * @param valueStr
     * @param targetType
     * @return Object
     * @throws ValidationException
     */
    private static Object validateInputParameterType(String valueStr, Type targetType) throws ValidationException {
        switch (targetType) {
            case INT:
                // Expecting an integer literal
                if (!valueStr.matches("^(-|\\+)?\\d+$")) {
                    throw new ValidationException("Invalid int value: " + valueStr);
                }
                return Integer.parseInt(valueStr);

            case DOUBLE:
                // Expecting a double literal (with or without decimal point)
                if (!valueStr.matches("^(-|\\+)?\\d*\\.?\\d+$")) {
                    throw new ValidationException("Invalid double value: " + valueStr);
                }
                return Double.parseDouble(valueStr);

            case BOOLEAN:
                // Expecting 'true' or 'false'
                //TODO: handle that!
                if (!valueStr.equals("true") && !valueStr.equals("false")) {
                    throw new ValidationException("Invalid boolean value: " + valueStr);
                }
                return Boolean.parseBoolean(valueStr);

            case CHAR:
                // Expecting a single character enclosed in single quotes, e.g., 'a'
                if (!valueStr.matches("^'.'$")) {
                    throw new ValidationException("Invalid char value: " + valueStr);
                }
                return valueStr.charAt(1);

            case STRING:
                // Expecting a string literal enclosed in double quotes, e.g., "hello"
                if (!valueStr.matches("^\".*\"$")) {
                    throw new ValidationException("Invalid string literal: " + valueStr);
                }
                // Remove the surrounding double quotes
                return valueStr.substring(1, valueStr.length() - 1);

            default:
                throw new ValidationException("Unsupported type: " + targetType);
        }
    }

    /**
     * Determines if a source type can be assigned to a target type based on type compatibility rules.
     *
     * @param source the source {@link Type} of the value being assigned
     * @param target the target {@link Type} of the variable receiving the value
     * @return {@code true} if the assignment is compatible; {@code false} otherwise
     */
    private boolean isAssignmentCompatible(Type source, Type target) {
        if (source == target) {
            return true;
        }
        // Allow assigning an int to a double
        if (source == Type.INT && target == Type.DOUBLE) {
            return true;
        }
        // Allow assigning a double to an int if explicitly allowed todo: check
        if (source == Type.DOUBLE && target == Type.INT) {
            return true;
        }
        // Extend with additional type compatibility rules as needed todo: check
        return false;
    }

    /**
     * Parses a type string into its corresponding {@link Type} enum value.
     *
     * @param typeStr the type as a string (e.g., "int", "double")
     * @return the corresponding {@link Type} enum value
     * @throws ValidationException if the type string does not match any known type
     */
    public Type parseType(String typeStr) throws ValidationException {
        switch (typeStr) {
            case "int":
                return Type.INT;
            case "double":
                return Type.DOUBLE;
            case "boolean":
                return Type.BOOLEAN;
            case "char":
                return Type.CHAR;
            case "String":
                return Type.STRING;
            default:
                throw new ValidationException("Unknown type: " + typeStr);
        }
    }




}


