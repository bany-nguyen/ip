package bany.commands;

import java.io.IOException;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

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

    /**
     * Marks the selected task as done, persists the list, and returns the outcome.
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

        Task task = tasks.markTask(taskIndex);
        if (task == null) {
            return new CommandResult(
                    responder.respondOutOfBoundIndex("mark", tasks.getSize()),
                    false
            );
        }

        try {
            saveTasks(tasks, responder, repository);
            return new CommandResult(
                    responder.respondMarkTask(task),
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
