package tasks;

import enums.TaskType;

public class ToDo extends Task {
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String getType() {
        return TaskType.TODO.getType();
    }

    @Override
    public String getTypeShort() {
        return TaskType.TODO.getTypeShort();
    }

    public String getDateInfo() {
        return "";
    }
}
