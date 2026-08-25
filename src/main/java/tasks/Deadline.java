package tasks;

import enums.TaskType;
import parsers.DateTimeParser;

import java.time.LocalDateTime;
import java.util.Objects;

/** Represents a task that must be completed by a specified date-time. */
public class Deadline extends Task {
    /** Date-time by which this task should be completed. */
    private final LocalDateTime by;

    /**
     * Creates a deadline from a formatted date-time string.
     *
     * @param description task description
     * @param by deadline in {@code dd-MM-yyyy HH:mm} format
     * @param id task ID
     */
    public Deadline(String description, String by, int id) {
        this(description, DateTimeParser.createLocalDateTime(by), id);
    }

    /**
     * Creates a deadline from a typed date-time value.
     *
     * @param description task description
     * @param by deadline date-time
     * @param id task ID
     */
    public Deadline(String description, LocalDateTime by, int id) {
        super(id, description);
        this.by = Objects.requireNonNull(by, "Deadline date-time cannot be null.");
    }

    /**
     * Returns the deadline in the user-facing format used by the task file.
     *
     * @return formatted deadline date-time
     */
    public String getBy() {
        return DateTimeParser.formatLocalDateTime(by);
    }

    /**
     * Returns the typed deadline value for date-time operations.
     *
     * @return deadline date-time
     */
    public LocalDateTime getByDateTime() {
        return by;
    }

    /**
     * Returns the date information appended to this task's display text.
     *
     * @return formatted deadline display text
     */
    public String getDateString() {
        return " (by: " + getBy() + ")";
    }
    /**
     * Returns the full task type name.
     *
     * @return {@code DEADLINE}
     */
    @Override
    public String getType() {
        return TaskType.DEADLINE.getType();
    }

    /**
     * Returns the abbreviated task type marker.
     *
     * @return {@code D}
     */
    @Override
    public String getTypeShort() {
        return TaskType.DEADLINE.getTypeShort();
    }

}
