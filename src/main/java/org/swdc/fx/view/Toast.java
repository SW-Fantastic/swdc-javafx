package org.swdc.fx.view;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.swdc.fx.FXApplication;

public class Toast {


    private Parent content;

    public Toast() {
        try {
            FXMLLoader loader = new FXMLLoader();
            content = loader.load(FXApplication.class.getModule().getResourceAsStream("/views/MessageView.fxml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void show(String text) {

        BorderPane root = (BorderPane) this.content;
        HBox container = (HBox)root.getCenter();
        Label label = (Label) container.getChildren().get(0);

        label.setText(text);

        Notifications.create()
                .graphic(content)
                .position(Pos.CENTER)
                .hideAfter(Duration.seconds(2))
                .hideCloseButton()
                .show();
    }

    public static void showMessage(String text) {
        Toast toast = new Toast();
        toast.show(text);
    }


}
