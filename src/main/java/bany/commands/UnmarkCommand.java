package bany.commands;

import java.io.IOException;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

/** Marks a numbered task as not completed. */
public class UnmarkCommand extends TaskIndexCommand {

    /**
     * Creates an unmark command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public UnmarkCommand(Map<String, String> values) {
        super(values);
    }

    /**
     * Marks the selected task as not done, persists the list, and returns the outcome.
     *
     * @return command outcome describing the change or an error.
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

        Task task = tasks.unmarkTask(taskIndex);
        if (task == null) {
            return new CommandResult(
                    responder.respondOutOfBoundIndex("unmark", tasks.getSize()),
                    false
            );
        }

        try {
            saveTasks(tasks, responder, repository);
            return new CommandResult(
                    responder.respondUnmarkTask(task),
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
