package bany;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import bany.exceptions.InvalidTaskType;
import bany.tasks.Deadline;
import bany.tasks.Event;
import bany.tasks.Task;
import bany.tasks.ToDo;

/** Persists and reconstructs Bany tasks in a JSON file. */
public class TaskFileRepository {

    /** File used to store the task list. */
    private final Path path;
    /** JSON mapper used to convert task data to and from tree nodes. */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a repository for a task file.
     *
     * @param path file used for persistence.
     * @throws NullPointerException if {@code path} is null.
     */
    public TaskFileRepository(Path path) {
        this.path = Objects.requireNonNull(path, "Task file path cannot be null.");
    }

    /**
     * Converts one task into the JSON representation used by the repository.
     *
     * @param task task to convert.
     * @return JSON object containing the task data.
     * @throws InvalidTaskType if the task type is unsupported.
     */
    private ObjectNode toJson(Task task) {
        ObjectNode json = mapper.createObjectNode();
        // Task IDs are runtime-only and are reconstructed from file order when loading.
        json.put("type", task.getType());
        json.put("description", task.getDescription());
        json.put("done", task.isDone());
        switch (task.getType()) {
            case "TODO":
                break;
            case "DEADLINE":
                Deadline deadline = (Deadline) task;
                json.put("by", deadline.getBy());
                break;
            case "EVENT":
                Event event = (Event) task;
                json.put("from", event.getFrom());
                json.put("to", event.getTo());
                break;
            default:
                throw new InvalidTaskType("Unknown task type!");
        }

        return json;
    }

    /**
     * Saves every task currently held by the supplied task storage as one JSON array.
     *
     * @param taskStorage storage whose tasks should be persisted.
     * @throws IOException if the file cannot be created or written.
     * @throws NullPointerException if {@code taskStorage} is null.
     */
    public void save(TaskStorage taskStorage) throws IOException {
        TaskStorage nonNullTaskStorage = Objects.requireNonNull(
                taskStorage, "Task storage cannot be null.");
        List<Task> tasks = nonNullTaskStorage.getTasks();

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
     * Loads all tasks from the JSON file directly into the supplied task storage.
     * Task IDs are reconstructed sequentially from the order of the saved
     * task entries; any persisted {@code id} fields are ignored.
     *
     * @param taskStorage storage to replace with the restored tasks.
     * @throws IOException if the file is malformed or cannot be read.
     * @throws NullPointerException if {@code taskStorage} is null.
     */
    public void load(TaskStorage taskStorage) throws IOException {
        TaskStorage nonNullTaskStorage = Objects.requireNonNull(
                taskStorage, "Task storage cannot be null.");
        if (Files.notExists(path)) {
            nonNullTaskStorage.replaceTasks(List.of());
            Task.resetIdAllocator(1);
            return;
        }

        String content = Files.readString(path);
        if (content.isBlank()) {
            nonNullTaskStorage.replaceTasks(List.of());
            Task.resetIdAllocator(1);
            return;
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
        nonNullTaskStorage.replaceTasks(tasks);
    }

    /**
     * Reconstructs one task from its saved JSON representation.
     *
     * @param json saved task object.
     * @param reconstructedId runtime ID assigned according to file order.
     * @return reconstructed task.
     * @throws IOException if the object is malformed or contains invalid data.
     * @throws InvalidTaskType if the saved type is unsupported.
     */
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
