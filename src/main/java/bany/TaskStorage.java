package bany;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TaskStorage {
    private final List<Task> listOfTasks;

    public TaskStorage() {
        this.listOfTasks = new ArrayList<>(100);
    }

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
     * @param tasks tasks restored from the data file
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

    public Task getTask(int index) {
        return listOfTasks.get(index);
    }

    public Task removeTask(int index) {
        return listOfTasks.remove(index);
    }

    public int getSize() {
        return listOfTasks.size();
    }

    public List<Task> getTasks() {
        return List.copyOf(listOfTasks);
    }

    public Task markTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = getTask(taskNo);
        task.mark();
        return task;
    }

    public Task unmarkTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = getTask(taskNo);
        task.unmark();
        return task;
    }

    public Task deleteTask(int taskNo) {
        if (taskNo < 0 || taskNo >= getSize()) {
            return null;
        }
        Task task = getTask(taskNo);
        listOfTasks.remove(task);
        return task;
    }
}
