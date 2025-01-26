package VariablesManegment;

import java.util.*;
import java.util.regex.*;

import VariablesManegment.Variable.Type;


public class VariableValidator {
    private final SymbolsTable symbolsTable;

    private static final Pattern VARIABLE_NAME_PATTERN =
            Pattern.compile("^[a-zA-Z]|^_[a-zA-Z][a-zA-Z0-9_]*|^[a-zA-Z][a-zA-Z0-9_]*$");

    public VariableValidator(List<String> globalVariables,SymbolsTable symbolsTable) throws Exception {

        this.symbolsTable = symbolsTable;
        for (String line: globalVariables){
            handleDeclarationOrAssignment(line);
        }
    }

    public List<Variable> handleDeclarationOrAssignment(String line) throws Exception {


        // 1) בדוק האם השורה מתחילה במילה 'final'
        boolean isFinal = false;
        if (line.startsWith("final ")) {
            isFinal = true;
            line = line.substring("final ".length()).trim();
        }

        // 2) בדיקה האם יש ציון של טיפוס. אם כן, ניקח אותו ונחתוך מהשורה.
        //    אם אין טיפוס, משמע זו השמה למשתנה קיים.
        String typeStr = null;
        String possibleType = extractTypeIfExists(line);
        if (possibleType != null) {
            typeStr = possibleType;
            // מורידים את הטיפוס (למשל "int ") מהשורה
            line = line.substring(possibleType.length()).trim();
        }

        // 3) כעת השורה שנותרה - או "a = 1, b, c=a" או סתם "x = 5"
        //    נפרק אותה לפי פסיקים, כדי לטפל באפשרות של הגדרה מרובה באותה שורה.
        //    קודם נסיר ; אם עדיין קיים בסוף.
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1).trim();
        }

        // פיצול לפי פסיקים
        String[] declarations = line.split(",");

        List<Variable> resultVariables = new ArrayList<>();

        for (String decl : declarations) {
            decl = decl.trim();
            // לדוגמה: decl יכול להיות "a = 1" או "b" או "c=a"

            // נבדוק האם יש השמה '=' בתוך הטקסט
            String varName;
            String assignmentValue = null;
            int eqIndex = decl.indexOf('=');
            if (eqIndex >= 0) {
                varName = decl.substring(0, eqIndex).trim();
                assignmentValue = decl.substring(eqIndex + 1).trim();
            } else {
                varName = decl.trim();//TODO: illiegal option
            }

            // אם אין טיפוס, מדובר בהשמה למשתנה קיים
            // אם יש טיפוס, מדובר בהגדרת משתנה חדש (או חדשים).
            if (typeStr != null) {
                // הגדרה חדשה
                resultVariables.add(
                        handleNewVariableDeclaration(
                                typeStr,
                                varName,
                                assignmentValue,
                                isFinal
                        )
                );
            } else {
                // השמה למשתנה קיים
                resultVariables.add(
                        handleExistingVariableAssignment(
                                varName,
                                assignmentValue

                        )
                );
            }
        }

        return resultVariables;
    }

    private String extractTypeIfExists(String line) {
        String[] words = line.trim().split("\\s+");
        if (words.length > 0) {
            String firstWord = words[0];

            // אם המילה הראשונה היא אחת מהטיפוסים, מחזירים אותה
            switch (firstWord) {
                case "int":
                case "double":
                case "boolean":
                case "char":
                case "String":
                    return firstWord;
                default:
                    return null;  // אם המילה הראשונה לא טיפוס, מחזירים null
            }
        }

        return null;
    }



    private Variable handleNewVariableDeclaration(String typeStr,
                                                  String varName,
                                                  String assignmentValue,
                                                  boolean isFinal)
            throws Exception {

        // 1) בדוק שם תקין
        validateVariableName(varName);

        // 2) בדוק שאין כבר משתנה באותו Scope עם אותו שם
        if (symbolsTable.isVariableInCurrentScope(varName)) {
            throw new ValidationException("Variable '" + varName + "' already declared in this scope.");
        }

        // 3) נזהה Enum של טיפוס
        Type type = parseType(typeStr); // מתודה שממירה מחרוזת ל-type (int, double ...)

        // 4) final מחייב השמה ראשונית
        boolean isInitialized = false;
        Object value = null;
        if (isFinal && assignmentValue == null) {
            throw new ValidationException("Final variable '" + varName + "' must have an initial value.");
        }

        if (assignmentValue != null) {
            // בדיקת תקינות הערך והמרה לאובייקט value המתאים
            value = parseAndValidateValue(assignmentValue, type);
            isInitialized = true;
        }

        // 5) יצירת המשתנה
        Variable newVar = new Variable(
                varName,
                type,
                isFinal,
                isInitialized,
                value
        );

        // 6) הכנסה לטבלת הסמלים
        symbolsTable.addVariable(newVar);

        return newVar;
    }

    /**
     * מטפל בהשמה למשתנה קיים (ללא ציון טיפוס).
     */
    private Variable handleExistingVariableAssignment(String varName,
                                                      String assignmentValue)
            throws Exception {
        // 1) שם המשתנה חייב להיות תקין
        validateVariableName(varName);// לא רואה סיבה לבדוק פעמיים

        // 2) משתנה צריך להיות קיים (ונגיש בסקופ הנוכחי או חיצוני)
        Variable variable = symbolsTable.getVariable(varName);
        if (variable == null) {
            throw new ValidationException("Variable '" + varName + "' is not declared.");
        }
        // בדיקה אם הוא final שכבר אותחל - לא ניתן לשנות ערך
        if (variable.isFinal()) {
            throw new ValidationException("Cannot assign a value to final variable '" + varName + "'.");
        }

        // 3) ניתוח הערך. אם אין ערך אחרי '=', זו שגיאה (השמה ללא ערך)
        if (assignmentValue == null) {
            throw new ValidationException("No value provided in assignment to '" + varName + "'.");
        }

        // 4) בדיקת הטיפוס והמרה לערך המתאים
        Object obj = parseAndValidateValue(assignmentValue,variable.getType());
        if (obj==null){
            throw new ValidationException("Value provided is illegal.");
        }
        // 5) עידכון
        variable.setValue(obj);
        variable.setInitialized(true);
        return variable;
    }

    /**
     * בדיקת שם משתנה באמצעות Regex פשוט, או לוגיקה אחרת לפי החוקים שלך.
     */
    private void validateVariableName(String varName) throws ValidationException {
        if (!VARIABLE_NAME_PATTERN.matcher(varName).matches()) {
            throw new ValidationException("Invalid variable name: " + varName);
        }
    }



    /**
     * מקבל ערך (מחרוזת) ובודק אם הוא תואם את הטיפוס המבוקש.
     * מחזיר אובייקט מתאים (Integer, Double, Boolean, Character, String) או זורק שגיאה אם לא תקין.
     */
    public Object parseAndValidateValue(String valueStr, Type targetType) throws ValidationException {
        // ייתכן שזה Literal או שם של משתנה אחר
        // צריך לבדוק אם valueStr הוא שם משתנה (קיים), או ערך מוחשי (כמו "5", "true", "\"abc\"", וכו').
        // 1) קודם בודקים אם זה שם משתנה קיים
        Variable otherVar = symbolsTable.getVariable(valueStr);
        if (otherVar != null) {
            // השמה ממשתנה קיים
            if (!isAssignmentCompatible(otherVar.getType(), targetType)) {
                throw new ValidationException("Type mismatch: cannot assign "
                        + otherVar.getType() + " to " + targetType);
            }
            if (!otherVar.isInitialized()) {
                throw new ValidationException("Cannot assign value from uninitialized variable '" + valueStr + "'.");
            }
            return otherVar.getValue(); // הערך של המשתנה האחר
        }

        // 2) אחרת, מניחים שזה Literal. נעשה בדיקה לפי ה- targetType:
        switch (targetType) {
            case INT:
                // int יכול לקבל "5", או "5.0" אם החלטת לאפשר int<-double?
                // כאן נניח שרק מספר שלם:
                if (!valueStr.matches("^-?\\d+$")) {
                    throw new ValidationException("Invalid int value: " + valueStr);
                }
                return Integer.parseInt(valueStr);

            case DOUBLE:
                // double יכול לקבל ערכים עם נקודה עשרונית, או int
                // Regex בסיסי מאוד:
                if (!valueStr.matches("^-?\\d*\\.?\\d+$")) {
                    throw new ValidationException("Invalid double value: " + valueStr);
                }
                return Double.parseDouble(valueStr);

            case BOOLEAN:
                if (!valueStr.equals("true") && !valueStr.equals("false")) {
                    throw new ValidationException("Invalid boolean value: " + valueStr);
                }
                return Boolean.parseBoolean(valueStr);

            case CHAR:
                // ציפייה לתו בודד במירכאות בודדות, למשל 'a'
                // או אפשרי לבדוק length == 3 ושהתו הראשון והאחרון הם גרש.
                if (!valueStr.matches("^'.'$")) {
                    throw new ValidationException("Invalid char value: " + valueStr);
                }
                return valueStr.charAt(1);

            case STRING:
                // נניח שמחרוזת חוקית היא "משהו בסוגריים כפולים".
                // כלומר: "abc" (כולל גרשיים)
                // אפשר לבנות Regex: ^".*"$
                if (!valueStr.matches("^\".*\"$")) {
                    throw new ValidationException("Invalid string literal: " + valueStr);
                }
                // הסרה של גרשיים
                return valueStr.substring(1, valueStr.length() - 1);

            default:
                throw new ValidationException("Unsupported type: " + targetType);
        }
    }

    /**
     * בדיקה האם ניתן להעתיק type מקור ל-type יעד, בהתאם לחוקי S-Java שלך.
     * למשל int -> double מותר, double -> int מותר (אם כך החלטת), וכו'.
     */
    private boolean isAssignmentCompatible(Type source, Type target) {
        if (source == target) {
            return true;
        }
        // int->double?
        if (source == Type.INT && target == Type.DOUBLE) {
            return true;
        }
        // double->int?
        if (source == Type.DOUBLE && target == Type.INT) {
            return true;
        }
        // ניתן להרחיב בהתאם לצרכים (String->String, boolean->boolean, char->char וכו')
        return false;
    }
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

    public SymbolsTable getSymbolsTable() {
        return symbolsTable;
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

}


