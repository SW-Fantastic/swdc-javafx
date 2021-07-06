package org.swdc.fx.config.editors;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

/**
 * Boolean属性的编辑器，
 * boolean字段可以使用此编辑器。
 */
public class CheckEditor extends PropEditorView {

    private CheckBox checkBox;

    public CheckEditor(PropertySheet.Item item) {
        super(item);
    }

    public CheckEditor(ConfigPropertiesItem item) {
        super(item);
    }

    @Override
    public Node getEditor() {
        if (this.checkBox == null) {
            this.checkBox = new CheckBox();
            this.checkBox.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                this.getItem().setValue(newValue);
            }));
        }
        return this.checkBox;
    }

    @Override
    public Object getValue() {
        if (this.checkBox == null) {
            this.checkBox = (CheckBox) this.getEditor();
        }
        return checkBox.isSelected();
    }

    @Override
    public void setValue(Object o) {
        if (this.checkBox == null) {
            this.checkBox = (CheckBox) this.getEditor();
        }
        Boolean bool = Boolean.valueOf(o.toString());
        this.checkBox.setSelected(bool);
    }
}
