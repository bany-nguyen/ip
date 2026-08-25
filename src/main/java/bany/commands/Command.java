package bany.commands;

import java.io.IOException;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;

/**
 * Represents one executable Bany command.
 *
 * <p>Concrete commands contain the behavior for one user action. This keeps
 * the main application loop independent from the details of adding, deleting,
 * or searching tasks.</p>
 */
public abstract class Command {

    /** Creates a command base object for a concrete command implementation. */
    public Command() {
    }

    /**
     * Executes this command using the application's shared components.
     *
     * @param tasks the current task list.
     * @param ui the user-interface component.
     * @param repository the task persistence component.
     */
    public abstract void execute(
            TaskStorage tasks,
            Ui ui,
            TaskFileRepository repository);

    /**
     * Indicates whether this command should end the application.
     *
     * @return true only for the exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Persists the current task list and displays an error if writing fails.
     *
     * @param tasks the current task list.
     * @param ui the user-interface component used for error reporting.
     * @param repository the task persistence component.
     * @return true if the list was saved successfully.
     */
    protected boolean saveTasks(TaskStorage tasks, Ui ui,
                                TaskFileRepository repository) {
        try {
            repository.save(tasks.getTasks());
            return true;
        } catch (IOException e) {
            Ui.ErrorUi.showFileUpdateError();
            return false;
        }
    }
}
