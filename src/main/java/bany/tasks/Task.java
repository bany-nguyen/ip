package bany.tasks;

import java.util.Objects;

/** Represents a task with a unique ID, description, type, and completion state. */
public abstract class Task {
    /** The next ID available for a newly created task. */
    private static long nextId = 1;

    /** The ID of this task, unique across all task types during this run. */
    private final int id;
    /** Text describing the work represented by this task. */
    private final String description;
    /** Whether this task has been marked as completed. */
    private boolean isDone;

    /**
     * Creates a task with an explicit ID.
     *
     * @param id ID previously assigned to the task.
     * @param description task description.
     */
    protected Task(int id, String description) {
        if (id < 1) {
            throw new IllegalArgumentException("Task ID must be positive.");
        }
        Objects.requireNonNull(description, "Task description cannot be null.");
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank.");
        }
        this.id = id;
        this.description = description;
        this.isDone = false;
        nextId = Math.max(nextId, (long) id + 1);
    }

    /**
     * Allocates the next globally unique task ID for a newly created task.
     *
     * @return a positive task ID.
     * @throws IllegalStateException if all positive integer IDs are exhausted.
     */
    public static synchronized int allocateId() {
        if (nextId > Integer.MAX_VALUE) {
            throw new IllegalStateException("No task IDs are available.");
        }
        return (int) nextId++;
    }

    /**
     * Resets the ID allocator after rebuilding a task list from storage.
     *
     * @param nextIdForNewTask ID that should be assigned to the next new task.
     */
    public static synchronized void resetIdAllocator(long nextIdForNewTask) {
        if (nextIdForNewTask < 1) {
            throw new IllegalArgumentException("Next task ID must be positive.");
        }
        nextId = nextIdForNewTask;
    }

    /**
     * Returns the globally assigned ID of this task.
     *
     * @return this task's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the completion marker used in task-list output.
     *
     * @return the completed or incomplete status marker.
     */
    public String getStatusIcon() {
        return (isDone ? TaskStatus.DONE.getStatus()
                : TaskStatus.NOT_DONE.getStatus()); // mark done task with X
    }

    /** Marks this task as completed. */
    public void mark() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the full name of this task's type.
     *
     * @return full task type.
     */
    public abstract String getType();

    /**
     * Returns the abbreviated type marker used in task-list output.
     *
     * @return abbreviated task type.
     */
    public abstract String getTypeShort();

    /**
     * Returns this task's date information, including its leading space when present.
     *
     * @return task date information, or an empty string for a ToDo.
     */
    public abstract String getDateString();

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if the task is marked done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Formats this task for display in the task list.
     *
     * @return formatted task text.
     */
    @Override
    public String toString() {
        return String.format("[%s][%s] %s%s", getTypeShort(), getStatusIcon(), description, getDateString());
    }

}
