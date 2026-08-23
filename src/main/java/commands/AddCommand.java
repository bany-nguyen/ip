package commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;
import errors.InvalidTaskType;
import parsers.DateTimeParser;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.ToDo;
import utilities.CommandValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Creates and persists TODO, DEADLINE, and EVENT tasks. */
public class AddCommand extends Command {
    private final Map<String, String> values;
    private final List<String> tagNames;
    private final CommandValidator validator;

    /**
     * Creates an add command from parsed values and tag names.
     *
     * @param values values extracted from the user's input
     * @param tagNames tags in their original input order
     * @param validator validates required and extra tags
     */
    public AddCommand(Map<String, String> values, List<String> tagNames,
                      CommandValidator validator) {
        this.values = values;
        this.tagNames = tagNames;
        this.validator = validator;
    }

    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        String type = values.get("command");
        String duplicateTag = validator.findDuplicateCriticalTag(type, tagNames);
        if (duplicateTag != null) {
            ui.showDuplicateTag(duplicateTag);
            return;
        }

        Task task = createTask(ui);
        if (task == null) {
            return;
        }

        if (validator.hasTagWarning(type, tagNames)) {
            ui.showTagWarning();
        }

        tasks.addTask(task);
        if (saveTasks(tasks, ui, repository)) {
            ui.showAddTask(task, tasks.getSize());
        }
    }

    private Task createTask(Ui ui) {
        String type = values.get("command");
        String description = values.get("description");

        if (description == null || description.isBlank()) {
            ui.showInvalidTaskDescription();
            return null;
        }

        try {
            return switch (type) {
                case "TODO" -> new ToDo(description, Task.allocateId());
                case "DEADLINE" -> createDeadline(description, ui);
                case "EVENT" -> createEvent(description, ui);
                default -> throw new InvalidTaskType("Unknown task type: " + type);
            };
        } catch (IllegalStateException e) {
            ui.showTaskCreationFail();
            return null;
        }
    }

    private Task createDeadline(String description, Ui ui) {
        String by = values.get("by");
        if (by == null || by.isBlank()) {
            ui.showInvalidTaskInitiation();
            return null;
        }

        try {
            LocalDateTime dateTime = DateTimeParser.createLocalDateTime(by);
            return new Deadline(description, dateTime, Task.allocateId());
        } catch (IllegalArgumentException e) {
            ui.showInvalidDateTime();
            return null;
        }
    }

    private Task createEvent(String description, Ui ui) {
        String from = values.get("from");
        String to = values.get("to");
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            ui.showInvalidTaskInitiation();
            return null;
        }

        try {
            LocalDateTime fromDateTime = DateTimeParser.createLocalDateTime(from);
            LocalDateTime toDateTime = DateTimeParser.createLocalDateTime(to);
            if (toDateTime.isBefore(fromDateTime)) {
                ui.showInvalidEventRange();
                return null;
            }
            return new Event(description, fromDateTime, toDateTime,
                    Task.allocateId());
        } catch (IllegalArgumentException e) {
            ui.showInvalidDateTime();
            return null;
        }
    }
}
