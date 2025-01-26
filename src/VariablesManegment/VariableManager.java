import VariablesManegment.SymbolsTable;
import VariablesManegment.Variable;
import VariablesManegment.Variable.Type;

import java.util.List;
import java.util.regex.Pattern;

public class VariableManager {
//    /**
//    private static final String INT_VARIABLE =
//            "^(final\\s*)?(int)\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*(=\\s*[+-]?\\d+)?(\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*[+-]?\\d+)?)*\\s*;$";
//    private static final String DOUBLE_VARIABLE =
//            "^(final\\s*)?(double)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*-?\\d+(\\.\\d+)?|\\s*)((\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*-?\\d+(\\.\\d+)?)*\\s*)*)\\s*;$";
//    private static final String STRING_VARIABLE =
//            "^(final\\s*)?(String)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*\"[^\"]*\"|\\s*)((\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*\"[^\"]*\")*\\s*)*)\\s*;$";
//    private static final String BOOLEAN_VARIABLE =
//            "^(final\\s*)?(boolean)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*(true|false|[-+]?\\d+(\\.\\d+)?))?(\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*(true|false|[-+]?\\d+(\\.\\d+)?))?\\s*)*\\s*;$";
//    private static final String CHAR_VARIABLE =
//            "^(final\\s*)?(char)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(\\s*=\\s*'[^\n']{1}'|\\s*)((\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*(=\\s*'[^\n']{1}')*\\s*)*)\\s*;$";
//
//    private static final String FINAL_VARIABLE = "^\\s*final\\b.*";
//
//    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(
//            "^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*([^;]+);\\s*$"
//    );
//
//    private SymbolsTable symbolsTable;
//    private int currentDepth;
//
//    public VariableManager(SymbolsTable symbolTable) {
//            this.symbolsTable = symbolTable;
//            this.currentDepth = 0; // נניח 0 זה גלובלי
//    }
//
//    public void addGlobalVariables(List<String> lines) throws Exception {
//        // currentDepth = 0
//        for (String line : lines) {
//            processDeclarationOrAssignment(line);
//        }
//    }
//
//    // כניסה לבלוק
//    public void enterScope() {
//        scopeStack.push(new SymbolsTable());
//    }
//
//    // יציאה מבלוק
//    public void exitScope() {
//        scopeStack.pop();
//    }
//
//    /**
//     * מחפש משתנה בשם מסוים בסטאק הסקופים מלמעלה למטה.
//     * מחזיר null אם לא נמצא.
//     */
//    public Variable findVariable(String name) {
//        for (SymbolsTable symbolsTable : scopeStack) {
//            if (symbolsTable.hasVariable(name)) {
//                return symbolsTable.getVariable(name);
//            }
//        }
//        return null;
//    }
//
//    /**
//     * בדיקה האם ניתן להכריז משתנה בסקופ העליון.
//     * הכרזה על משתנה חדש (אפשר לכלול את האתחול).
//     */
//    public void declareVariables(String declarationLine) throws Exception {
//        // 1) נזהה אם השורה מתחילה ב-final
//        // 2) נמצא את הסוג (int|double|String|boolean|char)
//        // 3) נפצל את השאר לפי פסיקים
//        // 4) על כל חלק נבדוק אם יש =, ונאתחל בהתאם
//        // 5) נוודא שאין התנגשות שמות בסקופ העליון
//        // 6) נוודא שאם זה final יש ערך
//        // 7) נוודא שהערך תואם סוג
//        // 8) נוסיף למפה של הסקופ העליון
//
//        // זו רק דוגמה: כאן אפשר לממש Regex מלא או לעבד את הטקסט ידנית.
//
//        // Scope עליון
//        SymbolsTable currentSymbolsTable = scopeStack.peek();
//        if (currentSymbolsTable == null) {
//            throw new Exception("No scope available!");
//        }
//
//        // דוגמה קצרה (לא מלאה) להמחשה בלבד:
//        // נשער שהשורה היא:
//        // [final] int a = 5, b, c = 10;
//        // נפרק:
//
//        boolean isFinal = false;
//        String line = declarationLine.trim();
//        if (line.startsWith("final")) {
//            isFinal = true;
//            line = line.substring("final".length()).trim();
//        }
//
//        // נמצא את הסוג (עוצרים עד רווח ראשון או עד שנתקלים בשם משתנה)
//        String[] split = line.split("\\s+", 2);
//        String typeString = split[0]; // אמור להיות int/double...
//        Type varType = parseType(typeString); // פונקציה שנזהה את ה-enum
//        String varsPart = split[1].trim(); // כל החלק שלאחר הסוג
//
//        // מפצלים בפסיקים
//        String[] declarations = varsPart.split(",");
//        for (String decl : declarations) {
//            decl = decl.trim();
//            // בודקים אם יש =
//            String name;
//            String value = null;
//            if (decl.contains("=")) {
//                String[] nv = decl.split("=", 2);
//                name = nv[0].trim();
//                value = nv[1].trim();
//            } else {
//                name = decl;
//            }
//
//            // בדיקה חוקיות שם
//            if (!isValidVariableName(name)) {
//                throw new Exception("Invalid variable name: " + name);
//            }
//            // בדיקה שהשם לא קיים בסקופ העליון
//            if (currentSymbolsTable.hasVariable(name)) {
//                throw new Exception("Variable name already declared in this scope: " + name);
//            }
//            // אם final ואין השמה => שגיאה
//            if (isFinal && value == null) {
//                throw new Exception("Final variable must have an initial value: " + name);
//            }
//
//            // אם יש השמה, בודקים שהיא חוקית לסוג
//            boolean isInitialized = (value != null);
//            Object parsedValue = null;
//            if (isInitialized) {
//                parsedValue = parseValue(value, varType);
//            }
//
//            Variable var = new Variable(name, varType, isFinal, isInitialized, parsedValue, scopeDepth);
//            currentSymbolsTable.addVariable(var);
//        }
//    }
//
//    /**
//     * השמה למשתנה קיים.
//     * @param assignmentLine לדוגמה: b = 10; או b = a; וכו'.
//     */
//    public void assignVariable(String assignmentLine) throws Exception {
//        // נניח שורה כמו: x = 123; או x = y;
//        String line = assignmentLine.trim();
//        if (!line.contains("=")) {
//            throw new Exception("No '=' in assignment");
//        }
//        String[] nv = line.split("=", 2);
//        String name = nv[0].trim();
//        String value = nv[1].trim();
//        // להסיר ; אם יש
//        if (value.endsWith(";")) {
//            value = value.substring(0, value.length() - 1).trim();
//        }
//
//        // מוצאים את המשתנה ב-Scopes
//        Variable var = findVariable(name);
//        if (var == null) {
//            throw new Exception("Variable not declared: " + name);
//        }
//        // אם var הוא final וכבר מאותחל – אי אפשר להשנות
//        if (var.isFinal() && var.isInitialized()) {
//            throw new Exception("Cannot assign to a final variable: " + name);
//        }
//
//        // האם הערך הוא ליטרל מתאים או שם משתנה אחר?
//        Variable otherVar = findVariable(value); // בודקים אולי זה משתנה
//        if (otherVar != null) {
//            // בדיקת התאמה בין סוגים
//            if (!var.getType().canAssignFrom(otherVar.getType())) {
//                throw new Exception("Type mismatch: " + var.getType() + " cannot get " + otherVar.getType());
//            }
//            // השמה
//            var.setInitialized(true);
//            var.setValue(otherVar.getValue());
//        } else {
//            // כנראה ליטרל
//            Object parsed = parseValue(value, var.getType());
//            var.setInitialized(true);
//            var.setValue(parsed);
//        }
//    }
//
//    // פונקציות עזר:
//    private Type parseType(String typeString) throws Exception {
//        switch (typeString) {
//            case "int": return Type.INT;
//            case "double": return Type.DOUBLE;
//            case "String": return Type.STRING;
//            case "boolean": return Type.BOOLEAN;
//            case "char": return Type.CHAR;
//        }
//        throw new Exception("Unknown type: " + typeString);
//    }
//
//    private boolean isValidVariableName(String name) {
//        // Regex לדוגמה:
//        return name.matches("^[a-zA-Z]|_[a-zA-Z][a-zA-Z0-9_]*$");
//    }
//
//    private Object parseValue(String text, Type type) throws Exception {
//        switch (type) {
//            case INT:
//                // בודקים אם text הוא שלם
//                if (!text.matches("[-]?\\d+")) {
//                    throw new Exception("Invalid int value: " + text);
//                }
//                return Integer.parseInt(text);
//            case DOUBLE:
//                // בודקים התאמה ל-double
//                if (!text.matches("[-]?\\d+(\\.\\d+)?")) {
//                    // אפשר להוסיף עוד דפוסים (כמו .5, 5., וכו')
//                    throw new Exception("Invalid double value: " + text);
//                }
//                return Double.parseDouble(text);
//            case BOOLEAN:
//                if (!text.equals("true") && !text.equals("false")) {
//                    throw new Exception("Invalid boolean value: " + text);
//                }
//                return Boolean.parseBoolean(text);
//            case STRING:
//                // בודקים אם מוקף במרכאות
//                if (!text.matches("\".*\"")) {
//                    throw new Exception("Invalid string value: " + text);
//                }
//                // מורידים מרכאות
//                return text.substring(1, text.length() - 1);
//            case CHAR:
//                // בודקים אם מוקף ב'...'
//                if (!text.matches("\\'.\\'")) {
//                    // אפשר לחדד יותר, לאפשר \' וכו'
//                    throw new Exception("Invalid char value: " + text);
//                }
//                return text.charAt(1);
//            default:
//                throw new Exception("Unknown type");
//        }
//    }

}
