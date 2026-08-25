package bany.commands;

import java.util.Map;

import bany.Ui;
import bany.utilities.Helper;

/**
 * Base class for commands that operate on a numbered task.
 */
public abstract class TaskIndexCommand extends Command {
    /** Parsed command values, including the user-provided task number. */
    protected final Map<String, String> values;

    /**
     * Creates a task-index command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    protected TaskIndexCommand(Map<String, String> values) {
        this.values = values;
    }

    /**
     * Converts the one-based number typed by the user into a zero-based index.
     *
     * @param ui user-interface component used for invalid-input feedback.
     * @return zero-based task index, or null if the input is invalid.
     */
    protected Integer getTaskIndex(Ui ui) {
        String description = values.get("description");
        if (description == null || description.isBlank()
                || !Helper.isInteger(description)) {
            ui.showInvalidCommand();
            return null;
        }
        return Integer.parseInt(description) - 1;
    }
}
