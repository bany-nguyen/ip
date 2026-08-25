package bany.commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;

/** Displays an error for an unrecognised command. */
public class InvalidCommand extends Command {
    /** Closest recognized command to suggest, if one was found. */
    private final String suggestion;

    /**
     * Creates an invalid command with an optional suggested command.
     *
     * @param suggestion the closest valid command, or null if unavailable.
     */
    public InvalidCommand(String suggestion) {
        this.suggestion = suggestion;
    }

    /** Displays the invalid-command message and any available suggestion. */
    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        ui.showInvalidCommand(suggestion);
    }
}
