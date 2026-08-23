package commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.Ui;

/** Displays every task currently stored by Bany. */
public class ListCommand extends Command {

    @Override
    public void execute(TaskStorage tasks, Ui ui,
                        TaskFileRepository repository) {
        ui.showList(tasks.getTasks());
    }
}
