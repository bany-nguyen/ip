package bany.commands;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;
import bany.errors.InvalidTaskType;
import bany.parsers.DateTimeParser;
import bany.tasks.Deadline;
import bany.tasks.Event;
import bany.tasks.Task;
import bany.tasks.ToDo;
import bany.utilities.CommandValidator;

/** Creates and persists TODO, DEADLINE, and EVENT tasks. */
public class AddCommand extends Command {
    /** Parsed command values such as the type, description, and date tags. */
    private final Map<String, String> values;
    /** Tag names in the order in which they appeared in the input. */
    private final List<String> tagNames;
    /** Rules used to validate required, duplicate, and extra tags. */
    private final CommandValidator validator;

    /**
     * Creates an add command from parsed values and tag names.
     *
     * @param values values extracted from the user's input.
     * @param tagNames tags in their original input order.
     * @param validator validates required and extra tags.
     */
    public AddCommand(Map<String, String> values, List<String> tagNames,
                      CommandValidator validator) {
        this.values = values;
        this.tagNames = tagNames;
        this.validator = validator;
    }

    /** Creates, stores, persists, and reports the new task. */
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

    /** Creates the task type requested by the parsed command. */
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

    /** Creates a deadline after validating its date-time value. */
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

    /** Creates an event after validating both date-times and their order. */
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
