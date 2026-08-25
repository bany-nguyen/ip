package commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;
import tasks.Task;

/** Marks a numbered task as not completed. */
public class UnmarkCommand extends TaskIndexCommand {

    /**
     * Creates an unmark command from parsed command values.
     *
     * @param values values extracted from the user's input
     */
    public UnmarkCommand(java.util.Map<String, String> values) {
        super(values);
    }

    /** Marks the selected task as not done, persists the list, and reports the result. */
    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        Integer taskIndex = getTaskIndex(ui);
        if (taskIndex == null) {
            return;
        }

        Task task = tasks.unmarkTask(taskIndex);
        if (task == null) {
            ui.showOutOfBoundIndex("unmark");
            return;
        }

        if (saveTasks(tasks, ui, repository)) {
            ui.showUnmarkTask(task);
        }
    }
}
