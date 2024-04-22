package org.swdc.fx.config.editors;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

/**
 * 文本属性的编辑器，字段为String的时候可以使用此编辑器。
 */
public class TextEditor extends PropEditorView {

    protected TextField field;

    public TextEditor(PropertySheet.Item item) {
        super(item);
    }

    public TextEditor(ConfigPropertiesItem item) {
        super(item);
    }


    @Override
    public Node getEditor() {
        if (field == null) {
            field = new TextField();
            field.textProperty().addListener(((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    this.getItem().setValue(newValue);
                }
            }));
        }
        return field;
    }

    @Override
    public Object getValue() {
        if (field == null) {
            field = (TextField) this.getEditor();
        }
        return field.getText();
    }

    @Override
    public void setValue(Object o) {
        if (field == null) {
            field = (TextField) this.getEditor();
        }
        field.setText(o.toString());
    }

}
