package Methods;

import Conditions.ConditionValidator;
import VariablesManegment.SymbolsTable;
import VariablesManegment.Variable;
import VariablesManegment.VariableValidator;


import java.util.List;
import java.util.Map;

public class MethodValidator {


    // מפה של שם-מתודה -> אובייקט MethodData (או מבנה דומה),
    // כדי שנוכל לאתר פרמטרים/גוף לפי שם המתודה.
    private final Map<String, MethodData> methods;

    private final MethodParser methodParser;
    private final SymbolsTable symbolsTable;
    private final FunctionCallValidator functionCallValidator;
    private final ConditionValidator conditionValidator;
    private final VariableValidator variableValidator;

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
     * דוגמה לפונקציה שרצה על כל המתודות במפה ומוודאת אותן.
     */
    public void validateAllMethods() throws Exception {
        for (MethodData methodData : methods.values()) {
            validateMethod(methodData);
        }
    }

    /**
     * מוודאת מתודה בודדת:
     * 1) פתיחת סקופ והוספת הפרמטרים כמשתנים מקומיים.
     * 2) מעבר שורה-שורה וביצוע הבדיקות הרלוונטיות.
     */
    public void validateMethod(MethodData methodData) throws Exception {
        // 1. פותחים סקופ (עומק 1) עבור הפרמטרים
        symbolsTable.openScope();
        // מכניסים את הפרמטרים כמשתנים מקומיים:
        for (Variable param : methodData.getMethodParameters()) {
            symbolsTable.addVariable(param);
            // בהנחה שה-param כבר בנוי נכון (שם, טיפוס, isFinal, isInitialized וכו').
        }

        // 2. נקבל את גוף המתודה (רשימת שורות) ואת ה-LineTypes שלה (לדוגמה).
        List<String> body = methodData.getBody();
        List<MethodParser.LineType> lineTypes = methodParser.parseMethod(body);

        int blockDepth = 1; // מונה לקביעת עומק בלוקים פנימיים
        for (int i = 0; i < body.size(); i++) {
            String line = body.get(i).trim();
            MethodParser.LineType lineType = lineTypes.get(i);

            switch (lineType) {
                case FUNCTION_CALL:
                    // מפעילים את הוולידציה של קריאת מתודה
                    functionCallValidator.validateFunctionCall(extractFunctionName(line), extractArguments(line));
                    break;

                case CONDITION_START:
                    // לדוגמה, שורה בסגנון "if (x > 5) {"
                    // 1) חילוץ הביטוי התנאי
                    String conditionExpr = extractConditionExpression(line);
                    // 2) ולידציה שלו
                    conditionValidator.validateCondition(conditionExpr);
                    // 3) פתיחת בלוק חדש => openScope
                    symbolsTable.openScope();
                    blockDepth++;
                    break;

                case END_BLOCK:
                    // סוגרים את הבלוק (בדרך כלל "}")
                    if (blockDepth == 0) {
                        throw new Exception("Unmatched closing brace for condition.");
                    }
                    symbolsTable.closeScope();
                    blockDepth--;
                    break;

                case VARIABLE_DECLARATION:
                    // נניח שזיהינו שזה הכרזה / השמה באותה קטגוריה
                    // מפעילים את VariableValidator
                    // בתוך השיטה הזו נזהה אם זה הכרזה או השמה ונפעל בהתאם
                    // (כמו handleDeclarationOrAssignment(line, currentScopeDepth)).
                    variableValidator.handleDeclarationOrAssignment(line);
                    break;
                case VARIABLE_ASSIGNMENT:
                    // נניח שזיהינו שזה הכרזה / השמה באותה קטגוריה
                    // מפעילים את VariableValidator
                    // בתוך השיטה הזו נזהה אם זה הכרזה או השמה ונפעל בהתאם
                    // (כמו handleDeclarationOrAssignment(line, currentScopeDepth)).
                    variableValidator.handleDeclarationOrAssignment(line);
                    break;
                case RETURN_STATEMENT:
                    break;
                default:
                    // שורה לא מוכרת - אולי ריקה, או משהו שנפל בין הכיסאות
                    // אפשר לזרוק שגיאה או להתעלם
                    throw new Exception("Unknown or invalid line: " + line);
            }
        }

        // בסוף המתודה, בודקים אם נשארו בלוקים פתוחים
        if (blockDepth != 0) {
            throw new Exception("Unclosed block(s) in method: " + methodData.getMethodName());
        }

    }


    // -----------------------------------------------------------
    // מתודות עזר (Pseudo-code) לחילוץ מידע מהשורות
    // -----------------------------------------------------------


    private String extractFunctionName(String line) {
        // בהנחה שה-MethodParser כבר בדק שזה תקין,
        // נחפש '('
        int idx = line.indexOf('(');
        if (idx < 0) return line;
        return line.substring(0, idx).trim();
    }



