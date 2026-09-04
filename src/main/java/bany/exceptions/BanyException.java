package bany.exceptions;

/** Base checked exception for Bany-specific failures. */
public class BanyException extends Exception {
    public BanyException(String message) {
        super(message);
    }
}
