import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.ToDo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;

public class Bany {
    private final Ui ui;  //Handles Scanner input and console output
    private final TaskStorage taskStorage; //Data Structure that stores and manage task lists
    private boolean running; //check whether running

    public Bany(Ui ui, TaskStorage taskStorage) {
        this.ui = ui;
        this.taskStorage = taskStorage;
        this.running = true;
    }

    public void initialise() {
        ui.showWelcome();
        while (running && ui.hasNextCommand()) {
            String input = ui.readCommand();
            handleInput(input);
        }
    }

    private void handleInput(String input) {
        Map<String, String> parsedCommand;
        try {
            parsedCommand = Parser.parse(input);
        } catch (IllegalArgumentException e) {
            ui.showInvalidCommand();
            return;
        }

        String command = parsedCommand.get("command");
        if (CommandStorage.checkValidAllCommandWord(command)) {
            switch (command) {
                case "BYE":
                    ui.showGoodbye();
                    running = false;
                    break;

                case "LIST":
                    listTasks();
                    break;

                case "DEADLINE", "TODO", "EVENT": {
                    List<String> tagNames = Parser.getTagNames(input);
                    String duplicateTag = findDuplicateCriticalTag(command, tagNames);
                    if (duplicateTag != null) {
                        ui.showDuplicateTag(duplicateTag);
                        break;
                    }

                    Task task = createTask(parsedCommand);
                    if (task != null) {
                        if (hasTagWarning(command, tagNames)) {
                            ui.showTagWarning();
                        }
                        addTask(task);
                    }
                    break;
                }

                case "MARK": {
                    String desc = parsedCommand.get("description");

                    if (desc == null || desc.isBlank() || !Helper.isInteger(desc)) {
                        ui.showInvalidCommand();
                        break;
                    }

                    int taskNo = Integer.parseInt(desc) - 1;
                    markTask(taskNo);
                    break;
                }

                case "UNMARK": {
                    String desc = parsedCommand.get("description");
                    if (desc == null || desc.isBlank() || !Helper.isInteger(desc)) {
                        ui.showInvalidCommand();
                        break;
                    }

                    int taskNo = Integer.parseInt(desc) - 1;
                    unmarkTask(taskNo);
                    break;
                }

                case "DELETE": {
                    String desc = parsedCommand.get("description");
                    if (desc == null || desc.isBlank() || !Helper.isInteger(desc)) {
                        ui.showInvalidCommand();
                        break;
                    }

                    int taskNo = Integer.parseInt(desc) - 1;
                    deleteTask(taskNo);
                    break;
                }

            }
        } else {
            String closestCommand = Helper.closestWordMatch(command);
            ui.showInvalidCommand(closestCommand);
        }
    }

    private String findDuplicateCriticalTag(String command, List<String> tagNames) {
        Set<String> criticalTags = switch (command) {
            case "DEADLINE" -> Set.of("by");
            case "EVENT" -> Set.of("from", "to");
            default -> Set.of();
        };

        Set<String> seenTags = new HashSet<>();
        for (String tagName : tagNames) {
            if (criticalTags.contains(tagName) && !seenTags.add(tagName)) {
                return tagName;
            }
        }
        return null;
    }

    private boolean hasTagWarning(String command, List<String> tagNames) {
        List<String> expectedOrder = switch (command) {
            case "DEADLINE" -> List.of("by");
            case "EVENT" -> List.of("from", "to");
            default -> List.of();
        };

        Set<String> expectedTags = Set.copyOf(expectedOrder);
        boolean hasExtraTag = tagNames.stream()
                .anyMatch(tagName -> !expectedTags.contains(tagName));
        List<String> actualRequiredOrder = tagNames.stream()
                .filter(expectedTags::contains)
                .toList();
        boolean hasWrongOrder = !actualRequiredOrder.equals(expectedOrder);

        return hasExtraTag || hasWrongOrder;
    }

    private Task createTask(Map<String, String> parsedCommand) {
        String type = parsedCommand.get("command");
        String description = parsedCommand.get("description");

        if (description.isBlank()) {
            ui.showInvalidTaskDescription();
            return null;
        }

        switch (type) {
            case "TODO":
                return new ToDo(description);

            case "DEADLINE":
                String by = parsedCommand.get("by");
                if (by == null || by.isBlank()) {
                    ui.showInvalidTaskInitiation();
                    break;
                }
                return new Deadline(description, by);

            case "EVENT":
                String from =  parsedCommand.get("from");
                String to = parsedCommand.get("to");
                if (from == null || to == null || from.isBlank() || to.isBlank()) {
                    ui.showInvalidTaskInitiation();
                    break;
                }
                return new Event(description, from, to);
        }
        return null;
    }

    private void addTask(Task task) {
        if (task == null) {
            ui.showTaskCreationFail();
            return;
        }

        taskStorage.addTask(task);
        List<Task> tasks = taskStorage.getTasks();
        ui.showAddTask(task, tasks);
    }

    private void listTasks() {
        List<Task> tasks = taskStorage.getTasks();
        ui.showList(tasks);
    }

    private void deleteTask(int taskNo) {
        Task task = taskStorage.deleteTask(taskNo);
        if  (task == null) {
            ui.showOutOfBoundIndex("delete");
            return;
        }
        List<Task> tasks = taskStorage.getTasks();
        ui.showDeleteTask(task, tasks);
    }

    private void markTask(int taskNo) {
        Task task = taskStorage.markTask(taskNo);
        if (task == null) {
            ui.showOutOfBoundIndex("mark");
            return;
        }

        ui.showMarkTask(task);

    }

    private void unmarkTask(int taskNo) {
        Task task = taskStorage.unmarkTask(taskNo);
        if (task == null) {
            ui.showOutOfBoundIndex("unmark");
            return;
        }

        ui.showUnmarkTask(task);

    }

    public static void main(String[] args) {
        Ui ui = new Ui(new Scanner(System.in));
        TaskStorage taskList = new TaskStorage();

        Bany bany = new Bany(ui, taskList);
        bany.initialise();

    }
}
