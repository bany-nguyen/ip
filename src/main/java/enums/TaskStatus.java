package enums;

public enum TaskStatus {
    DONE("X"), NOT_DONE("");

    private String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
