public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from,  String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public String getDateInfo() {
        return " (from: " + from + " to: " + to + ")";
    }

    public String getType() {
        return "E";
    }

}
