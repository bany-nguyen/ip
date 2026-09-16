package bany.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bany.tasks.Task;
import bany.tasks.ToDo;

/** Tests that {@link Responder} builds GUI-ready strings without printing. */
class ResponderTest {
    private Responder responder;
    private Task task;

    /** Creates a responder and a reusable task before each test. */
    @BeforeEach
    void setUp() {
        responder = new Responder();
        task = new ToDo("Read book", 1);
    }

    @Test
    void simpleResponses_returnExpectedText() {
        assertEquals("Hi, I'm Bany.\nLet's keep your tasks growing.\n", responder.respondWelcome());
        assertEquals("Until next time - your task list will be here when you're ready.",
                responder.respondGoodbye());
        assertEquals("I need a task description before I can add it.",
                responder.respondInvalidTaskDescription());
        assertEquals("I couldn't schedule that task because its required tags are missing or invalid.",
                responder.respondInvalidTaskInitiation());
        assertEquals("Use the date-time format dd-MM-yyyy HH:mm.",
                responder.respondInvalidDateTime());
        assertEquals("An event must end after it begins.",
                responder.respondInvalidEventRange());
        assertEquals("I didn't recognize that command. Please try again.", responder.respondInvalidCommand());
        assertEquals("No tasks on your list match that search.",
                responder.respondNoSuchTask());
        assertEquals("I couldn't save your task list.",
                Responder.ErrorResponder.respondFileUpdateError());
        assertEquals("I couldn't load your task list.",
                Responder.ErrorResponder.respondFileLoadError());
        assertEquals("I couldn't read the startup file. I'll begin with an empty task list. "
                        + "The original file is available at data/report.",
                Responder.ErrorResponder.respondStartupFileWarning());
        assertTrue(Responder.ErrorResponder.respondStartupRecoveryError().contains("Commands are disabled"));
    }

    @Test
    void taskResponses_includeTaskDetailsAndCounts() {
        assertEquals("Added to your task list:" + System.lineSeparator()
                        + "  [T][  ] Read book" + System.lineSeparator()
                        + "Your list now holds 1 tasks.", responder.respondAddTask(task, 1));
        assertEquals("Checked off:" + System.lineSeparator()
                        + "   [T][  ] Read book",
                responder.respondMarkTask(task));
        assertEquals("Moved back to active tasks:" + System.lineSeparator()
                        + "   [T][  ] Read book",
                responder.respondUnmarkTask(task));
        assertEquals("Removed from your task list:" + System.lineSeparator()
                        + "   [T][  ] Read book" + System.lineSeparator()
                        + "Your list now holds 0 tasks.", responder.respondDeleteTask(task, 0));
        assertEquals("Schedule refreshed for this task:" + System.lineSeparator()
                        + "   [T][  ] Read book", responder.respondRescheduleTask(task));
    }

    @Test
    void listResponses_numberTasksInOrder() {
        Task secondTask = new ToDo("Submit assignment", 2);

        assertEquals("Your task list:" + System.lineSeparator()
                        + "1. [T][  ] Read book" + System.lineSeparator()
                        + "2. [T][  ] Submit assignment",
                responder.respondList(List.of(task, secondTask)));
        assertEquals("Tasks matching your search:" + System.lineSeparator()
                        + "1.[T][  ] Read book",
                responder.respondMatchedTasks(List.of(task)));
    }

    @Test
    void parameterisedResponses_includeArguments() {
        assertEquals("Use the tag /by only once.", responder.respondDuplicateTag("by"));
        assertEquals("I can't delete that task. Choose a valid task number from 1 to 2.",
                responder.respondOutOfBoundIndex("delete", 2));
        assertEquals("I found extra or out-of-order tags. The task was still added.",
                responder.respondTagWarning());
        assertEquals("I found extra or out-of-order tags. The task was still rescheduled.",
                responder.respondRescheduleTagWarning());
        assertEquals("I didn't recognize that command. Did you mean LIST?",
                responder.respondInvalidCommand("LIST"));
        assertEquals("I didn't recognize that command. Please try again.", responder.respondInvalidCommand(null));
    }
}
