package org.swdc.fx.config;

import org.controlsfx.property.BeanProperty;
import org.controlsfx.property.editor.PropertyEditor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
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

    public Field getProp() {
        return prop;
    }

    public PropEditor getEditorInfo() {
        return this.editor;
    }


}
