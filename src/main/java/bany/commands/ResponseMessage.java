package bany.commands;

import java.util.Objects;

public record ResponseMessage (
        MessageLevel level,
        String text) {
    public ResponseMessage {
        level = Objects.requireNonNull(level, "Message level cannot be null");
        text = Objects.requireNonNull(text, "Message text cannot be null");

        if (text.isBlank()) {
            throw new IllegalArgumentException("Message text cannot be blank");
        }
    }

    public static ResponseMessage info(String text) {
        return new ResponseMessage(MessageLevel.INFO, text);
    }

    public static ResponseMessage warning(String text) {
        return new ResponseMessage(MessageLevel.WARNING, text);
    }

    public static ResponseMessage error(String text) {
        return new ResponseMessage(MessageLevel.ERROR, text);
    }

}
