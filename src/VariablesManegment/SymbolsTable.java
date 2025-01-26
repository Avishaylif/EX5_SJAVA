package VariablesManegment;

import java.util.*;
/**
 * Represents a symbol table that manages variable declarations across multiple scopes.
 * Utilizes a stack-based approach to handle nested scopes, allowing for variable shadowing.
 *
 * <p>The {@code SymbolsTable} maintains a stack of scopes, where each scope is a mapping
 * from variable names to their corresponding {@link Variable} instances. It provides
 * methods to open and close scopes, add variables, and lookup variables across all
 * active scopes.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SymbolsTable symbolsTable = new SymbolsTable();
 * symbolsTable.openScope(); // Enter a new scope
 * Variable var = new Variable("x", Variable.Type.INT, false, true, 10);
 * symbolsTable.addVariable(var);
 * Variable retrievedVar = symbolsTable.lookup("x");
 * symbolsTable.closeScope(); // Exit the current scope
 * }</pre>
 */
public class SymbolsTable implements Iterable<Map<String, Variable>> {
    /**
     * A deque (double-ended queue) representing the stack of scopes.
     * Each scope is a map from variable names to {@link Variable} instances.
     */
    private Deque<Map<String, Variable>> scopes;

    /**
     * Constructs a new {@code SymbolsTable} and initializes it with a global scope.
     */
    public SymbolsTable() {
        this.scopes = new ArrayDeque<>();
        openScope();
    }

    /**
     * Opens a new scope by pushing a new empty scope onto the stack.
     * This new scope becomes the current active scope.
     */
    public void openScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Closes the current active scope by popping it from the stack.
     * If there are no scopes to close, an {@link IllegalStateException} is thrown.
     *
     * @throws IllegalStateException if there are no scopes to close
     */
    public void closeScope() {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No scope to close.");
        }
        scopes.pop();
    }


    /**
     * Retrieves the current depth of the active scope stack.
     *
     * @return the current scope depth
     */
    public int getCurrentScopeDepth() {
        return scopes.size() - 1; // למשל
    }


    /**
     * Adds a new variable to the current active scope.
     *
     * @param var the {@link Variable} instance to add
     * @throws Exception if a variable with the same name already exists in the current scope
     */
    public void addVariable(Variable var) throws Exception {
        Map<String, Variable> currentScope = scopes.peek();
        if (currentScope.containsKey(var.getName())) {
            throw new Exception("Variable " + var.getName() + " already declared in this scope");
        }
        currentScope.put(var.getName(), var);
    }

    /**
     * Retrieves a variable by its name across all active scopes.
     *
     * @param varName the name of the variable to retrieve
     * @return the {@link Variable} instance if found; {@code null} otherwise
     */
    public Variable getVariable(String varName) {
        for (Map<String, Variable> scope : scopes) {
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        return null;
    }
    /**
     * Returns an iterator over the scopes in the symbol table.
     *
     * <p>The iterator traverses the scopes from the most recent (innermost) to the outermost (global) scope.</p>
     *
     * @return an {@link Iterator} over the scopes
     */
    @Override
    public Iterator<Map<String, Variable>> iterator() {
        return scopes.iterator();
    }
    /**
     * Checks if a variable with the specified name exists in the current active scope.
     *
     * @param varName the name of the variable to check
     * @return {@code true} if the variable exists in the current scope; {@code false} otherwise
     */
    public boolean isVariableInCurrentScope(String varName) {
    Map<String, Variable> currentScope = scopes.peek();
        if (currentScope == null) {
        return false;
    }
        return currentScope.containsKey(varName);
}
    /**
     * An iterator implementation for the {@code SymbolsTable}.
     *
     * <p>Iterates over the scopes in the symbol table from the most recent to the oldest.</p>
     */

    private static class SymbolsTableIterator implements Iterator<Map<String, Variable>> {//todo: erase
        private Iterator<Map<String, Variable>> scopeIterator;

        public SymbolsTableIterator(Deque<Map<String, Variable>> scopes) {
            this.scopeIterator = scopes.iterator();
        }

        @Override
        public boolean hasNext() {
            return scopeIterator.hasNext();
        }

        @Override
        public Map<String, Variable> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return scopeIterator.next();
        }
    }
}
