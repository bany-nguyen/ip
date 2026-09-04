package bany.gui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents one chat message with its text and sender image.
 *
 * <p>Each instance loads the shared dialog FXML layout and can reverse its
 * children to place the avatar on the opposite side of the message.</p>
 */
public class DialogBox extends HBox {
    /** Selectable text area that displays the message text. */
    @FXML
    private TextArea dialog;
    /** Image view that displays the sender avatar. */
    @FXML
    private ImageView displayImage;

    /**
     * Creates a dialog box from the FXML layout.
     *
     * @param text message text to display.
     * @param image avatar associated with the message.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/bany/gui/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayImage.setImage(image);
    }

    /** Reverses the dialog contents so the avatar appears on the left. */
    public void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     *
     * @param text message text to display.
     * @param image user avatar.
     * @return user-message dialog box.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned dialog box for a Bany response.
     *
     * @param text response text to display.
     * @param image Bany avatar.
     * @return Bany-response dialog box.
     */
    public static DialogBox getBotDialog(String text, Image image) {
        var db = new DialogBox(text, image);
        db.flip();
        return db;
    }

}
