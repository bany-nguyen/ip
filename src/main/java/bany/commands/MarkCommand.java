package bany.commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;
import bany.tasks.Task;

import java.util.Map;

/** Marks a numbered task as completed. */
public class MarkCommand extends TaskIndexCommand {

    /**
     * Creates a mark command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public MarkCommand(Map<String, String> values) {
        super(values);
    }

    /** Marks the selected task as done, persists the list, and reports the result. */
    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        Integer taskIndex = getTaskIndex(ui);
        if (taskIndex == null) {
            return;
        }

        Task task = tasks.markTask(taskIndex);
        if (task == null) {
            ui.showOutOfBoundIndex("mark");
            return;
        }

        if (saveTasks(tasks, ui, repository)) {
            ui.showMarkTask(task);
        }
    }
}
