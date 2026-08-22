package errors;

public class InvalidTaskType extends RuntimeException {
    public InvalidTaskType(String message) {
        super(message);
    }
}
