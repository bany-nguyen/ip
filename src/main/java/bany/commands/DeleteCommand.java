package bany.commands;

import java.io.IOException;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

/** Deletes a numbered task from Bany's task list. */
public class DeleteCommand extends TaskIndexCommand {

    /**
     * Creates a delete command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public DeleteCommand(Map<String, String> values) {
        super(values);
    }

    /**
     * Deletes the selected task, persists the updated list, and returns the outcome.
     *
     * @return command outcome describing the deletion or an error.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        Integer taskIndex = getTaskIndex(responder);
        if (taskIndex == null) {
            return new CommandResult(
                    responder.respondInvalidCommand(),
                    false
            );
        }

        Task task = tasks.deleteTask(taskIndex);
        if (task == null) {
            return new CommandResult(
                    responder.respondOutOfBoundIndex("delete", tasks.getSize()),
                    false
            );
        }

        try {
            saveTasks(tasks, responder, repository);
            return new CommandResult(
                    responder.respondDeleteTask(task, tasks.getSize()),
                    false
            );
        } catch (IOException e) {
            return new CommandResult(
                    Responder.ErrorResponder.respondFileUpdateError(),
                    false
            );
        }
    }
}
