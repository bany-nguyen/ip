import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.ToDo;
import errors.InvalidTaskType;
import utilities.CommandValidator;
import utilities.CommandStorage;
import utilities.Helper;
import parsers.CommandParser;
import parsers.DateTimeParser;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Bany {
    private final Ui ui;  //Handles Scanner input and console output
    private final TaskStorage taskStorage; //Data Structure that stores and manage task lists
    private final CommandValidator commandValidator;
    private final TaskFileRepository taskFileRepository;
    private boolean running; //check whether running

    public Bany(Ui ui,
                TaskStorage taskStorage,
                CommandValidator commandValidator,
                TaskFileRepository taskFileRepository) {
        this.ui = ui;
        this.taskStorage = taskStorage;
        this.commandValidator = commandValidator;
        this.taskFileRepository = taskFileRepository;
        this.running = true;
    }

    public void initialise() {
        loadTasks();
        ui.showWelcome();
        while (running && ui.hasNextCommand()) {
            String input = ui.readCommand();
            handleInput(input);
        }
    }

    private void loadTasks() {
        try {
            taskStorage.replaceTasks(taskFileRepository.load());
        } catch (IOException | InvalidTaskType e) {
            Ui.ErrorUi.showFileLoadError();
        }
    }

    private void handleInput(String input) {
        Map<String, String> parsedCommand;
        try {
            parsedCommand = CommandParser.parse(input);
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
                    List<String> tagNames;
                    try {
                        tagNames = CommandParser.getTagNames(input);
                    } catch (IllegalArgumentException e) {
                        ui.showInvalidCommand();
                        break;
                    }
                    String duplicateTag = commandValidator.findDuplicateCriticalTag(command, tagNames);
                    if (duplicateTag != null) {
                        ui.showDuplicateTag(duplicateTag);
                        break;
                    }

                    Task task = createTask(parsedCommand);
                    if (task != null) {
                        if (commandValidator.hasTagWarning(command, tagNames)) {
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

                case "FIND": {
                    String desc = parsedCommand.get("description");
                    if (desc == null || desc.isBlank()) {
                        ui.showInvalidCommand();
                        break;
                    }
                    findTask(desc);
                    break;
                }

            }
        } else {
            String closestCommand = Helper.closestWordMatch(command);
            ui.showInvalidCommand(closestCommand);
        }
    }

    private Task createTask(Map<String, String> parsedCommand) {
        if (parsedCommand == null) {
            ui.showInvalidCommand();
            return null;
        }

        String type = parsedCommand.get("command");
        String description = parsedCommand.get("description");

        if (type == null || type.isBlank()) {
            ui.showInvalidCommand();
            return null;
        }

        if (description == null || description.isBlank()) {
            ui.showInvalidTaskDescription();
            return null;
        }

        switch (type) {
            case "TODO":
                try {
                    return new ToDo(description, Task.allocateId());
                } catch (IllegalStateException e) {
                    ui.showTaskCreationFail();
                    break;
                }

            case "DEADLINE":
                String by = parsedCommand.get("by");
                if (by == null || by.isBlank()) {
                    ui.showInvalidTaskInitiation();
                    break;
                }
                try {
                    LocalDateTime byDateTime = DateTimeParser.createLocalDateTime(by);
                    return new Deadline(description, byDateTime, Task.allocateId());
                } catch (IllegalArgumentException e) {
                    ui.showInvalidDateTime();
                    break;
                } catch (IllegalStateException e) {
                    ui.showTaskCreationFail();
                    break;
                }

            case "EVENT":
                String from =  parsedCommand.get("from");
                String to = parsedCommand.get("to");
                if (from == null || to == null || from.isBlank() || to.isBlank()) {
                    ui.showInvalidTaskInitiation();
                    break;
                }
                try {
                    LocalDateTime fromDateTime = DateTimeParser.createLocalDateTime(from);
                    LocalDateTime toDateTime = DateTimeParser.createLocalDateTime(to);
                    if (toDateTime.isBefore(fromDateTime)) {
                        ui.showInvalidEventRange();
                        break;
                    }
                    return new Event(description, fromDateTime, toDateTime, Task.allocateId());
                } catch (IllegalArgumentException e) {
                    ui.showInvalidDateTime();
                    break;
                } catch (IllegalStateException e) {
                    ui.showTaskCreationFail();
                    break;
                }
        }
        return null;
    }

    private void addTask(Task task) {
        if (task == null) {
            ui.showTaskCreationFail();
            return;
        }

        taskStorage.addTask(task);
        if (saveTasks()) {
            ui.showAddTask(task, taskStorage.getSize());
        }
    }

    /**
     * Persists the current task list and displays an error if writing fails.
     *
     * @return true if the list was saved successfully
     */
    private boolean saveTasks() {
        try {
            taskFileRepository.save(taskStorage.getTasks());
            return true;
        } catch (IOException e) {
            Ui.ErrorUi.showFileUpdateError();
            return false;
        }
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
        if (saveTasks()) {
            ui.showDeleteTask(task, taskStorage.getSize());
        }
    }

    private void markTask(int taskNo) {
        Task task = taskStorage.markTask(taskNo);
        if (task == null) {
            ui.showOutOfBoundIndex("mark");
            return;
        }

        if (saveTasks()) {
            ui.showMarkTask(task);
        }

    }

    private void unmarkTask(int taskNo) {
        Task task = taskStorage.unmarkTask(taskNo);
        if (task == null) {
            ui.showOutOfBoundIndex("unmark");
            return;
        }

        if (saveTasks()) {
            ui.showUnmarkTask(task);
        }

    }

    private void findTask(String description) {
        List<Task> matchedTasks = new ArrayList<>();
        List<Task> tasks = taskStorage.getTasks();

        for (Task task : tasks) {
            if (task.getDescription().contains(description)) {
                matchedTasks.add(task);
            }
        }

        if  (matchedTasks.isEmpty()) {
            ui.showNoSuchTask();
            return;
        }

        ui.showMatchedTasks(matchedTasks);
    }

    public static void main(String[] args) {
        Ui ui = new Ui(new Scanner(System.in));
        TaskStorage taskList = new TaskStorage();
        CommandValidator commandValidator = new CommandValidator();
        Path historyPath = Path.of("data", "bany.txt");
        TaskFileRepository repository = new TaskFileRepository(historyPath);
        Bany bany = new Bany(ui, taskList, commandValidator, repository);
        bany.initialise();

    }
}
