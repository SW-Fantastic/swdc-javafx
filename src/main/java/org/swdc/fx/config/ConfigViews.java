package org.swdc.fx.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.Property;
import org.swdc.fx.FXResources;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

public class ConfigViews {


    public static ObservableList<ConfigPropertiesItem> parseConfigs(AbstractConfig config) {
        ObservableList<ConfigPropertiesItem> result = FXCollections.observableArrayList();
        Map<Property, Field> propertiesMap = config.getConfigureInfo();

        for (Map.Entry<Property,Field> ent: propertiesMap.entrySet()) {
            try {
                Field prop = ent.getValue();
                PropertyDescriptor desc = new PropertyDescriptor(prop.getName(),config.getClass());
                ConfigPropertiesItem item = new ConfigPropertiesItem(config,desc,prop);
                result.add(item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }


    public static PropertyEditor createEditor(ConfigPropertiesItem item) {
        Optional<Class<? extends PropertyEditor<?>>> editor = item.getPropertyEditorClass();
        if (editor.isEmpty()) {
            throw new RuntimeException("请为Config指定Editor" + item.getName());
        }
        try{
            Class editorClass = editor.get();
            PropEditorView view = (PropEditorView) editorClass.getConstructor(ConfigPropertiesItem.class)
                    .newInstance(item);

            return view;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Callback<PropertySheet.Item,PropertyEditor<?>> factory(FXResources resources) {
        return (item) -> {
            if (ConfigPropertiesItem.class.isAssignableFrom(item.getClass())) {
                PropertyEditor editor = createEditor((ConfigPropertiesItem) item);
                ((PropEditorView)editor).setResources(resources);
                return editor;
            }
            throw new RuntimeException("Item必须为ConfigPropertiesItem类型");
        };
    }


}
