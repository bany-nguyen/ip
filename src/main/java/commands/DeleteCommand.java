package commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;
import tasks.Task;

/** Deletes a numbered task from Bany's task list. */
public class DeleteCommand extends TaskIndexCommand {

    /**
     * Creates a delete command from parsed command values.
     *
     * @param values values extracted from the user's input
     */
    public DeleteCommand(java.util.Map<String, String> values) {
        super(values);
    }

    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        Integer taskIndex = getTaskIndex(ui);
        if (taskIndex == null) {
            return;
        }

        Task task = tasks.deleteTask(taskIndex);
        if (task == null) {
            ui.showOutOfBoundIndex("delete");
            return;
        }

        if (saveTasks(tasks, ui, repository)) {
            ui.showDeleteTask(task, tasks.getSize());
        }
    }
}
