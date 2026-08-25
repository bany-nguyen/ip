package bany.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

import bany.enums.TaskType;
import bany.parsers.DateTimeParser;

/** Represents a task that occurs between a start and end date-time. */
public class Event extends Task {
    /** Start date-time of the event. */
    private final LocalDateTime from;
    /** End date-time of the event. */
    private final LocalDateTime to;

    /**
     * Creates an event from formatted date-time strings.
     *
     * @param description task description.
     * @param from event start in {@code dd-MM-yyyy HH:mm} format.
     * @param to event end in {@code dd-MM-yyyy HH:mm} format.
     * @param id task ID.
     */
    public Event(String description, String from, String to, int id) {
        this(description,
                DateTimeParser.createLocalDateTime(from),
                DateTimeParser.createLocalDateTime(to),
                id);
    }

    /**
     * Creates an event from typed date-time values.
     *
     * @param description task description.
     * @param from event start date-time.
     * @param to event end date-time.
     * @param id task ID.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to, int id) {
        super(id, description);
        this.from = Objects.requireNonNull(from, "Event start date-time cannot be null.");
        this.to = Objects.requireNonNull(to, "Event end date-time cannot be null.");
        validateDateRange(from, to);
    }

    /**
     * Ensures that the event's end is not before its start.
     *
     * @param from event start date-time.
     * @param to event end date-time.
     * @throws IllegalArgumentException if the end precedes the start.
     */
    private static void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date-time cannot be before its start.");
        }
    }

    /**
     * Returns the event start in the user-facing format used by the task file.
     *
     * @return formatted event start date-time.
     */
    public String getFrom() {
        return DateTimeParser.formatLocalDateTime(from);
    }

    /**
     * Returns the event end in the user-facing format used by the task file.
     *
     * @return formatted event end date-time.
     */
    public String getTo() {
        return DateTimeParser.formatLocalDateTime(to);
    }

    /**
     * Returns the typed event start for date-time operations.
     *
     * @return event start date-time.
     */
    public LocalDateTime getFromDateTime() {
        return from;
    }

    /**
     * Returns the typed event end for date-time operations.
     *
     * @return event end date-time.
     */
    public LocalDateTime getToDateTime() {
        return to;
    }

    /**
     * Returns the date information appended to this task's display text.
     *
     * @return formatted event display text.
     */
    public String getDateString() {
        return " (from: " + getFrom() + " to: " + getTo() + ")";
    }

    /**
     * Returns the abbreviated task type marker.
     *
     * @return {@code E}.
     */
    @Override
    public String getTypeShort() {
        return TaskType.EVENT.getTypeShort();
    }

    /**
     * Returns the full task type name.
     *
     * @return {@code EVENT}.
     */
    @Override
    public String getType() {
        return TaskType.EVENT.getType();
    }

}
