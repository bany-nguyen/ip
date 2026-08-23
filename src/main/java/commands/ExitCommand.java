package commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;

/** Executes the command that ends the Bany session. */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