    private String extractArguments(String line) {
        // "foo(1,2)" -> מחזיר "1,2"
        int start = line.indexOf("(");
        int end = line.lastIndexOf(")");
        if (start < 0 || end < 0 || end <= start) return "";
        return line.substring(start + 1, end).trim();
    }

    private String extractConditionExpression(String line) {
        // לדוגמה: "if (x > 5) {"
        // נחפש מה שבין הסוגריים העגולים
        int open = line.indexOf("(");
        int close = line.lastIndexOf(")");
        if (open < 0 || close < 0 || close <= open) {
            return ""; // או זרוק שגיאה
        }
        return line.substring(open + 1, close).trim();
    }
}


/** old version
package Methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MethodValidator {

    private final Map<String, List<Map<String, String>>> methods; // Methods and their parameters
    private final Stack<Map<String, String>> variableScopes; // Stack to manage variable scopes

    public MethodValidator(Map<String, List<Map<String, String>>> methods) {
        this.methods = methods;
        this.variableScopes = new Stack<>();
    }

    public void validateMethod(List<String> methodLines, List<MethodParser.LineType> lineTypes, List<Map<String, String>> parameters) {
        int blockDepth = 0;

        // Add the method's parameters to the first scope
        Map<String, String> methodScope = new HashMap<>();
        for (Map<String, String> param : parameters) {
            String paramName = param.get("name");
            String paramType = param.get("type");
            if (methodScope.containsKey(paramName)) {
                throw new IllegalStateException("Duplicate parameter name in method: " + paramName); //todo: check
            }
            methodScope.put(paramName, paramType);
        }
        variableScopes.push(methodScope);

        for (int i = 0; i < methodLines.size(); i++) {
            String line = methodLines.get(i).trim();
            MethodParser.LineType lineType = lineTypes.get(i);

            switch (lineType) {
                case FUNCTION_CALL:
                    validateFunctionCall(line);
                    break;
                case VARIABLE_ASSIGNMENT:
                    validateVariableAssignment(line);
                    break;
                case VARIABLE_DECLARATION:
                    validateVariableDeclaration(line);
                    break;
                case START_BLOCK:
                    blockDepth++;
                    variableScopes.push(new HashMap<>());
                    break;
                case END_BLOCK:
                    if (blockDepth == 0) {
                        throw new IllegalStateException("Unmatched closing block: " + line);
                    }
                    blockDepth--;
                    variableScopes.pop();
                    break;
                case RETURN_STATEMENT:
                    // No action needed for return
                    break;
                default:
                    throw new IllegalStateException("Unknown or invalid line: " + line);
            }
        }

        if (blockDepth != 0) {
            throw new IllegalStateException("Unclosed block detected in the method.");
        }
    }

    private void validateFunctionCall(String line) {
        String functionName = line.substring(0, line.indexOf('(')).trim();
        String arguments = line.substring(line.indexOf('(') + 1, line.lastIndexOf(')')).trim();

        if (!methods.containsKey(functionName)) {
            throw new IllegalStateException("Function not found: " + functionName);
        }

        List<Map<String, String>> expectedParams = methods.get(functionName);
        String[] providedArgs = arguments.isEmpty() ? new String[0] : arguments.split(",");

        if (providedArgs.length != expectedParams.size()) {
            throw new IllegalStateException("Parameter count mismatch for function: " + functionName);
        }

        for (int i = 0; i < providedArgs.length; i++) {
            String providedArg = providedArgs[i].trim();
            String expectedType = expectedParams.get(i).get("type");

            if (!isValidTypeMatch(providedArg, expectedType)) {
                throw new IllegalStateException("Type mismatch for parameter " + (i + 1) + " in function: " + functionName);
            }
        }
    }



    private boolean variableExists(String variableName) {
        for (Map<String, String> scope : variableScopes) {
            if (scope.containsKey(variableName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFinalVariable(String variableName) {
        for (Map<String, String> scope : variableScopes) {
            if (scope.containsKey(variableName) && "true".equals(scope.get(variableName))) {
                return true;
            }
        }
        return false;
    }

    private void validateVariableDeclaration(String line) {
        String[] declarations = line.split(",");
        for (String declaration : declarations) {
            declaration = declaration.trim();
            String[] parts = declaration.split("\\s*");
            String type = parts[0];
            String name = parts[1];

            if (currentScopeContainsVariable(name)) {
                throw new IllegalStateException("Variable already declared in current scope: " + name);
            }

            variableScopes.peek().put(name, type);
        }
    }

    private boolean currentScopeContainsVariable(String name) {
        return variableScopes.peek().containsKey(name);
    }
}*/
