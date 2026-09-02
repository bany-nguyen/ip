package bany.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

/** Finds tasks whose descriptions contain the search text. */
public class FindCommand extends Command {
    /** Parsed command values containing the search description. */
    private final Map<String, String> values;

    /**
     * Creates a find command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public FindCommand(Map<String, String> values) {
        this.values = values;
    }

    /**
     * Searches task descriptions and returns either matches or an error response.
     *
     * @return command outcome containing matching tasks or an error message.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        String query = values.get("description");

        if (query == null || query.isBlank()) {
            return new CommandResult(
                    responder.respondInvalidCommand(),
                    false
            );
        }

        List<Task> matches = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            if (task.getDescription().contains(query)) {
                matches.add(task);
            }
        }

        if (matches.isEmpty()) {
            return new CommandResult(
                    responder.respondNoSuchTask(),
                    false
            );
        } else {
            return new CommandResult(
                    responder.respondMatchedTasks(matches),
                    false
            );
        }
    }
}
