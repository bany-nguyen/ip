package bany.exceptions;

/** Signals that persisted task data does not follow Bany's expected format. */
public class TaskDataFormatException extends BanyException {
    public TaskDataFormatException(String message) {
        super(message);
    }
}
