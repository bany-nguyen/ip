package bany;

import java.util.List;
import java.util.Scanner;

import bany.tasks.Task;
import bany.utilities.Helper;

/** Handles all user-facing input and output for Bany. */
public class Ui {

    /** Reads commands from the configured input stream. */
    private final Scanner scanner;

    /**
     * Creates a user interface backed by the given input scanner.
     *
     * @param scanner source from which user commands are read.
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Displays Bany's welcome message. */
    public void showWelcome() {
        String banner = """
                ____________________________________________________________
                 ____
                | __ )  __ _ _ __  _   _
                |  _ \\ / _` | '_ \\| | | |
                | |_) | (_| | | | | |_| |
                |____/ \\__,_|_| |_|\\__, |
                                   |___/
                Hello! I'm Bany.
                What can I do for you?
                ____________________________________________________________
                """;
        System.out.println(banner);
    }

    /** Displays Bany's goodbye message. */
    public void showGoodbye() {
        Helper.printDivider();
        System.out.println("Bye. Hope to see you again soon!");
        Helper.printDivider();
    }

    /**
     * Displays confirmation after a task is added.
     *
     * @param task task that was added.
     * @param size number of tasks currently stored.
     */
    public void showAddTask(Task task, int size) {
        Helper.printDivider();
        System.out.println("Got it. I've added this task:");
        System.out.printf("  %s%n", task);
        System.out.printf("Now you have %d tasks in the list.%n", size);
        Helper.printDivider();
    }

    /** Displays an error for a blank task description. */
    public void showInvalidTaskDescription() {
        Helper.printDivider();
        System.out.println("Task description cannot be blank!");
        Helper.printDivider();
    }

    /** Displays an error when required task tags are missing. */
    public void showInvalidTaskInitiation() {
        Helper.printDivider();
        System.out.println("Your tags for the task do not match the requirements!");
        Helper.printDivider();
    }

    /** Displays an error when a date-time has an unsupported format. */
    public void showInvalidDateTime() {
        Helper.printDivider();
        System.out.println("Date-time must use the format dd-MM-yyyy HH:mm!");
        Helper.printDivider();
    }

    /** Displays an error when an event ends before it starts. */
    public void showInvalidEventRange() {
        Helper.printDivider();
        System.out.println("An event's end date-time cannot be before its start date-time!");
        Helper.printDivider();
    }

    /**
     * Displays an error for a repeated critical tag.
     *
     * @param tagName repeated tag name.
     */
    public void showDuplicateTag(String tagName) {
        Helper.printDivider();
        System.out.printf("The tag /%s can only be used once!%n", tagName);
        Helper.printDivider();
    }

    /** Displays a warning when task tags are extra or out of order. */
    public void showTagWarning() {
        Helper.printDivider();
        System.out.println("Warning: the command contains extra tags or tags in an unexpected order.\n"
                + "The task will still be added.");
    }

    /** Displays an error when a task cannot be created. */
    public void showTaskCreationFail() {
        Helper.printDivider();
        System.out.println("Task creation failed! Please check your command.");
        Helper.printDivider();
    }

    /**
     * Displays an error for a task index outside the current task list.
     *
     * @param type action that was attempted.
     */
    public void showOutOfBoundIndex(String type) {
        Helper.printDivider();
        System.out.printf("The task that you are trying to %s is not here!%n", type);
        Helper.printDivider();
    }

    /**
     * Displays confirmation after a task is marked as done.
     *
     * @param task task that was marked.
     */
    public void showMarkTask(Task task) {
        Helper.printDivider();
        System.out.println("Nice! I've marked this task as done:");
        System.out.printf("   %s%n", task);
        Helper.printDivider();
    }

    /**
     * Displays confirmation after a task is marked as not done.
     *
     * @param task task that was unmarked.
     */
    public void showUnmarkTask(Task task) {
        Helper.printDivider();
        System.out.println("Nice! I've unmarked this task as done:");
        System.out.printf("   %s%n", task);
        Helper.printDivider();
    }

    /**
     * Displays confirmation after a task is deleted.
     *
     * @param task task that was deleted.
     * @param size number of tasks remaining.
     */
    public void showDeleteTask(Task task, int size) {
        Helper.printDivider();
        System.out.println("Noted. I've removed this task:");
        System.out.printf("   %s%n", task);
        System.out.printf("Now you have %d tasks in the list.%n", size);
        Helper.printDivider();
    }

    /**
     * Displays all tasks in their current storage order.
     *
     * @param listOfTasks tasks to display.
     */
    public void showList(List<Task> listOfTasks) {
        Helper.printDivider();
        int count = 1;
        System.out.println("Here are the tasks in your list:");
        for (Task task : listOfTasks) {
            System.out.printf("%d.%s%n", count++, task);
        }
        Helper.printDivider();
    }
    /** Displays a generic invalid-command message. */
    public void showInvalidCommand() {
        Helper.printDivider();
        System.out.println("Invalid command. Try again.");
        Helper.printDivider();
    }

    /**
     * Displays an invalid-command message with an optional suggestion.
     *
     * @param closestCommand closest recognized command, or {@code null}.
     */
    public void showInvalidCommand(String closestCommand) {
        if (closestCommand == null) {
            showInvalidCommand();
            return;
        }
        Helper.printDivider();
        System.out.printf("Command not found. Do you mean %s?%n", closestCommand);
        Helper.printDivider();
    }

    /**
     * Reads the next command from the input stream.
     *
     * @return the next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Checks whether another command is available.
     *
     * @return {@code true} if another input line exists.
     */
    public Boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Displays a message when a search matches no tasks. */
    public void showNoSuchTask() {
        Helper.printDivider();
        System.out.println("Your query does not match any task in the current task list!");
        Helper.printDivider();
    }

    /**
     * Displays tasks matching a search query.
     *
     * @param matchedTasks tasks that matched the query.
     */
    public void showMatchedTasks(List<Task> matchedTasks) {
        Helper.printDivider();
        System.out.println("Here are the matching tasks in your list:");
        int count = 1;
        for (Task task : matchedTasks) {
            System.out.printf("%d.%s%n", count++, task);
        }
        Helper.printDivider();
    }

    /** Displays errors related to task-file persistence. */
    public static class ErrorUi {
        /** Creates the persistence-error UI helper. */
        public ErrorUi() {
        }

        /** Displays an error when the task file cannot be updated. */
        public static void showFileUpdateError() {
            Helper.printDivider();
            System.out.println("Error writing task to history file!");
            Helper.printDivider();
        }

        /** Displays an error when the task file cannot be loaded. */
        public static void showFileLoadError() {
            Helper.printDivider();
            System.out.println("Error loading tasks from the history file!");
            Helper.printDivider();
        }
    }
}
