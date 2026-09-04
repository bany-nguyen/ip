package bany.commands;

import java.util.Map;

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
     * @return zero-based task index, or null if the input is invalid.
     */
    protected Integer getTaskIndex() {
        String description = values.get("description");
        if (description == null || description.isBlank()
                || !Helper.isInteger(description)) {
            return null;
        }
        return Integer.parseInt(description) - 1;
    }
}
