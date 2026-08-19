
public class Deadline extends Task {
    private final String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    public String getDateInfo() {
        return " (by: " + by + ")";
    }

    public String getType() {
        return "D";
    }

}
