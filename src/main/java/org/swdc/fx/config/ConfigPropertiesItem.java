package org.swdc.fx.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.property.BeanProperty;
import org.controlsfx.property.editor.PropertyEditor;
import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.Property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class ConfigPropertiesItem extends BeanProperty {

    private Field prop;
    private PropEditor editor;

    public ConfigPropertiesItem(Object bean, PropertyDescriptor propertyDescriptor, Field field) {
        super(bean, propertyDescriptor);
        this.prop = field;
        try {
            this.editor = prop.getAnnotation(PropEditor.class);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return this.editor.name();
    }

    @Override
    public String getDescription() {
        return this.editor.description();
    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(this.editor.editor());
    }


}
