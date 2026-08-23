package commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;

/** Displays an error for an unrecognised command. */
public class InvalidCommand extends Command {
    private final String suggestion;

    /**
     * Creates an invalid command with an optional suggested command.
     *
     * @param suggestion the closest valid command, or null if unavailable
     */
    public InvalidCommand(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        ui.showInvalidCommand(suggestion);
    }
}
