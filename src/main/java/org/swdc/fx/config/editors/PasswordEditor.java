package org.swdc.fx.config.editors;

import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;

public class PasswordEditor extends TextEditor {
    public PasswordEditor(PropertySheet.Item item) {
        super(item);
    }

    public PasswordEditor(ConfigPropertiesItem item) {
        super(item);
    }

    @Override
    public Node getEditor() {
        if (field == null) {
            field = new PasswordField();
            field.textProperty().addListener(((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    this.getItem().setValue(newValue);
                }
            }));
        }
        return field;
    }

}
