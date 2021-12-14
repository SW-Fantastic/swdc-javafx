package org.swdc.fx.config.editors;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.PropEditorView;

import java.util.List;

public class SelectionEditor extends PropEditorView {

    private ComboBox comboBox;

    public SelectionEditor(PropertySheet.Item item) {
        super(item);
    }

    public SelectionEditor(ConfigPropertiesItem item) {
        super(item);
    }

    private void refreshValues() {
        PropEditor editorInfo = getItem().getEditorInfo();
        String optionsList = editorInfo.resource();

        String[] options;

        if (optionsList.contains(",")) {
            options = optionsList.split(",");
        } else if (optionsList.contains(";")) {
            options = optionsList.split(";");
        } else {
            options = new String[]{ optionsList };
        }

        List<String> arrOptions = comboBox.getItems();
        arrOptions.clear();

        for (String option: options) {
            arrOptions.add(option);
        }

    }

    @Override
    public Node getEditor() {
        if (comboBox == null) {
            comboBox = new ComboBox();
            refreshValues();
        }
        return comboBox;
    }

    @Override
    public Object getValue() {
        if (comboBox == null) {
            comboBox = new ComboBox();
            refreshValues();
        }
        if (comboBox.getSelectionModel().isEmpty()) {
            return "";
        }
        String val = (String) comboBox.getSelectionModel().getSelectedItem();
        return val;
    }

    @Override
    public void setValue(Object value) {
        if (comboBox == null) {
            comboBox = new ComboBox();
            refreshValues();
        }
        String val = value.toString();
        List<String> valList = comboBox.getItems();
        for (String item: valList) {
            if (item.equalsIgnoreCase(val)) {
                comboBox.getSelectionModel().select(item);
                break;
            }
        }
    }
}
