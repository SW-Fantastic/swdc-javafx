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

    private Map<String,String> labeledValue = new HashMap<>();

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

        String[] optionsParts;
        if (optionsList.contains(",")) {
            optionsParts = optionsList.split(",");
        } else if (optionsList.contains(";")) {
            optionsParts = optionsList.split(";");
        } else {
            optionsParts = new String[]{ optionsList };
        }

        for (String opt: optionsParts) {
            String[] kv = new String[] { opt, opt};
            if (opt.contains("=>")) {
                kv[0] = opt.substring(0,opt.indexOf("=>"));
                kv[1] = opt.substring(opt.indexOf("=>") + 2);
            } else if (opt.contains("->")) {
                kv[0] = opt.substring(0,opt.indexOf("->")).trim();
                kv[1] = opt.substring(opt.indexOf("->") + 2);
            } else if (opt.contains("=")) {
                kv = opt.split("=");
            }
            labeledValue.put(kv[0].trim(),kv[1].trim());
        }

        List<String> arrOptions = comboBox.getItems();
        arrOptions.clear();
        langKeysReverseMap.clear();

        for (Map.Entry<String,String> option: labeledValue.entrySet()) {
            String key = option.getKey();
            String val = option.getValue();
            if (key.startsWith("%")) {
                langKeysReverseMap.put(bundle.getString(key.substring(1)),key);
            }
            if (val.startsWith("%")) {
                langKeysReverseMap.put(bundle.getString(val.substring(1)),val);
            }
            arrOptions.add(key.startsWith("%") ? bundle.getString(key.substring(1)): key);
        }

    }

    @Override
    public Node getEditor() {
        if (comboBox == null) {
            comboBox = new ComboBox();
            comboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
                String val = comboBox.getSelectionModel().getSelectedItem().toString();
                if (langKeysReverseMap.containsKey(val)) {
                    val = "%" + langKeysReverseMap.get(val);
                }
                getItem().setValue(labeledValue.get(val));
            }));
            refreshValues();
        }
        return comboBox;
    }

    @Override
    public Object getValue() {
        if (comboBox == null) {
            comboBox = new ComboBox();
            comboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
                String val = comboBox.getSelectionModel().getSelectedItem().toString();
                if (langKeysReverseMap.containsKey(val)) {
                    val = "%" + langKeysReverseMap.get(val);
                }
                getItem().setValue(labeledValue.get(val));
            }));
            refreshValues();
        }
        if (comboBox.getSelectionModel().isEmpty()) {
            return "";
        }
        String val = (String) comboBox.getSelectionModel().getSelectedItem();
        if (langKeysReverseMap.containsKey(val)) {
            val = "%" + langKeysReverseMap.get(val);
        }
        return labeledValue.get(val);
    }

    @Override
    public void setValue(Object value) {
        if (comboBox == null) {
            comboBox = new ComboBox();
            comboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
                String val = comboBox.getSelectionModel().getSelectedItem().toString();
                if (langKeysReverseMap.containsKey(val)) {
                    val = "%" + langKeysReverseMap.get(val);
                }
                getItem().setValue(labeledValue.get(val));
            }));
            refreshValues();
        }
        String val = value.toString();
        if (langKeysReverseMap.containsKey(val)) {
            val = "%" + langKeysReverseMap.get(val);
        }

        String key = null;
        for (Map.Entry<String,String> entry: labeledValue.entrySet()) {
            if (entry.getValue().equals(val)) {
                key = entry.getKey();
            }
        }

        if (key.startsWith("%")) {
            key = getResources().getResourceBundle().getString(key.substring(1));
        }

        List<String> valList = comboBox.getItems();
        for (String item: valList) {
            if (item.equalsIgnoreCase(key)) {
                comboBox.getSelectionModel().select(item);
                break;
            }
        }
        if (val.equals("unavailable")) {
            comboBox.setDisable(true);
        }
    }
}
