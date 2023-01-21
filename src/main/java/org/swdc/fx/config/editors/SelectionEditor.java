package org.swdc.fx.config.editors;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.PropEditorView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SelectionEditor extends PropEditorView {

    private ComboBox comboBox;
    private Map<String,String> langKeysReverseMap = new HashMap<>();

    public SelectionEditor(PropertySheet.Item item) {
        super(item);
    }

    public SelectionEditor(ConfigPropertiesItem item) {
        super(item);
    }

    private void refreshValues() {
        ResourceBundle bundle = getResources().getResourceBundle();

        PropEditor editorInfo = getItem().getEditorInfo();
        String optionsList = editorInfo.resource();

        if (optionsList.startsWith("%")) {
            optionsList = bundle.getString(optionsList.substring(1));
        }
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
        langKeysReverseMap.clear();

        for (String option: options) {
            if (option.startsWith("%")) {
                langKeysReverseMap.put(bundle.getString(option.substring(1)),option);
            }
            arrOptions.add(option.startsWith("%") ? bundle.getString(option.substring(1)): option);
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
        if (!langKeysReverseMap.isEmpty()) {
            return langKeysReverseMap.get(val);
        }
        return val;
    }

    @Override
    public void setValue(Object value) {
        if (comboBox == null) {
            comboBox = new ComboBox();
            refreshValues();
        }
        String val = value.toString();
        if (val.startsWith("%")) {
            val = getResources().getResourceBundle().getString(val.substring(1));
        }
        List<String> valList = comboBox.getItems();
        for (String item: valList) {
            if (item.equalsIgnoreCase(val)) {
                comboBox.getSelectionModel().select(item);
                break;
            }
        }
        if (val.equals("unavailable")) {
            comboBox.setDisable(true);
        }
    }
}
