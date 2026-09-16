package bany.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bany.commands.ResponseMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Checks that the chat layout provides enough height for complete responses. */
class DialogLayoutTest {
    /** Starts JavaFX so FXML and CSS can be laid out on its application thread. */
    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(ready::countDown);
        assertTrue(ready.await(10, TimeUnit.SECONDS), "JavaFX should start within ten seconds");
    }

    @Test
    void growingConversation_preservesWrappedResponseHeight() throws Exception {
        FutureTask<Void> checkLayout = new FutureTask<>(() -> {
            FXMLLoader loader = new FXMLLoader(
                    MainWindow.class.getResource("/bany/gui/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            VBox container = (VBox) loader.getNamespace().get("dialogContainer");
            new Scene(root, 400, 600);

            String response = "Here are the tasks in your list:\n"
                    + "1. [D][ ] survive (by: 11-11-2222 11:11)\n"
                    + "2. [E][ ] study (from: 21-01-2022 11:11 to: 22-01-2022 19:47)\n"
                    + "3. [T][ ] run\n4. [T][ ] read\n5. [T][ ] exercise";
            for (int i = 0; i < 8; i++) {
                container.getChildren().addAll(
                        DialogBox.getUserDialog("list", null),
                        DialogBox.getBotDialog(ResponseMessage.info(response), null));
                root.applyCss();
                root.layout();

                for (var row : container.getChildren()) {
                    Label label = (Label) row.lookup("#dialog");
                    assertTrue(label.getHeight() + 0.5 >= label.prefHeight(label.getWidth()),
                            "Every message should have enough height for all wrapped lines");
                }
            }
            assertTrue(container.getHeight() > root.getHeight(),
                    "Long conversations should grow beyond the viewport so they can scroll");
            return null;
        });
        Platform.runLater(checkLayout);
        checkLayout.get(15, TimeUnit.SECONDS);
    }
}
