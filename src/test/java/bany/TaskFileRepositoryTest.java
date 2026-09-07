package bany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bany.tags.Tag;
import bany.tasks.Deadline;
import bany.tasks.Task;
import bany.tasks.ToDo;

/** Tests loading and saving directly between {@link TaskFileRepository} and {@link TaskStorage}. */
class TaskFileRepositoryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveThenLoad_tasksAreRestoredIntoStorage() throws IOException {
        TaskStorage savedStorage = new TaskStorage();
        Task todo = new ToDo("Read book", 1);
        Task deadline = new Deadline("Submit report", "21-03-2026 21:03", 2);
        deadline.mark();
        savedStorage.addTask(todo);
        savedStorage.addTask(deadline);
        TaskFileRepository repository = new TaskFileRepository(
                temporaryDirectory.resolve("bany.txt"));

        repository.save(savedStorage);

        TaskStorage loadedStorage = new TaskStorage();
        repository.load(loadedStorage);

        assertEquals(2, loadedStorage.getSize());
        assertEquals("Read book", loadedStorage.getTask(0).getDescription());
        assertEquals("Submit report", loadedStorage.getTask(1).getDescription());
        assertTrue(loadedStorage.getTask(1).isDone());
    }

    @Test
    void load_missingFile_replacesStorageWithNoTasks() throws IOException {
        TaskStorage taskStorage = new TaskStorage();
        taskStorage.addTask(new ToDo("Old task", 1));
        TaskFileRepository repository = new TaskFileRepository(
                temporaryDirectory.resolve("missing.txt"));

        repository.load(taskStorage);

        assertEquals(0, taskStorage.getSize());
        assertFalse(temporaryDirectory.resolve("missing.txt").toFile().exists());
    }

    @Test
    void load_existingFile_reconstructsTasksInFileOrder() throws IOException {
        Path taskFile = temporaryDirectory.resolve("bany.txt");
        Files.writeString(taskFile, """
                [{"id":99,"type":"TODO","description":"Read book","done":false},
                 {"id":42,"type":"DEADLINE","description":"Submit report","done":true,
                 "by":"21-03-2026 21:03"}]
                """);
        TaskStorage taskStorage = new TaskStorage();
        TaskFileRepository repository = new TaskFileRepository(taskFile);

        repository.load(taskStorage);

        assertEquals(2, taskStorage.getSize());
        assertEquals(1, taskStorage.getTask(0).getId());
        assertEquals("Read book", taskStorage.getTask(0).getDescription());
        assertEquals(2, taskStorage.getTask(1).getId());
        assertEquals("Submit report", taskStorage.getTask(1).getDescription());
        assertTrue(taskStorage.getTask(1).isDone());
    }

    @Test
    void saveThenLoad_preservesGenericTags() throws IOException {
        TaskStorage savedStorage = new TaskStorage();
        Task todo = new ToDo("Read book", 1);
        todo.updateTags(List.of(new Tag("urgent"), new Tag("priority", "high")));
        savedStorage.addTask(todo);
        TaskFileRepository repository = new TaskFileRepository(
                temporaryDirectory.resolve("bany.txt"));

        repository.save(savedStorage);

        TaskStorage loadedStorage = new TaskStorage();
        repository.load(loadedStorage);

        assertEquals(todo.getTags(), loadedStorage.getTask(0).getTags());
    }
}
