package bany.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import bany.BanyService;
import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.commands.CommandParser;
import bany.utilities.CommandValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Starts the JavaFX version of Bany and wires its shared dependencies together.
 */
public class Main extends Application {
    /** Scroll pane retained for the legacy in-class input handler. */
    private ScrollPane scrollPane;
    /** Dialog container retained for the legacy in-class input handler. */
    private VBox dialogContainer;
    /** User-input field retained for the legacy in-class input handler. */
    private TextField userInput;
    /** Send button retained for the legacy in-class input handler. */
    private Button sendButton;
    /** Scene retained for the legacy in-class input handler. */
    private Scene scene;
    /** Avatar used by the legacy in-class input handler for user messages. */
    private Image userImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("/bany/gui/images/DaUser.png")));
    /** Avatar used by the legacy in-class input handler for Bany messages. */
    private Image banyImage = new Image(Objects.requireNonNull(
            getClass().getResourceAsStream("/bany/gui/images/BanyLogo.png")));
    /** In-memory task storage shared by all GUI commands. */
    private TaskStorage taskStorage = new TaskStorage();
    /** Parser that converts GUI input into commands. */
    private CommandParser commandParser = new CommandParser(new CommandValidator());
    /** Location of Bany's saved task data. */
    private Path historyPath = Path.of("data", "bany.txt");
    /** Repository that loads and saves GUI task data. */
    private TaskFileRepository repository = new TaskFileRepository(historyPath);
    /** Builder for responses displayed by the GUI. */
    private Responder responder = new Responder();

    /** Service injected into the main-window controller. */
    private BanyService banyService = new BanyService(
            repository,
            taskStorage,
            commandParser,
            responder);

    /**
     * Loads the main-window layout, injects the command service, and shows the stage.
     *
     * @param stage primary JavaFX stage for the application.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/bany/gui/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setBanyGuiService(banyService);
            repository.load(taskStorage);
            fxmlLoader.<MainWindow>getController().showWelcomeMessage();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
