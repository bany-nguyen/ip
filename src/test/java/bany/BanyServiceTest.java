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
import bany.tasks.Deadline;
import bany.tasks.Event;
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

    @Test
    void executeCommand_rescheduleDeadline_updatesByTag() {
        banyService.executeCommand("deadline submit report /by 21-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /by 22-02-2026 21:03");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("rescheduled")));
        Deadline deadline = (Deadline) taskStorage.getTask(0).orElseThrow();
        assertEquals("22-02-2026 21:03", deadline.getBy());
    }

    @Test
    void executeCommand_rescheduleEventWithMisorderedCriticalTags_updatesWithWarning() {
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /to 24-02-2026 21:03 /from 23-02-2026 21:03");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Warning:")));
        Event event = (Event) taskStorage.getTask(0).orElse(null);
        assertEquals("23-02-2026 21:03", event.getFrom());
        assertEquals("24-02-2026 21:03", event.getTo());
    }

    @Test
    void executeCommand_rescheduleWithNonCriticalTag_updatesWithWarning() {
        banyService.executeCommand("todo read book");

        CommandResult result = banyService.executeCommand("reschedule 1 /note urgent");

        assertTrue(result.messages().stream()
                .anyMatch(message -> message.text().contains("Warning:")));
        assertEquals("urgent", taskStorage.getTask(0).get().getTag("note")
                .orElseThrow().value().orElseThrow());
    }

    @Test
    void executeCommand_rescheduleDuplicateCriticalTag_rejectsWithoutChangingTask() {
        banyService.executeCommand("deadline submit report /by 21-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /by 22-02-2026 21:03 /BY 23-02-2026 21:03");

        assertEquals("The tag /by can only be used once!", result.messages().get(0).text());
        assertEquals("21-02-2026 21:03", ((Deadline) taskStorage.getTask(0).get()).getBy());
    }

    @Test
    void executeCommand_rescheduleInvalidEventRange_rejectsWithoutChangingTask() {
        banyService.executeCommand(
                "event meeting /from 21-02-2026 21:03 /to 22-02-2026 21:03");

        CommandResult result = banyService.executeCommand(
                "reschedule 1 /from 24-02-2026 21:03 /to 23-02-2026 21:03");

        assertEquals("An event's end date-time cannot be before its start date-time!",
                result.messages().get(0).text());
        Event event = (Event) taskStorage.getTask(0).get();
        assertEquals("21-02-2026 21:03", event.getFrom());
        assertEquals("22-02-2026 21:03", event.getTo());
    }

    @Test
    void executeCommand_rescheduleSaveFailure_rollsBackTagUpdate() {
        TaskStorage failingStorage = new TaskStorage();
        Event event = new Event("Meeting", "21-02-2026 21:03",
                "22-02-2026 21:03", 1);
        failingStorage.addTask(event);
        BanyService failingService = new BanyService(
                new TaskFileRepository(temporaryDirectory),
                failingStorage,
                new CommandParser(new CommandValidator()),
                new Responder());

        CommandResult result = failingService.executeCommand(
                "reschedule 1 /from 20-02-2026 21:03");

        assertEquals("Error writing task to history file!", result.messages().get(0).text());
        assertEquals("21-02-2026 21:03", event.getFrom());
    }
}
