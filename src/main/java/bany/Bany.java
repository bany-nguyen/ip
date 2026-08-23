package bany;

import commands.Command;
import commands.Parser;
import errors.InvalidTaskType;
import utilities.CommandValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Runs Bany's command-line application.
 *
 * <p>Bany owns the application lifecycle, while {@link Parser} and
 * {@link Command} objects own command-specific behaviour.</p>
 */
public class Bany {
    private final Ui ui;
    private final TaskStorage taskStorage;
    private final TaskFileRepository taskFileRepository;
    private final Parser parser;

    /**
     * Creates an application with its runtime dependencies.
     *
     * @param ui user-interface component
     * @param taskStorage in-memory task storage
     * @param commandValidator command validation rules
     * @param taskFileRepository task persistence component
     */
    public Bany(Ui ui,
                TaskStorage taskStorage,
                CommandValidator commandValidator,
                TaskFileRepository taskFileRepository) {
        this.ui = ui;
        this.taskStorage = taskStorage;
        this.taskFileRepository = taskFileRepository;
        this.parser = new Parser(commandValidator);
    }

    /**
     * Starts Bany and processes commands until the user enters {@code BYE}
     * or the input stream reaches its end.
     */
    public void run() {
        loadTasks();
        ui.showWelcome();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                Command command = parser.parse(fullCommand);
                command.execute(taskStorage, ui, taskFileRepository);
                isExit = command.isExit();
            } catch (IllegalArgumentException e) {
                // CommandParser uses IllegalArgumentException for blank or
                // malformed input. The UI turns it into friendly output.
                ui.showInvalidCommand();
            }
        }
    }

    /** Loads saved tasks before the first command is read. */
    private void loadTasks() {
        try {
            taskStorage.replaceTasks(taskFileRepository.load());
        } catch (IOException | InvalidTaskType e) {
            Ui.ErrorUi.showFileLoadError();
        }
    }

    /**
     * Keeps the previous entry-point name available to existing callers.
     */
    public void initialise() {
        run();
    }

    /**
     * Starts Bany using standard input and the default data file.
     *
     * @param args command-line arguments, currently unused
     */
    public static void main(String[] args) {
        Ui ui = new Ui(new Scanner(System.in));
        TaskStorage taskStorage = new TaskStorage();
        CommandValidator commandValidator = new CommandValidator();
        Path historyPath = Path.of("data", "bany.txt");
        TaskFileRepository repository = new TaskFileRepository(historyPath);

        Bany bany = new Bany(ui, taskStorage, commandValidator, repository);
        bany.run();
    }
}
