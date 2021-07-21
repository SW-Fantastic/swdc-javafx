package org.swdc.fx.config.editors;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;

public class ColorEditor extends PropEditorView {

    private ColorPicker picker;
    private TextField field;
    private HBox view;

    public ColorEditor(PropertySheet.Item item) {
        super(item);
    }

    public ColorEditor(ConfigPropertiesItem item) {
        super(item);
    }

    private void create() {
        this.view = new HBox();
        this.view.setFillHeight(true);
        this.view.setSpacing(12);
        this.view.setAlignment(Pos.CENTER_LEFT);

        this.field = new TextField();
        this.field.textProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                picker.setValue(Color.web(field.getText()));
            } catch (Exception e) {
                field.setText(toRGBCode(picker.getValue()));
            }
        }));
        this.picker = new ColorPicker();
        this.picker.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(picker, Priority.ALWAYS);
        this.picker.valueProperty().addListener(((observable, oldValue, newValue) -> {
            this.field.setText(toRGBCode(this.picker.getValue()));
            this.getItem().setValue(toRGBCode(this.picker.getValue()));
        }));

        this.view.getChildren().addAll(picker,field);
    }

    @Override
    public Node getEditor() {
        if (this.view == null) {
            this.create();
        }
        return this.view;
    }

    @Override
    public Object getValue() {
        if (this.view == null) {
            this.create();
        }
        return toRGBCode(this.picker.getValue());
    }

    @Override
    public void setValue(Object o) {
        if (this.view == null) {
            this.create();
        }
        this.picker.setValue(Color.web(o.toString()));
    }

    private static String toRGBCode( Color color ) {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

}
