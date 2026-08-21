package enums;

public enum TaskType {
    TODO("T", "TODO"),
    DEADLINE("D", "DEADLINE"),
    EVENT("E", "EVENT"),
    TASK("", "TASK");

    private final String typeShort;
    private final String type;

    TaskType(String typeShort, String type) {
        this.typeShort = typeShort;
        this.type = type;
    }

    public String getTypeShort() {
        return typeShort;
    }

    public String getType() {
        return type;
    }

}
