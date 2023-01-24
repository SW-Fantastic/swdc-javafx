package org.swdc.fx.config.editors;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.LanguageEntry;
import org.swdc.fx.config.PropEditorView;

import java.util.Map;

public class LanguageSelectionEditor extends PropEditorView {

    private ComboBox<LanguageEntry> languageSupported;

    public LanguageSelectionEditor(ConfigPropertiesItem item) {
        super(item);
    }

    public LanguageSelectionEditor(PropertySheet.Item item) {
        super(item);
    }

    @Override
    public Node getEditor() {

        if (languageSupported == null) {
            create();
        }

        return languageSupported;
    }

    private void create(){
        FXResources resources = getResources();
        Map<String,LanguageEntry> languages = resources.getSupportedLanguages();

        languageSupported = new ComboBox<>();
        languageSupported.getItems().addAll(languages.values());
        languageSupported.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getItem().setValue(newValue != null ? newValue.getLocal(): oldValue.getLocal());
        });
    }

    @Override
    public Object getValue() {
        return languageSupported
                .getSelectionModel()
                .getSelectedItem()
                .getLocal();
    }

    @Override
    public void setValue(Object value) {
        if (languageSupported == null) {
            create();
        }
        String local = value.toString();
        if (local.equals("unavailable")) {
            languageSupported.getSelectionModel()
                    .select(new LanguageEntry("Unavailable","unavailable"));
            languageSupported.setDisable(true);
        } else {
            FXResources resources = getResources();
            Map<String,LanguageEntry> languageEntries = resources.getSupportedLanguages();
            if (languageEntries.containsKey(local)) {
                languageSupported.getSelectionModel().select(
                        resources.getSupportedLanguages().get(local)
                );
            } else {
                setValue("unavailable");
            }
        }
    }
}
