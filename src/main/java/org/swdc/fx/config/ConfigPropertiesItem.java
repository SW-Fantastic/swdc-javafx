package org.swdc.fx.config;

import org.controlsfx.property.BeanProperty;
import org.controlsfx.property.editor.PropertyEditor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConfigPropertiesItem extends BeanProperty {

    private Field prop;
    private PropEditor editor;
    private ResourceBundle bundle;

    public ConfigPropertiesItem(Object bean, PropertyDescriptor propertyDescriptor, Field field,ResourceBundle bundle) {
        super(bean, propertyDescriptor);
        this.prop = field;
        this.bundle = bundle;
        try {
            this.editor = prop.getAnnotation(PropEditor.class);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return this.editor.name().startsWith("%") ?
                bundle.getString(this.editor.name().substring(1)) :
                this.editor.name();
    }

    @Override
    public String getDescription() {
        return this.editor.description().startsWith("%") ?
                bundle.getString(this.editor.description().substring(1)):
                this.editor.description();
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
