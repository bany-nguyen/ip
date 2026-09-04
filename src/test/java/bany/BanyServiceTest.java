package bany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bany.commands.CommandParser;
import bany.commands.CommandResult;
import bany.gui.Responder;
import bany.utilities.CommandValidator;

/** Tests command outcomes returned through {@link BanyService}. */
class BanyServiceTest {
    private BanyService banyService;
    private TaskStorage taskStorage;
    @TempDir
    private Path temporaryDirectory;

    /** Creates an isolated service before each test. */
    @BeforeEach
    void setUp() {
        taskStorage = new TaskStorage();
        banyService = new BanyService(
                new TaskFileRepository(temporaryDirectory.resolve("bany.txt")),
                taskStorage,
                new CommandParser(new CommandValidator()),
                new Responder());
    }

    @Test
    void executeCommand_todoWithoutDescription_returnsDescriptionError() {
        CommandResult result = banyService.executeCommand("todo");

        assertEquals("Task description cannot be blank!", result.messages().get(0).text());
        assertFalse(result.shouldExit());
        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_deadlineWithWrongTag_returnsTagErrorWithoutCreatingTask() {
        CommandResult result = banyService.executeCommand(
                "deadline run /from 21-02-2026 21:03");

        assertEquals("Your tags for the task do not match the requirements!",
                result.messages().get(0).text());
        assertFalse(result.shouldExit());
        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_validTaskCommands_createTasks() {
        banyService.executeCommand("todo read book");
        banyService.executeCommand("deadline submit report /by 21-02-2026 21:03");
        banyService.executeCommand("event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        assertEquals(3, taskStorage.getSize());
    }

    @Test
    void executeCommand_eventCriticalTagsInAnyOrder_createsTask() {
        CommandResult result = banyService.executeCommand(
                "event meeting /to 22-02-2026 21:03 /from 21-02-2026 21:03");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Got it. I've added this task:")));
        assertEquals(1, taskStorage.getSize());
    }

    @Test
    void executeCommand_blankDescriptions_doNotCreateTasks() {
        banyService.executeCommand("todo");
        banyService.executeCommand("deadline /by 21-02-2026 21:03");
        banyService.executeCommand("event /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_missingCriticalTags_doNotCreateTasks() {
        banyService.executeCommand("deadline submit report");
        banyService.executeCommand("event meeting /from 21-02-2026 21:03");
        banyService.executeCommand("event meeting /to 22-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_invalidCriticalTagValues_doNotCreateTasks() {
        banyService.executeCommand("deadline submit report /by 31-02-2026 21:03");
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 20-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_duplicateCriticalTags_doNotCreateTasks() {
        banyService.executeCommand(
                "deadline submit report /by 21-02-2026 21:03 /by 22-02-2026 21:03");
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /from 22-02-2026 21:03"
                        + " /to 23-02-2026 21:03");
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03"
                        + " /to 23-02-2026 21:03");

        assertEquals(0, taskStorage.getSize());
    }

    @Test
    void executeCommand_extraOrDuplicateNonCriticalTags_warnsAndCreatesTask() {
        CommandResult result = banyService.executeCommand("todo read book /open now /open later");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Warning: the command contains extra tags")));
        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Got it. I've added this task:")));
        assertEquals(1, taskStorage.getSize());
    }

    @Test
    void executeCommand_invalidCommand_doesNotCreateTask() {
        banyService.executeCommand("reminder read book");

        assertEquals(0, taskStorage.getSize());
    }
}
