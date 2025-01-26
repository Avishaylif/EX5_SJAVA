    package VariablesManegment;
import errors.ValidationException;

    /**
     * Represents a variable with a name, type, value, and scope information.
     * Supports various data types and handles initialization and finality constraints.
     */
        public class Variable {
        /**
         * Enumeration of supported variable types.
         */
            public enum Type {
            /** Represents an integer type. */
            INT,
            /** Represents a double-precision floating-point type. */
            DOUBLE,
            /** Represents a string type. */
            STRING,
            /** Represents a boolean type. */
            BOOLEAN,
            /** Represents a character type. */
            CHAR;
            }

        /** The name of the variable. */
        private String name;
        /** The type of the variable. */
        private Type type;
        /** Indicates whether the variable is final. */
        private boolean isFinal;
        /** Indicates whether the variable has been initialized. */
        private boolean isInitialized;
        /** The value assigned to the variable. */
        private Object value;

        /**
         * Constructs a new {@code Variable} with the specified attributes.
         *
         * @param name          the name of the variable
         * @param type          the type of the variable
         * @param isFinal       {@code true} if the variable is final; {@code false} otherwise
         * @param isInitialized {@code true} if the variable has been initialized; {@code false} otherwise
         * @param value         the value assigned to the variable
         */

        public Variable(String name, Type type, boolean isFinal, boolean isInitialized, Object value) {
            this.name = name;
            this.type = type;
            this.isFinal = isFinal;
            this.isInitialized = isInitialized;
            this.value = value;
        }
        /**
         * Constructs a new {@code Variable} by parsing the type from a string.
         *
         * @param name          the name of the variable
         * @param typeString    the type of the variable as a string
         * @param isFinal       {@code true} if the variable is final; {@code false} otherwise
         * @param isInitialized {@code true} if the variable has been initialized; {@code false} otherwise
         * @param value         the value assigned to the variable
         * @throws ValidationException if the type string is unknown
         */
        public Variable(String name, String typeString, boolean isFinal, boolean isInitialized, Object value)
                throws ValidationException {
            Type type = parseType(typeString);
            this.name = name;
            this.type = type;
            this.isFinal = isFinal;
            this.isInitialized = isInitialized;
            this.value = value;
        }


        /**
         * Returns the name of the variable.
         *
         * @return the variable's name
         */
        public String getName(){
            return name;
        }
        /**
         * Indicates whether the variable has been initialized.
         *
         * @return {@code true} if the variable is initialized; {@code false} otherwise
         */
        public boolean isInitialized() {
            return isInitialized;
        }

        /**
         * Returns the depth of the scope in which the variable is declared.
         *
         * @return the scope depth
         */
        public Type getType() {
            return type;
        }

        /**
         * Returns the value assigned to the variable.
         *
         * @return the variable's value
         */
        public Object getValue() {
            return value;
        }

        /**
         * Indicates whether the variable is final.
         *
         * @return {@code true} if the variable is final; {@code false} otherwise
         */
        public boolean isFinal() {
            return isFinal;
        }
        /**
         * Sets the initialization status of the variable.
         *
         * @param initialized {@code true} to mark the variable as initialized; {@code false} otherwise
         */
        public void setInitialized(boolean initialized) {
            isInitialized = initialized;
        }

        /**
         * Sets the type of the variable.
         *
         * @param type the new type of the variable
         */
        public void setType(Type type) {
            this.type = type;
        }

        /**
         * Sets the value of the variable.
         *
         * @param value the new value to assign to the variable
         */
        public void setValue(Object value) {
            this.value = value;
        }
        /**
         * Parses a type from its string representation.
         *
         * @param typeStr the type as a string
         * @return the corresponding {@code Type} enum value
         * @throws ValidationException if the type string is unknown
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

        /**
         * Returns a string representation of the variable, including its name, type, finality,
         * initialization status, value, and scope depth.
         *
         * @return a string representation of the variable
         */
            @Override
            public String toString() {
                return "Variable{" +
                        "name='" + name + '\'' +
                        ", type=" + type +
                        ", isFinal=" + isFinal +
                        ", isInitialized=" + isInitialized +
                        ", value=" + value +
                        '}';
            }

        }
