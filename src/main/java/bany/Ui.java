package bany;

import tasks.Task;
import utilities.Helper;

import java.util.List;
import java.util.Scanner;

public class Ui {

    private final Scanner scanner;

    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

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

    public void showGoodbye() {
        Helper.printDivider();
        System.out.println("Bye. Hope to see you again soon!");
        Helper.printDivider();
    }

    public void showAddTask(Task task, int size) {
        Helper.printDivider();
        System.out.println("Got it. I've added this task:");
        System.out.printf("  %s%n", task);
        System.out.printf("Now you have %d tasks in the list.%n", size);
        Helper.printDivider();
    }

    public void showInvalidTaskDescription() {
        Helper.printDivider();
        System.out.println("Task description cannot be blank!");
        Helper.printDivider();
    }

    public void showInvalidTaskInitiation() {
        Helper.printDivider();
        System.out.println("Your tags for the task do not match the requirements!");
        Helper.printDivider();
    }

    public void showInvalidDateTime() {
        Helper.printDivider();
        System.out.println("Date-time must use the format dd-MM-yyyy HH:mm!");
        Helper.printDivider();
    }

    public void showInvalidEventRange() {
        Helper.printDivider();
        System.out.println("An event's end date-time cannot be before its start date-time!");
        Helper.printDivider();
    }

    public void showDuplicateTag(String tagName) {
        Helper.printDivider();
        System.out.printf("The tag /%s can only be used once!%n", tagName);
        Helper.printDivider();
    }

    public void showTagWarning() {
        Helper.printDivider();
        System.out.println("Warning: the command contains extra tags or tags in an unexpected order.\nThe task will still be added.");

    }

    public void showTaskCreationFail() {
        Helper.printDivider();
        System.out.println("Task creation failed! Please check your command.");
        Helper.printDivider();
    }

    public void showOutOfBoundIndex(String type) {
        Helper.printDivider();
        System.out.printf("The task that you are trying to %s is not here!%n", type);
        Helper.printDivider();
    }

    public void showMarkTask(Task task) {
        Helper.printDivider();
        System.out.println("Nice! I've marked this task as done:");
        System.out.printf("   %s%n", task);
        Helper.printDivider();
    }

    public void showUnmarkTask(Task task) {
        Helper.printDivider();
        System.out.println("Nice! I've unmarked this task as done:");
        System.out.printf("   %s%n", task);
        Helper.printDivider();
    }

    public void showDeleteTask(Task task, int size) {
        Helper.printDivider();
        System.out.println("Noted. I've removed this task:");
        System.out.printf("   %s%n", task);
        System.out.printf("Now you have %d tasks in the list.%n", size);
        Helper.printDivider();
    }

    public void showList(List<Task> listOfTasks) {
        Helper.printDivider();
        int count = 1;
        System.out.println("Here are the tasks in your list:");
        for (Task task : listOfTasks) {
            System.out.printf("%d.%s%n", count++, task);
        }
        Helper.printDivider();
    }
    public void showInvalidCommand() {
        Helper.printDivider();
        System.out.println("Invalid command. Try again.");
        Helper.printDivider();
    }

    public void showInvalidCommand(String closestCommand) {
        if (closestCommand == null) {
            showInvalidCommand();
            return;
        }
        Helper.printDivider();
        System.out.printf("Command not found. Do you mean %s?%n", closestCommand);
        Helper.printDivider();
    }
    public String readCommand() {
        return scanner.nextLine();
    }

    public Boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public void showNoSuchTask() {
        Helper.printDivider();
        System.out.println("Your query does not match any task in the current task list!");
        Helper.printDivider();
    }

    public void showMatchedTasks(List<Task> matchedTasks) {
        Helper.printDivider();
        System.out.println("Here are the matching tasks in your list:");
        int count = 1;
        for (Task task : matchedTasks) {
            System.out.printf("%d.%s%n", count++, task);
        }
        Helper.printDivider();
    }

    public static class ErrorUi {
        public static void showFileUpdateError() {
            Helper.printDivider();
            System.out.println("Error writing task to history file!");
            Helper.printDivider();
        }

        public static void showFileLoadError() {
            Helper.printDivider();
            System.out.println("Error loading tasks from the history file!");
            Helper.printDivider();
        }
    }
}
