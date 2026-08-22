import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import errors.InvalidTaskType;
import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.ToDo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskFileRepository {

    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();

    public TaskFileRepository(Path path) {
        this.path = path;
    }

    private ObjectNode toJson(Task task) {
        ObjectNode json = mapper.createObjectNode();
        json.put("id", task.getId());
        json.put("type", task.getType());
        json.put("description", task.getDescription());
        json.put("done", task.isDone());
        switch (task.getType()) {
            case "TODO": {
                break;
            }

            case "DEADLINE": {
                Deadline deadline = (Deadline) task;
                json.put("by", deadline.getBy());
                break;
            }

            case "EVENT": {
                Event event = (Event) task;
                json.put("from", event.getFrom());
                json.put("to", event.getTo());
                break;
            }

            default: {
                throw new InvalidTaskType("Unknown task type!");
            }

        }

        return json;
    }

    /**
     * Saves the complete current task list as one JSON array.
     *
     * @param tasks tasks that should be persisted
     * @throws IOException if the file cannot be created or written
     */
    public void save(List<Task> tasks) throws IOException {
        ArrayNode jsonTasks = mapper.createArrayNode();

        for (Task task : tasks) {
            jsonTasks.add(toJson(task));
        }

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, mapper.writeValueAsString(jsonTasks));
    }

    /**
     * Loads all tasks from the JSON file.
     *
     * @return restored tasks, or an empty list if the file does not exist
     * @throws IOException if the file is malformed or cannot be read
     */
    public List<Task> load() throws IOException {
        if (Files.notExists(path)) {
            return List.of();
        }

        String content = Files.readString(path);
        if (content.isBlank()) {
            return List.of();
        }

        JsonNode jsonTasks = mapper.readTree(content);
        if (!jsonTasks.isArray()) {
            throw new IOException("Task file must contain a JSON array.");
        }

        List<Task> tasks = new ArrayList<>();
        for (JsonNode json : jsonTasks) {
            tasks.add(fromJson(json));
        }
        return tasks;
    }

    private Task fromJson(JsonNode json) throws IOException {
        if (!json.hasNonNull("type") || !json.hasNonNull("description")) {
            throw new IOException("Task is missing its type or description.");
        }

        String type = json.get("type").asText();
        String description = json.get("description").asText();
        int id = json.path("id").asInt(0);

        Task task;
        switch (type) {
            case "TODO":
                task = id > 0 ? new ToDo(description, id) : new ToDo(description);
                break;

            case "DEADLINE":
                if (!json.hasNonNull("by")) {
                    throw new IOException("Deadline is missing its by field.");
                }
                task = id > 0
                        ? new Deadline(description, json.get("by").asText(), id)
                        : new Deadline(description, json.get("by").asText());
                break;

            case "EVENT":
                if (!json.hasNonNull("from") || !json.hasNonNull("to")) {
                    throw new IOException("Event is missing its from or to field.");
                }
                task = id > 0
                        ? new Event(description, json.get("from").asText(), json.get("to").asText(), id)
                        : new Event(description, json.get("from").asText(), json.get("to").asText());
                break;

            default:
                throw new InvalidTaskType("Unknown task type: " + type);
        }

        if (json.path("done").asBoolean(false)) {
            task.mark();
        }
        return task;
    }
}
