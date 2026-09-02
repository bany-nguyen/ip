package bany.commands;

/**
 * Describes the user-facing outcome of executing one command.
 *
 * @param message response text to display to the user.
 * @param shouldExit whether the application should close after showing the message.
 */
public record CommandResult(
        String message,
        boolean shouldExit
) {
}
