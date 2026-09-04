package bany.commands;

import java.util.Objects;

/** Represents one user-facing message and its presentation level. */
public record ResponseMessage(
        MessageLevel level,
        String text) {
    /**
     * Validates the level and text of this response message.
     *
     * @throws NullPointerException if the level or text is null.
     * @throws IllegalArgumentException if the text is blank.
     */
    public ResponseMessage {
        level = Objects.requireNonNull(level, "Message level cannot be null");
        text = Objects.requireNonNull(text, "Message text cannot be null");

        if (text.isBlank()) {
            throw new IllegalArgumentException("Message text cannot be blank");
        }
    }

    /** Creates an informational response message. */
    public static ResponseMessage info(String text) {
        return new ResponseMessage(MessageLevel.INFO, text);
    }

    /** Creates a warning response message. */
    public static ResponseMessage warning(String text) {
        return new ResponseMessage(MessageLevel.WARNING, text);
    }

    /** Creates an error response message. */
    public static ResponseMessage error(String text) {
        return new ResponseMessage(MessageLevel.ERROR, text);
    }

}
