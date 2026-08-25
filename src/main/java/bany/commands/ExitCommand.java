package bany.commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;

/** Executes the command that ends the Bany session. */
public class ExitCommand extends Command {

    /** Creates an exit command. */
    public ExitCommand() {
    }

    /** Displays the goodbye message for the current session. */
    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        ui.showGoodbye();
    }

    /**
     * Indicates that this command ends the application loop.
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
