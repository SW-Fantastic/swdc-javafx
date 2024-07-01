package org.swdc.fx.config.editors;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件夹选择。
 * 在本Application的Asset文件夹下，
 * 在PropEditor的resources属性指定的文件夹的内部，
 * 可以在这里选择一个文件夹。
 *
 * 主要是选择内部的文件，如果想要在应用外部选择文件夹，请使用ExternalFolderSelectEditor
 *
 * 此编辑器用于选择主题，当然也可以选择其他文件夹。
 *
 */
public class FolderSelectEditor extends PropEditorView {


    private ComboBox<Path> cbx;

    public FolderSelectEditor(PropertySheet.Item item) {
        super(item);
    }

    public FolderSelectEditor(ConfigPropertiesItem item) {
        super(item);
    }

    @Override
    public Node getEditor() {
        this.refreshFolders();
        return cbx;
    }

    private void refreshFolders() {
        String path = getItem().getEditorInfo().resource();
        FXResources resources = getResources();
        Path assets = resources.getAssetsFolder().toPath();

        try {

            Path selectingFolderPath = assets.resolve(path);
            List<Path> folders = Files.list(selectingFolderPath)
                    .filter(Files::isDirectory)
                    .map(p -> getResources().getAssetsFolder().getAbsoluteFile().toPath().resolve(p))
                    .collect(Collectors.toList());
            if (this.cbx == null) {
                this.cbx = new ComboBox<>();
                this.cbx.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        return;
                    }
                    getItem().setValue(newValue.getFileName().toString());
                }));
                this.cbx.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Path object) {
                        if (object == null) {
                            return null;
                        }
                        return object.getFileName().toString();
                    }

                    @Override
                    public Path fromString(String string) {
                        if (string == null) {
                            return null;
                        }
                        Path target = selectingFolderPath
                                .resolve(string);
                        return target.getFileName();
                    }
                });
            }
            cbx.getItems().clear();
            cbx.getItems().addAll(folders);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getValue() {
        this.refreshFolders();
        return cbx.getSelectionModel()
                .getSelectedItem()
                .getFileName()
                .toString();
    }

    @Override
    public void setValue(Object o) {
        this.refreshFolders();
        Path target = getResources().getAssetsFolder().getAbsoluteFile()
                .toPath()
                .resolve(this.getItem().getEditorInfo().resource())
                .resolve(o.toString());

        if (Files.exists(target)) {
            this.cbx.getSelectionModel().select(target);
        }
    }
}

