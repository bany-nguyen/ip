package tasks;

import enums.TaskType;
import parsers.DateTimeParser;

import java.time.LocalDateTime;
import java.util.Objects;

public class Deadline extends Task {
    private final LocalDateTime by;

    public Deadline(String description, String by, int id) {
        this(description, DateTimeParser.createLocalDateTime(by), id);
    }

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

    public String getDateString() {
        return " (by: " + getBy() + ")";
    }
    @Override
    public String getType() {
        return TaskType.DEADLINE.getType();
    }

    @Override
    public String getTypeShort() {
        return TaskType.DEADLINE.getTypeShort();
    }

}
