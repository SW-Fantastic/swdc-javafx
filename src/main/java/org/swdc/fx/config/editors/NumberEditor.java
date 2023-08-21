package org.swdc.fx.config.editors;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;
import org.swdc.ours.common.type.Converter;

import java.lang.reflect.Field;

public class NumberEditor extends PropEditorView {

    private HBox view;
    private TextField  field;
    private Slider slider;

    public NumberEditor(PropertySheet.Item item) {
        super(item);
    }

    public NumberEditor(ConfigPropertiesItem item) {
        super(item);
    }

    public void create() {
        this.view = new HBox();
        this.field = new TextField();
        this.slider = new Slider();

        String[] ranges = null;
        String resource = this.getItem().getEditorInfo().resource();
        if (resource.contains(",")) {
            ranges =  resource.split(",");
        } else if (resource.contains("-")) {
            ranges = resource.split("-");
        } else {
            throw new RuntimeException("resources内应该填写数字的范围，并以”,“或者”-“作为 分隔符连接。");
        }
        double min = Double.parseDouble(ranges[0]);
        double max = Double.parseDouble(ranges[1]);

        slider.setMax(max);
        slider.setMin(min);
        view.setFillHeight(true);
        view.setAlignment(Pos.CENTER_LEFT);
        view.setSpacing(12);
        view.getChildren().add(slider);
        view.getChildren().add(field);
        slider.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(slider, Priority.ALWAYS);

        slider.valueProperty().addListener(((observable, oldValue, newValue) ->   {
            double value = slider.getValue();
            field.setText(value  + "");
            Field field = this.getItem().getProp();
            if (field.getType() != double.class) {
                Converter conv = converters.getConverter(double.class,field.getType());
                if (conv != null) {
                    this.getItem().setValue(conv.convert(value));
                }
            } else {
                this.getItem().setValue(value);
            }
        }));

        field.textProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                this.slider.setValue(Double.valueOf(field.getText()));
            } catch (Exception e){
                this.field.setText(this.slider.getValue() + "");
            }
        }));

    }

    @Override
    public Node getEditor() {
        if (this.view == null){
            this.create();
        }
        return this.view;
    }

    @Override
    public Object getValue() {
        if (this.view == null){
            this.create();
        }
        return this.slider.getValue();
    }

    @Override
    public void setValue(Object o) {
        if (this.view == null){
            this.create();
        }
        this.slider.setValue(Double.valueOf(o.toString()));
    }
}
