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
import java.util.Objects;

public class TaskFileRepository {

    private final Path path;
    private final ObjectMapper mapper = new ObjectMapper();

    public TaskFileRepository(Path path) {
        this.path = Objects.requireNonNull(path, "Task file path cannot be null.");
    }

    private ObjectNode toJson(Task task) {
        ObjectNode json = mapper.createObjectNode();
        // Task IDs are runtime-only and are reconstructed from file order when loading.
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
        if (tasks == null) {
            throw new IllegalArgumentException("Task list cannot be null.");
        }

        ArrayNode jsonTasks = mapper.createArrayNode();

        for (Task task : tasks) {
            if (task == null) {
                throw new IllegalArgumentException("Task list cannot contain null tasks.");
            }
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
     * Task IDs are reconstructed sequentially from the order of the saved
     * task entries; any persisted {@code id} fields are ignored.
     *
     * @return restored tasks, or an empty list if the file does not exist
     * @throws IOException if the file is malformed or cannot be read
     */
    public List<Task> load() throws IOException {
        if (Files.notExists(path)) {
            Task.resetIdAllocator(1);
            return List.of();
        }

        String content = Files.readString(path);
        if (content.isBlank()) {
            Task.resetIdAllocator(1);
            return List.of();
        }

        JsonNode jsonTasks = mapper.readTree(content);
        if (!jsonTasks.isArray()) {
            throw new IOException("Task file must contain a JSON array.");
        }

        List<Task> tasks = new ArrayList<>();
        long reconstructedId = 1;
        for (JsonNode json : jsonTasks) {
            if (reconstructedId > Integer.MAX_VALUE) {
                throw new IOException("The task file contains too many tasks.");
            }
            tasks.add(fromJson(json, (int) reconstructedId));
            reconstructedId++;
        }
        Task.resetIdAllocator(reconstructedId);
        return tasks;
    }

    private Task fromJson(JsonNode json, int reconstructedId) throws IOException {
        if (json == null || !json.isObject()) {
            throw new IOException("Each saved task must be a JSON object.");
        }

        if (!json.hasNonNull("type") || !json.get("type").isTextual()
                || !json.hasNonNull("description") || !json.get("description").isTextual()) {
            throw new IOException("Task is missing its type or description.");
        }

        String type = json.get("type").asText();
        String description = json.get("description").asText();
        if (type.isBlank()) {
            throw new IOException("Task type cannot be blank.");
        }
        if (description.isBlank()) {
            throw new IOException("Task description cannot be blank.");
        }
        if (json.has("done") && !json.get("done").isBoolean()) {
            throw new IOException("Task done field must be a boolean.");
        }

        Task task;
        switch (type) {
            case "TODO":
                try {
                    task = new ToDo(description, reconstructedId);
                } catch (IllegalArgumentException e) {
                    throw new IOException("To-do contains invalid task data.", e);
                }
                break;

            case "DEADLINE":
                if (!json.hasNonNull("by")) {
                    throw new IOException("Deadline is missing its by field.");
                }
                try {
                    task = new Deadline(description, json.get("by").asText(), reconstructedId);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Deadline contains an invalid date-time.", e);
                }
                break;

            case "EVENT":
                if (!json.hasNonNull("from") || !json.hasNonNull("to")) {
                    throw new IOException("Event is missing its from or to field.");
                }
                try {
                    task = new Event(description, json.get("from").asText(),
                            json.get("to").asText(), reconstructedId);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Event contains an invalid date-time.", e);
                }
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
