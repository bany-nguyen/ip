package bany.commands;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.errors.InvalidTaskType;
import bany.gui.Responder;
import bany.parsers.DateTimeParser;
import bany.tasks.Deadline;
import bany.tasks.Event;
import bany.tasks.Task;
import bany.tasks.ToDo;
import bany.utilities.CommandValidator;

/** Handles the creation and persistence of TODO, DEADLINE, and EVENT tasks. */
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

    /**
     * Creates, stores, and persists a task before returning a response.
     *
     * @return command outcome describing success, validation failure, or a save failure.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        String type = values.get("command");
        String duplicateTag = validator.findDuplicateCriticalTag(type, tagNames);
        if (duplicateTag != null) {
            return new CommandResult(
                    responder.respondDuplicateTag(duplicateTag),
                    false
            );
        }
        Task task = createTask(responder);
        if (task == null) {
            return new CommandResult(
                    responder.respondInvalidTaskDescription(),
                    false
            );
        }

        if (validator.hasTagWarning(type, tagNames)) {
            return new CommandResult(
                    responder.respondTagWarning(),
                    false
            );
        }
        tasks.addTask(task);
        try {
            saveTasks(tasks, responder, repository);
            return new CommandResult(
                    responder.respondAddTask(task, tasks.getSize()),
                    false
            );
        } catch (IOException e) {
            return new CommandResult(
                    Responder.ErrorResponder.respondFileUpdateError(),
                    false
            );
        }
    }

    /**
     * Creates the task type requested by the parsed command.
     *
     * @param responder response builder used while validating task details.
     * @return created task, or {@code null} when its details are invalid.
     */
    private Task createTask(Responder responder) {
        String type = values.get("command");
        String description = values.get("description");

        if (description == null || description.isBlank()) {
            responder.respondInvalidTaskDescription();
            return null;
        }

        return switch (type) {
            case "TODO" -> new ToDo(description, Task.allocateId());
            case "DEADLINE" -> createDeadline(description, responder);
            case "EVENT" -> createEvent(description, responder);
            default -> throw new InvalidTaskType("Unknown task type: " + type);
        };
    }

    /**
     * Creates a deadline after validating its date-time value.
     *
     * @param description deadline description.
     * @param responder response builder used while validating the deadline.
     * @return created deadline, or {@code null} when its details are invalid.
     */
    private Task createDeadline(String description, Responder responder) {
        String by = values.get("by");
        if (by == null || by.isBlank()) {
            responder.respondInvalidTaskInitiation();
            return null;
        }

        try {
            LocalDateTime dateTime = DateTimeParser.createLocalDateTime(by);
            return new Deadline(description, dateTime, Task.allocateId());
        } catch (IllegalArgumentException e) {
            responder.respondInvalidDateTime();
            return null;
        }
    }

    /**
     * Creates an event after validating both date-times and their order.
     *
     * @param description event description.
     * @param responder response builder used while validating the event.
     * @return created event, or {@code null} when its details are invalid.
     */
    private Task createEvent(String description, Responder responder) {
        String from = values.get("from");
        String to = values.get("to");
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            responder.respondInvalidTaskInitiation();
            return null;
        }

        try {
            LocalDateTime fromDateTime = DateTimeParser.createLocalDateTime(from);
            LocalDateTime toDateTime = DateTimeParser.createLocalDateTime(to);
            if (toDateTime.isBefore(fromDateTime)) {
                responder.respondInvalidEventRange();
                return null;
            }
            return new Event(description, fromDateTime, toDateTime,
                    Task.allocateId());
        } catch (IllegalArgumentException e) {
            responder.respondInvalidDateTime();
            return null;
        }
    }
}
