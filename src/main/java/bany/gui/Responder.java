package bany.gui;

import java.util.List;

import bany.tasks.Task;

/** Builds user-facing response strings for the Bany GUI. */
public class Responder {

    /**
     * Builds Bany's welcome message.
     *
     * @return welcome message.
     */
    public String respondWelcome() {
        return """
                Hello! I'm Bany.
                What can I do for you?
                """;
    }

    /**
     * Builds Bany's goodbye message.
     *
     * @return goodbye message.
     */
    public String respondGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Builds confirmation after a task is added.
     *
     * @param task task that was added.
     * @param size number of tasks currently stored.
     * @return task-added response.
     */
    public String respondAddTask(Task task, int size) {
        return String.format("Got it. I've added this task:%n  %s%n"
                + "Now you have %d tasks in the list.", task, size);
    }

    /**
     * Builds an error response for a blank task description.
     *
     * @return invalid-description message.
     */
    public String respondInvalidTaskDescription() {

        return "Task description cannot be blank!";
    }

    /**
     * Builds an error response when required task tags are missing.
     *
     * @return invalid-tag message.
     */
    public String respondInvalidTaskInitiation() {
        return "Your tags for the task do not match the requirements!";
    }

    /**
     * Builds an error response when a date-time has an unsupported format.
     *
     * @return invalid-date-time message.
     */
    public String respondInvalidDateTime() {
        return "Date-time must use the format dd-MM-yyyy HH:mm!";
    }

    /**
     * Builds an error response when an event ends before it starts.
     *
     * @return invalid-event-range message.
     */
    public String respondInvalidEventRange() {
        return "An event's end date-time cannot be before its start date-time!";
    }

    /**
     * Builds an error response for a repeated critical tag.
     *
     * @param tagName repeated tag name.
     * @return duplicate-tag message.
     */
    public String respondDuplicateTag(String tagName) {
        return String.format("The tag /%s can only be used once!", tagName);
    }

    /**
     * Builds a warning when task tags are extra or out of order.
     *
     * @return tag warning message.
     */
    public String respondTagWarning() {
        return "Warning: the command contains extra tags or tags in an unexpected order.\n"
                + "The task will still be added.";
    }

    /**
     * Builds an error response for a task index outside the current task list.
     *
     * @param type action that was attempted.
     * @param size number of tasks currently stored.
     * @return out-of-bounds response.
     */
    public String respondOutOfBoundIndex(String type, int size) {
        return String.format("The task that you are trying to %s is not here!", type)
                + String.format(" Please enter an integer between 1 and %d", size);
    }

    /**
     * Builds confirmation after a task is marked as done.
     *
     * @param task task that was marked.
     * @return mark-task response.
     */
    public String respondMarkTask(Task task) {
        return String.format("Nice! I've marked this task as done:%n   %s", task);
    }

    /**
     * Builds confirmation after a task is marked as not done.
     *
     * @param task task that was unmarked.
     * @return unmark-task response.
     */
    public String respondUnmarkTask(Task task) {
        return String.format("Nice! I've unmarked this task as done:%n   %s", task);
    }

    /**
     * Builds confirmation after a task is deleted.
     *
     * @param task task that was deleted.
     * @param size number of tasks remaining.
     * @return task-deleted response.
     */
    public String respondDeleteTask(Task task, int size) {
        return String.format("Noted. I've removed this task:%n   %s%n"
                + "Now you have %d tasks in the list.", task, size);
    }

    /**
     * Builds a response containing all tasks in their current storage order.
     *
     * @param listOfTasks tasks to display.
     * @return formatted task-list response.
     */
    public String respondList(List<Task> listOfTasks) {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        int count = 1;
        for (Task task : listOfTasks) {
            response.append(System.lineSeparator())
                    .append(count++)
                    .append('.')
                    .append(' ')
                    .append(task);
        }
        return response.toString();
    }

    /**
     * Builds a generic invalid-command response.
     *
     * @return invalid-command message.
     */
    public String respondInvalidCommand() {
        return "Invalid command. Try again.";
    }

    /**
     * Builds an invalid-command response with an optional suggestion.
     *
     * @param closestCommand closest recognized command, or {@code null}.
     * @return invalid-command response.
     */
    public String respondInvalidCommand(String closestCommand) {
        if (closestCommand == null) {
            return respondInvalidCommand();
        }
        return String.format("Command not found. Do you mean %s?", closestCommand);
    }

    /**
     * Builds a message when a search matches no tasks.
     *
     * @return no-match message.
     */
    public String respondNoSuchTask() {
        return "Your query does not match any task in the current task list!";
    }

    /**
     * Builds a response containing tasks matching a search query.
     *
     * @param matchedTasks tasks that matched the query.
     * @return formatted matching-tasks response.
     */
    public String respondMatchedTasks(List<Task> matchedTasks) {
        StringBuilder response = new StringBuilder(
                "Here are the matching tasks in your list:");
        int count = 1;
        for (Task task : matchedTasks) {
            response.append(System.lineSeparator())
                    .append(count++)
                    .append('.')
                    .append(task);
        }
        return response.toString();
    }

    /** Builds responses for task-file persistence errors. */
    public static class ErrorResponder {
        /** Creates the persistence-error response helper. */
        public ErrorResponder() {
        }

        /**
         * Builds an error response when the task file cannot be updated.
         *
         * @return file-update-error message.
         */
        public static String respondFileUpdateError() {
            return "Error writing task to history file!";
        }

        /**
         * Builds an error response when the task file cannot be loaded.
         *
         * @return file-load-error message.
         */
        public static String respondFileLoadError() {
            return "Error loading tasks from the history file!";
        }
    }
}
