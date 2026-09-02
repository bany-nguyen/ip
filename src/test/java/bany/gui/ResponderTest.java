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
        assertTrue(responder.respondWelcome().contains("Hello! I'm Bany."));
        assertEquals("Bye. Hope to see you again soon!", responder.respondGoodbye());
        assertEquals("Task description cannot be blank!",
                responder.respondInvalidTaskDescription());
        assertEquals("Your tags for the task do not match the requirements!",
                responder.respondInvalidTaskInitiation());
        assertEquals("Date-time must use the format dd-MM-yyyy HH:mm!",
                responder.respondInvalidDateTime());
        assertEquals("An event's end date-time cannot be before its start date-time!",
                responder.respondInvalidEventRange());
        assertEquals("Invalid command. Try again.", responder.respondInvalidCommand());
        assertEquals("Your query does not match any task in the current task list!",
                responder.respondNoSuchTask());
        assertEquals("Error writing task to history file!",
                Responder.ErrorResponder.respondFileUpdateError());
        assertEquals("Error loading tasks from the history file!",
                Responder.ErrorResponder.respondFileLoadError());
    }

    @Test
    void taskResponses_includeTaskDetailsAndCounts() {
        assertEquals("Got it. I've added this task:" + System.lineSeparator()
                        + "  [T][] Read book" + System.lineSeparator()
                        + "Now you have 1 tasks in the list.", responder.respondAddTask(task, 1));
        assertEquals("Nice! I've marked this task as done:" + System.lineSeparator()
                        + "   [T][] Read book",
                responder.respondMarkTask(task));
        assertEquals("Nice! I've unmarked this task as done:" + System.lineSeparator()
                        + "   [T][] Read book",
                responder.respondUnmarkTask(task));
        assertEquals("Noted. I've removed this task:" + System.lineSeparator()
                        + "   [T][] Read book" + System.lineSeparator()
                        + "Now you have 0 tasks in the list.", responder.respondDeleteTask(task, 0));
    }

    @Test
    void listResponses_numberTasksInOrder() {
        Task secondTask = new ToDo("Submit assignment", 2);

        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                        + "1.[T][] Read book" + System.lineSeparator() + "2.[T][] Submit assignment",
                responder.respondList(List.of(task, secondTask)));
        assertEquals("Here are the matching tasks in your list:" + System.lineSeparator()
                        + "1.[T][] Read book",
                responder.respondMatchedTasks(List.of(task)));
    }

    @Test
    void parameterisedResponses_includeArguments() {
        assertEquals("The tag /by can only be used once!", responder.respondDuplicateTag("by"));
        assertEquals("The task that you are trying to delete is not here!"
                        + " Please enter an integer between 1 and 2",
                responder.respondOutOfBoundIndex("delete", 2));
        assertEquals("Warning: the command contains extra tags or tags in an unexpected order.\n"
                        + "The task will still be added.", responder.respondTagWarning());
        assertEquals("Command not found. Do you mean LIST?",
                responder.respondInvalidCommand("LIST"));
        assertEquals("Invalid command. Try again.", responder.respondInvalidCommand(null));
    }
}
