public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public int mark() {
        if (isDone) {
            return 1;
        }
        isDone = true;
        return 0;
    }

    public int unmark() {
        if (!isDone) {
            return 1;
        }
        isDone = false;
        return 0;
    }

    public String getDescription() {
        return description;
    }
    //...
}
