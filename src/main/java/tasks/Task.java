package tasks;

import enums.TaskStatus;
import enums.TaskType;

public abstract class Task {
    private final String description;
    private boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? TaskStatus.DONE.getStatus()
                : TaskStatus.NOT_DONE.getStatus()); // mark done task with X
    }

    public void mark() {
        isDone = true;
    }

    public void unmark() {
        isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getType();

    public abstract String getTypeShort();
    /**
     * Returns this task's date information, including its leading space when present.
     *
     * @return task date information, or an empty string for a ToDo
     */
    public abstract String getDateInfo();

    @Override
    public String toString() {
        return String.format("[%s][%s] %s%s", getTypeShort(), getStatusIcon(), description, getDateInfo());
    }
}
