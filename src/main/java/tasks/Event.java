package tasks;

import enums.TaskType;
import parsers.DateTimeParser;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    public Event(String description, String from, String to, int id) {
        this(description,
                DateTimeParser.createLocalDateTime(from),
                DateTimeParser.createLocalDateTime(to),
                id);
    }

    public Event(String description, LocalDateTime from, LocalDateTime to, int id) {
        super(id, description);
        this.from = Objects.requireNonNull(from, "Event start date-time cannot be null.");
        this.to = Objects.requireNonNull(to, "Event end date-time cannot be null.");
        validateDateRange(from, to);
    }

    private static void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date-time cannot be before its start.");
        }
    }

    /**
     * Returns the event start in the user-facing format used by the task file.
     *
     * @return formatted event start date-time
     */
    public String getFrom() {
        return DateTimeParser.formatLocalDateTime(from);
    }

    /**
     * Returns the event end in the user-facing format used by the task file.
     *
     * @return formatted event end date-time
     */
    public String getTo() {
        return DateTimeParser.formatLocalDateTime(to);
    }

    /**
     * Returns the typed event start for date-time operations.
     *
     * @return event start date-time
     */
    public LocalDateTime getFromDateTime() {
        return from;
    }

    /**
     * Returns the typed event end for date-time operations.
     *
     * @return event end date-time
     */
    public LocalDateTime getToDateTime() {
        return to;
    }

    public String getDateString() {
        return " (from: " + getFrom() + " to: " + getTo() + ")";
    }

    @Override
    public String getTypeShort() {
        return TaskType.EVENT.getTypeShort();
    }

    @Override
    public String getType() {
        return TaskType.EVENT.getType();
    }

}
