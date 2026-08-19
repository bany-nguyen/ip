import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bany {
    public static void main(String[] args) {
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
        Scanner scanner = new Scanner(System.in);
        List<Task> listOfTasks = new ArrayList<>(100);

        while (true) {
            String line = scanner.nextLine();
            Helper.printDivider();

            if (line.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                Helper.printDivider();
                break;
            }
            if (line.equalsIgnoreCase("list")) {
                int count = 1;
                System.out.println("Here are the tasks in your list:");
                for (Task task : listOfTasks) {
                    System.out.printf("%d.[%s] %s%n", count, task.getStatusIcon(), task.getDescription());
                    count++;
                }
                Helper.printDivider();
                continue;
            }
            if (line.length() > 4 && line.substring(0, 4).equalsIgnoreCase("mark")) {
                Task task = getTask(line, listOfTasks);
                if (task != null) {
                    updateTaskStatus(task, true);
                }
                continue;
            }

            if (line.length() > 6 && line.substring(0, 6).equalsIgnoreCase("unmark")) {
                Task task = getTask(line, listOfTasks);
                if (task != null) {
                    updateTaskStatus(task, false);
                }
                continue;
            }

            listOfTasks.add(new Task(line));
            System.out.printf("added: %s%n", line);
            Helper.printDivider();
        }
    }

    /**
     * Retrieves the task referred to in a mark or unmark command.
     *
     * @param line command entered by the user
     * @param tasks tasks currently stored by the chatbot
     * @return the selected task, or {@code null} when the task number is invalid
     */
    private static Task getTask(String line, List<Task> tasks) {
        int taskNumber = Helper.firstNumber(line);
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            System.out.println("Invalid task number!");
            Helper.printDivider();
            return null;
        }
        return tasks.get(taskNumber - 1);
    }

    /**
     * Updates a task's completion status and displays the result.
     *
     * @param task task whose status should be updated
     * @param shouldMark whether the task should be marked done
     */
    private static void updateTaskStatus(Task task, boolean shouldMark) {
        if (shouldMark) {
            task.mark();
            System.out.println("Nice! I've marked this task as done:");
        } else {
            task.unmark();
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.printf("   [%s] %s%n", task.getStatusIcon(), task.getDescription());
        Helper.printDivider();
    }
}
