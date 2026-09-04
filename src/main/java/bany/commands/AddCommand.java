package bany.commands;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.exceptions.InvalidTaskType;
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
        TaskCreationResult creationResult = createTask(responder);
        if (!creationResult.isSuccessful()) {
            return new CommandResult(
                    creationResult.validationMessage(),
                    false
            );
        }
        Task task = creationResult.task();

        boolean hasTagWarning = validator.hasTagWarning(type, tagNames);
        tasks.addTask(task);
        try {
            saveTasks(tasks, responder, repository);
            String response = responder.respondAddTask(task, tasks.getSize());
            if (hasTagWarning) {
                response = responder.respondTagWarning() + System.lineSeparator()
                        + System.lineSeparator() + response;
            }
            return new CommandResult(
                    response,
                    false
            );
        } catch (IOException e) {
            tasks.removeTask(tasks.getSize() - 1);
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
     * @return a complete task or the user-facing validation message that prevents creation.
     */
    private TaskCreationResult createTask(Responder responder) {
        String type = values.get("command");
        String description = values.get("description");

        if (description == null || description.isBlank()) {
            return TaskCreationResult.failure(responder.respondInvalidTaskDescription());
        }

        return switch (type) {
            case "TODO" -> TaskCreationResult.success(new ToDo(description, Task.allocateId()));
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
     * @return a complete deadline or the validation message that prevents creation.
     */
    private TaskCreationResult createDeadline(String description, Responder responder) {
        String by = values.get("by");
        if (by == null || by.isBlank()) {
            return TaskCreationResult.failure(responder.respondInvalidTaskInitiation());
        }

        try {
            LocalDateTime dateTime = DateTimeParser.createLocalDateTime(by);
            return TaskCreationResult.success(new Deadline(description, dateTime, Task.allocateId()));
        } catch (IllegalArgumentException e) {
            return TaskCreationResult.failure(responder.respondInvalidDateTime());
        }
    }

    /**
     * Creates an event after validating both date-times and their order.
     *
     * @param description event description.
     * @param responder response builder used while validating the event.
     * @return a complete event or the validation message that prevents creation.
     */
    private TaskCreationResult createEvent(String description, Responder responder) {
        String from = values.get("from");
        String to = values.get("to");
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            return TaskCreationResult.failure(responder.respondInvalidTaskInitiation());
        }

        try {
            LocalDateTime fromDateTime = DateTimeParser.createLocalDateTime(from);
            LocalDateTime toDateTime = DateTimeParser.createLocalDateTime(to);
            if (toDateTime.isBefore(fromDateTime)) {
                return TaskCreationResult.failure(responder.respondInvalidEventRange());
            }
            return TaskCreationResult.success(new Event(description, fromDateTime, toDateTime,
                    Task.allocateId()));
        } catch (IllegalArgumentException e) {
            return TaskCreationResult.failure(responder.respondInvalidDateTime());
        }
    }

    /**
     * Captures either a fully validated task or the message explaining why it cannot be created.
     *
     * @param task created task, or {@code null} for a validation failure.
     * @param validationMessage validation message, or {@code null} for a successful creation.
     */
    private record TaskCreationResult(Task task, String validationMessage) {
        /**
         * Creates a successful task-creation result.
         *
         * @param task fully validated task.
         * @return successful creation result.
         */
        private static TaskCreationResult success(Task task) {
            return new TaskCreationResult(task, null);
        }

        /**
         * Creates a validation-failure result.
         *
         * @param validationMessage explanation suitable for the user.
         * @return failed creation result.
         */
        private static TaskCreationResult failure(String validationMessage) {
            return new TaskCreationResult(null, validationMessage);
        }

        /**
         * Returns whether task creation completed validation.
         *
         * @return {@code true} when a task is available for storage.
         */
        private boolean isSuccessful() {
            return task != null;
        }
    }
}
