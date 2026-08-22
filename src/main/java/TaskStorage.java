import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private final List<Task> listOfTasks;

    public TaskStorage() {
        this.listOfTasks = new ArrayList<>(100);
    }

    public void addTask(Task task) {
        listOfTasks.add(task);
    }

    /**
     * Replaces the current list with tasks loaded from storage.
     *
     * @param tasks tasks restored from the data file
     */
    public void replaceTasks(List<Task> tasks) {
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
