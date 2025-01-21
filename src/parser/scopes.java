//package parser;
//
//import rules.Variables;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.*;
//import java.util.regex.*;
//
//public class scopes {
//
//
//        List<String> scopes = new ArrayList<>();
//        Map<String, List<String>> scopeVariables = new HashMap<>();
//
//        //constructor
//        public scopes() {
//        }
//
//        public List<String> extractScopes(List<String> lines) {
//            this.scopes = new ArrayList<>();
//            Stack<Integer> scopeStack = new Stack<>();
//            StringBuilder currentScope = new StringBuilder();
//
//            boolean insideClass = false;
//            boolean insideFunction = false;
//
//            for (String line : lines) {
//                line = line.trim();
//
//                if (line.matches("class\\s+[a-zA-Z_][a-zA-Z0-9_]*")) {
//                    if (scopeStack.isEmpty()) {
//                        insideClass = true;
//                    }
//                }
//
//                if (line.matches("(public|private|protected|static|void)\\s+[a-zA-Z_][a-zA0-9_]*\\s*\\(.*\\)\\s*\\{")) {
//                    if (scopeStack.isEmpty()) {
//                        insideFunction = true;
//                    }
//                }
//
//                if (line.contains("{")) {
//                    scopeStack.push(1);
//                    currentScope.append(line).append("\n");
//                }
//
//                if (line.contains("}")) {
//                    if (!scopeStack.isEmpty()) {
//                        scopeStack.pop();
//                        currentScope.append(line).append("\n");
//                    }
//
//                    if (scopeStack.isEmpty()) {
//                        if (insideClass || insideFunction) {
//                            scopes.add(currentScope.toString().trim());
//
//                        }
//                        currentScope.setLength(0);
//                        insideClass = false;
//                        insideFunction = false;
//                    }
//                }
//            }
//            return scopes;
//        }
//
//
//
//    private void globalVariables() {
//        for (String scope : this.scopes) {
//            this.scopeVariables = new HashMap<>();
//            List<String> lines = Arrays.asList(scope.split("\n"));
//            for (int i = 0; i < lines.size(); i++) {
//                scopeVariables.put(lines.get(i), new ArrayList<>());
//                for (int j = i + 1; j < lines.size(); j++) {
//
//                }
//
//
//            }
//        }
//    }
//
//    private boolean isScopeValid(String scope, List<String> globalVariables) {
//        Variables variables = new Variables();
//        variables.Variables(Arrays.asList(scope.split("\n")));
//
//        return true;
//
//    }
//
//}
