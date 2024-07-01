package org.swdc.fx.config.editors;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

import java.io.File;
import java.util.ResourceBundle;

public class ExternalFolderSelectEditor extends PropEditorView {

    private HBox container;

    private TextField pathField;

    public ExternalFolderSelectEditor(PropertySheet.Item item) {
        super(item);
    }

    public ExternalFolderSelectEditor(ConfigPropertiesItem item) {
        super(item);
    }

    @Override
    public Node getEditor() {
        if (container == null) {
            container = new HBox();
            container.setAlignment(Pos.CENTER_LEFT);
            container.setSpacing(8);

            pathField = new TextField();
            pathField.setEditable(false);

            HBox.setHgrow(pathField, Priority.ALWAYS);
            container.getChildren().add(pathField);

            ResourceBundle bundle = getResources().getResourceBundle();
            Button button = new Button();
            button.setText("打开");
            if (bundle != null) {
                button.setText(bundle.getString("app.open"));
            }
            button.setOnAction(e -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File target = directoryChooser.showDialog(null);
                if (target != null) {
                    getItem().setValue(target.getAbsolutePath());
                }
            });
            container.getChildren().add(button);
        }
        return container;
    }

    @Override
    public Object getValue() {
        if (pathField == null) {
            getEditor();
        }
        return pathField.getText();
    }

    @Override
    public void setValue(Object value) {
        if (pathField == null) {
            getEditor();
        }
        pathField.setText(new File(value.toString()).getAbsolutePath());
    }
}
