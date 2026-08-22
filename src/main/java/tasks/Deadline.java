package tasks;

import enums.TaskType;

public class Deadline extends Task {
    private final String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    public Deadline(String description, String by, int id) {
        super(id, description);
        this.by = by;
    }

    public String getBy() {
        return by;
    }

    public String getDateString() {
        return " (by: " + by + ")";
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
