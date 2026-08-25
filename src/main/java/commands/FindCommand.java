package commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Displays tasks whose descriptions contain the search text. */
public class FindCommand extends Command {
    /** Parsed command values containing the search description. */
    private final Map<String, String> values;

    /**
     * Creates a find command from parsed command values.
     *
     * @param values values extracted from the user's input
     */
    public FindCommand(Map<String, String> values) {
        this.values = values;
    }

    /** Searches task descriptions and displays either matches or an error. */
    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        String query = values.get("description");
        if (query == null || query.isBlank()) {
            ui.showInvalidCommand();
            return;
        }

        List<Task> matches = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            if (task.getDescription().contains(query)) {
                matches.add(task);
            }
        }

        if (matches.isEmpty()) {
            ui.showNoSuchTask();
        } else {
            ui.showMatchedTasks(matches);
        }
    }
}
