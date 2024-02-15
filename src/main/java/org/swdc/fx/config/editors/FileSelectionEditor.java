package org.swdc.fx.config.editors;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.glyphfont.FontAwesome;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FileSelectionEditor extends PropEditorView {

    private ComboBox<Path> comboBox;
    private HBox view;

    public FileSelectionEditor(PropertySheet.Item item) {
        super(item);
    }

    public FileSelectionEditor(ConfigPropertiesItem item) {
        super(item);
    }

    private void resolveData() {
        String path = this.getItem().getEditorInfo().resource();
        Path target = this.getResources().getAssetsFolder().toPath().resolve(path);
        try {
            List<Path> files = Files.list(target)
                    .filter(p -> !Files.isDirectory(p))
                    .collect(Collectors.toList());
            this.comboBox.getItems().clear();
            this.comboBox.getItems().addAll(files);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void create(){
        this.view = new HBox();
        this.comboBox = new ComboBox<>();
        this.comboBox.setMaxWidth(Double.MAX_VALUE);
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (comboBox.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            String name = comboBox.getSelectionModel().getSelectedItem().getFileName().toString();
            this.getItem().setValue(name);
        }));
        comboBox.setConverter(new StringConverter<Path>() {
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
                return getResources().getAssetsFolder().toPath().resolve(string);
            }
        });

        ResourceBundle bundle = getResources().getResourceBundle();

        HBox.setHgrow(comboBox,Priority.ALWAYS);
        Button button = new Button();
        button.setText("导入");
        if (bundle != null) {
            button.setText(bundle.getString("app.import"));
        }
        button.setOnAction(e -> {
            System.err.println(comboBox.getSelectionModel().getSelectedItem());
            String path = this.getItem().getEditorInfo().resource();
            Path target = this.getResources().getAssetsFolder().toPath().resolve(path);

            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(null);

            try {

                if (file == null) {
                    return;
                }

                Path saveTo = target.resolve(file.getName());
                FileInputStream fin = new FileInputStream(file);
                OutputStream out = Files.newOutputStream(saveTo);
                fin.transferTo(out);
                this.resolveData();

            } catch (Exception ex) {
                throw new RuntimeException("无法导入文件：",ex);
            }

        });
        this.view.getChildren().addAll(this.comboBox,button);
        this.view.setSpacing(12);
    }

    @Override
    public Node getEditor() {
        if (this.view == null) {
            this.create();
            this.resolveData();
        }
        return view;
    }

    @Override
    public Object getValue() {
        if (this.view == null) {
            create();
            this.resolveData();
        }
        return comboBox.getSelectionModel().getSelectedItem().getFileName();
    }

    @Override
    public void setValue(Object o) {
        if (this.view == null) {
            create();
            this.resolveData();
        }
        for (Path p: this.comboBox.getItems()) {
            if (p.getFileName().toString().equals(o.toString())) {
                this.comboBox.getSelectionModel().select(p);
                break;
            }
        }
    }
}
