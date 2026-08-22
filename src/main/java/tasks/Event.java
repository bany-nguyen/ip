package tasks;

import enums.TaskType;

public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from,  String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public Event(String description, String from, String to, int id) {
        super(id, description);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDateString() {
        return " (from: " + from + " to: " + to + ")";
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
