import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine();
            line = line.trim();
            Helper.printDivider();
            int firstSpace = line.indexOf(' ');

            String command;
            String remaining;

            if (firstSpace == -1) {
                command = line;    // "event"
                remaining = "";    // nothing after it
            } else {
                command = line.substring(0, firstSpace);
                remaining = line.substring(firstSpace + 1);
            }

            if (command.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                Helper.printDivider();
                break;
            }

            if (command.equalsIgnoreCase("list")) {
                int count = 1;
                System.out.println("Here are the tasks in your list:");
                for (Task task : listOfTasks) {
                    System.out.printf("%d.%s%n", count, task);
                    count++;
                }
                Helper.printDivider();
                continue;
            }

            if (command.equalsIgnoreCase("mark")) {
                Task task = getTask(remaining, listOfTasks);
                if (task != null) {
                    updateTaskStatus(task, true);
                }
                continue;
            }

            if (command.equalsIgnoreCase("unmark")) {
                Task task = getTask(remaining, listOfTasks);
                if (task != null) {
                    updateTaskStatus(task, false);
                }
                continue;
            }

            if (command.equalsIgnoreCase("delete")) {
                int taskIndex = getTaskIndex(remaining, listOfTasks);
                if (taskIndex != -1) {
                    Task deletedTask = listOfTasks.remove(taskIndex);
                    System.out.println("Noted. I've removed this task:");
                    System.out.printf("   %s%n", deletedTask);
                    System.out.printf("Now you have %d tasks in the list.%n", listOfTasks.size());
                    Helper.printDivider();
                }
                continue;
            }

            if (command.equalsIgnoreCase("todo")) {
                Task task = new ToDo(remaining);
                listOfTasks.add(task);
                announceTask(task, listOfTasks);
                continue;
            }
            if (command.equalsIgnoreCase("deadline")) {
                Pattern pattern = Pattern.compile(
                        "^(.+?)\\s+/by\\s+(.+)$",
                        Pattern.CASE_INSENSITIVE
                );

                Matcher matcher = pattern.matcher(remaining);
                String name, by = "";

                if (matcher.matches()) {
                    name = matcher.group(1);
                    by = matcher.group(2);
                } else {
                    System.out.println("Invalid format. Please try again.");
                    Helper.printDivider();
                    continue;
                }
                if (name.isEmpty()) {
                    System.out.println("Invalid name. Please try again.");
                }

                Task task = new Deadline(name, by);
                listOfTasks.add(task);
                announceTask(task, listOfTasks);
                continue;
            }

            if (command.equalsIgnoreCase("event")) {
                Pattern pattern = Pattern.compile(
                        "^(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$",
                        Pattern.CASE_INSENSITIVE
                );

                Matcher matcher = pattern.matcher(remaining);
                String name, from, to = "";

                if (matcher.matches()) {
                    name = matcher.group(1);
                    from = matcher.group(2);
                    to = matcher.group(3);
                } else {
                    System.out.println("Invalid format. Please try again.");
                    Helper.printDivider();
                    continue;
                }

                if (name.isEmpty()) {
                    System.out.println("Invalid name. Please try again.");
                }
                
                Task task = new Event(name, from , to);
                listOfTasks.add(task);
                announceTask(task, listOfTasks);
                continue;
            }

            System.out.println("Invalid Command");
            Helper.printDivider();
        }
    }

    /**
     * Retrieves the task referred to in a mark or unmark command.
     *
     * @param number command entered by the user
     * @param tasks tasks currently stored by the chatbot
     * @return the selected task, or {@code null} when the task number is invalid
     */
    private static Task getTask(String number, List<Task> tasks) {
        int taskIndex = getTaskIndex(number, tasks);
        if (taskIndex == -1) {
            return null;
        }
        return tasks.get(taskIndex);
    }

    /**
     * Converts a user-provided 1-based task number into a zero-based list index.
     *
     * @param number task number entered by the user
     * @param tasks tasks currently stored by the chatbot
     * @return the zero-based index, or {@code -1} when the number is invalid
     */
    private static int getTaskIndex(String number, List<Task> tasks) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(number.trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid task number!");
            Helper.printDivider();
            return -1;
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            System.out.println("Invalid task number!");
            Helper.printDivider();
            return -1;
        }
        return taskNumber - 1;
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
        System.out.printf("   %s%n", task);
        Helper.printDivider(); //Some changes
    }

    private static void announceTask(Task task, List<Task> tasks) {
        System.out.println("Got it. I've added this task:");
        System.out.printf("  %s%n", task);
        System.out.printf("Now you have %d tasks in the list.%n", tasks.size());
        Helper.printDivider();
    }
}
