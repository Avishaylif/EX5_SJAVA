package errors;

/**
 * Exception class representing validation errors during variable processing.
 */
public class ValidationException extends Exception {
    /**
     * Constructs a new {@code ValidationException} with the specified detail message.
     *
     * @param message the detail message explaining the validation error
     */
    public ValidationException(String message) {
        super(message);
    }
}