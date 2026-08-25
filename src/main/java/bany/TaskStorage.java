package bany;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import bany.tasks.Task;

/** Maintains the in-memory ordered list of tasks used by Bany. */
public class TaskStorage {
    /** Tasks in the order in which they are displayed and indexed. */
    private final List<Task> listOfTasks;

    /** Creates an empty task list. */
    public TaskStorage() {
        this.listOfTasks = new ArrayList<>(100);
    }

    /**
     * Adds a task while enforcing non-null and unique task IDs.
     *
     * @param task task to add.
     * @throws NullPointerException if {@code task} is null.
     * @throws IllegalArgumentException if another task has the same ID.
     */
    public void addTask(Task task) {
        Task nonNullTask = Objects.requireNonNull(task, "Task cannot be null.");
        if (listOfTasks.stream().anyMatch(existing -> existing.getId() == nonNullTask.getId())) {
            throw new IllegalArgumentException("Task ID must be unique.");
        }
        listOfTasks.add(nonNullTask);
    }

    /**
     * Replaces the current list with tasks loaded from storage.
     *
     * @param tasks tasks restored from the data file.
     */
    public void replaceTasks(List<Task> tasks) {
        Objects.requireNonNull(tasks, "Task list cannot be null.");
        Set<Integer> ids = new HashSet<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new IllegalArgumentException("Task list cannot contain null tasks.");
            }
            if (!ids.add(task.getId())) {
                throw new IllegalArgumentException("Task IDs must be unique.");
            }
        }
        listOfTasks.clear();
        listOfTasks.addAll(tasks);
    }

    /**
     * Returns the task at a zero-based list index.
     *
     * @param index zero-based task index.
     * @return task at the requested index.
     * @throws IndexOutOfBoundsException if the index is outside the list.
     */
    public Task getTask(int index) {
        return listOfTasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based list index.
     *
     * @param index zero-based task index.
     * @return removed task.
     * @throws IndexOutOfBoundsException if the index is outside the list.
     */
    public Task removeTask(int index) {
        return listOfTasks.remove(index);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return current task count.
     */
    public int getSize() {
        return listOfTasks.size();
    }

    /**
     * Returns an immutable snapshot of the stored tasks.
     *
     * @return tasks in display order.
     */
    public List<Task> getTasks() {
        return List.copyOf(listOfTasks);
    }

    /**
     * Marks a task at a zero-based index as done.
     *
     * @param taskNo zero-based task index.
     * @return the marked task, or {@code null} if the index is invalid.
     */
    public Task markTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = getTask(taskNo);
        task.mark();
        return task;
    }

    /**
     * Marks a task at a zero-based index as not done.
     *
     * @param taskNo zero-based task index.
     * @return the unmarked task, or {@code null} if the index is invalid.
     */
    public Task unmarkTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = getTask(taskNo);
        task.unmark();
        return task;
    }

    /**
     * Deletes a task at a zero-based index.
     *
     * @param taskNo zero-based task index.
     * @return the deleted task, or {@code null} if the index is invalid.
     */
    public Task deleteTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = getTask(taskNo);
        listOfTasks.remove(task);
        return task;
    }
}
