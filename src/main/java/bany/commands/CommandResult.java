package bany.commands;

import java.util.List;
import java.util.Objects;

/**
 * Describes the user-facing outcome of executing one command.
 *
 * @param messages response text to display to the user.
 * @param outcome whether the application should close after showing the message.
 */
public record CommandResult(
        List<ResponseMessage> messages,
        CommandOutcome outcome
) {
    public CommandResult {
        messages = List.copyOf(Objects.requireNonNull(messages, "Messages cannot be null."));
        outcome = Objects.requireNonNull(outcome, "Outcome cannot be null");

        if (messages.isEmpty()) {
            throw new IllegalArgumentException("A command result must contain a message.");
        }
    }

    public static CommandResult success(ResponseMessage ... messages) {
        return new CommandResult(
                List.of(messages), CommandOutcome.SUCCESS
        );
    }
}
