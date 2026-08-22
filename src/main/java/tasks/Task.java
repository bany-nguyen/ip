package tasks;

import enums.TaskStatus;

public abstract class Task {
    /** The next ID assigned to a newly created task. */
    private static int nextId = 1;

    /** The ID of this task, unique across all task types during this run. */
    private final int id;
    private final String description;
    private boolean isDone;

    public Task(String description) {
        this(nextId, description);
    }

    /**
     * Creates a task with an existing ID when restoring it from storage.
     *
     * @param id ID previously assigned to the task
     * @param description task description
     */
    protected Task(int id, String description) {
        if (id < 1) {
            throw new IllegalArgumentException("Task ID must be positive.");
        }
        this.id = id;
        this.description = description;
        this.isDone = false;
        nextId = Math.max(nextId, id + 1);
    }

    /**
     * Returns the globally assigned ID of this task.
     *
     * @return this task's ID
     */
    public int getId() {
        return id;
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
    public abstract String getDateString();

    public boolean isDone() {
        return isDone;
    }

    @Override
    public String toString() {
        return String.format("[%s][%s] %s%s", getTypeShort(), getStatusIcon(), description, getDateString());
    }

}
