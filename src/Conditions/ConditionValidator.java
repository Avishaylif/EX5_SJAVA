package Conditions;

import VariablesManegment.Variable;
import VariablesManegment.SymbolsTable;
import VariablesManegment.Variable.Type; // נניח שיש Enum של טייפים

/**
 * Validator שבודק שהתנאי של if/while תקין לפי הכללים הבאים:
 *  - ביטוי בוליאני בודד יכול להיות:
 *    1) "true" או "false"
 *    2) משתנה שהוא boolean או משתנה מספרי (int/double) שאינו null ושמאותחל
 *    3) קבוע מספרי (חיובי או שלילי)
 *  - ייתכן מספר ביטויים, מופרדים ב-|| או &&, ללא סוגריים
 *  - אין להתייחס למקרה של )( או סוגריים מקוננים.
 */
public class ConditionValidator {

    private final SymbolsTable symbolsTable;

    public ConditionValidator(SymbolsTable symbolsTable) {
        this.symbolsTable = symbolsTable;
    }

    /**
     * בודק האם מחרוזת התנאי תקינה.
     * זורק ValidationException אם לא תקין.
     */
    public void validateCondition(String condition) throws ValidationException {
        if (condition == null || condition.trim().isEmpty()) {
            throw new ValidationException("Empty or null condition is invalid.");
        }

        // מפצלים את המחרוזת לתתי-ביטויים לפי && או ||.
        // אין כאן טיפול בסוגריים או בתחביר מורכב, רק פיצול פשוט.
        String[] subConditions = condition.split("(&&|\\|\\|)");

        // בדיקה בסיסית: אם מתקבל מערך ריק, כנראה שהיו אופרטורים לא תקינים (למשל מתחיל ב-&&).
        if (subConditions.length == 0) {
            throw new ValidationException("Invalid condition syntax (possibly leading/trailing operators).");
        }

        // עבור כל תת-תנאי בודקים חוקיות
        for (String subCond : subConditions) {
            String trimmed = subCond.trim();
            if (trimmed.isEmpty()) {
                // ריק => מעיד על שני אופרטורים רצופים, או על אופרטור בראש/זנב
                throw new ValidationException("Invalid syntax: empty sub-condition (consecutive operators?)");
            }
            validateSingleCondition(trimmed);
        }

        // אם עברנו על כולם בהצלחה, התנאי תקין.
    }

    /**
     * בודק תת-ביטוי בודד ("אטומי") לפי הכללים:
     *  1) true/false
     *  2) משתנה בוליאני או מספרי, מאותחל
     *  3) קבוע מספרי (חיובי או שלילי)
     */
    private void validateSingleCondition(String cond) throws ValidationException {
        // 1. המילה השמורה true/false
        if (cond.equals("true") || cond.equals("false")) {
            return; // תקין
        }

        // 2. בדיקת קבוע מספרי (חיובי או שלילי, פה נתיר גם Double).
        if (isNumericLiteral(cond)) {
            return; // תקין
        }

        // 3. אם זה לא true/false ולא קבוע מספרי,
        //    נבדוק האם זה משתנה מוכר, מאותחל, ובעל טיפוס מתאים (boolean/int/double).
        Variable var = symbolsTable.getVariable(cond);
        if (var == null) {
            throw new ValidationException("Unknown variable: " + cond);
        }
        if (!var.isInitialized()) {
            throw new ValidationException("Variable '" + cond + "' is not initialized.");
        }
        // מותר טיפוס boolean או טיפוס מספרי:
        if (var.getType() != Type.BOOLEAN && !isNumericType(var.getType())) {
            throw new ValidationException("Variable '" + cond + "' is not boolean or numeric.");
        }
        // אם הגענו לכאן => תקין
    }

    /**
     * בודק האם מדובר בערך מספרי (כולל חיובי או שלילי) לפי parseDouble.
     * אפשר להחליף ב parseInt אם רוצים רק שלם.
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
     * בודק האם הטיפוס הוא מספרי (int או double).
     * אפשר להוסיף CHAR אם השפה מתירה.
     */
    private boolean isNumericType(Type type) {
        return (type == Type.INT || type == Type.DOUBLE);
    }

    /**
     * שגיאת ולידציה כללית לקוד.
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
