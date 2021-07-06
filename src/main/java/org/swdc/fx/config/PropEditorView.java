package org.swdc.fx.config;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

/**
 * View，这个是显示配置或者属性编辑器的那个view，
 * 需要注意的是，setValue和getValue之前需要首先检查
 * ui组件是否存在，这两个方法可以先于getEditor执行。
 */
public abstract class PropEditorView implements PropertyEditor {

    private ConfigPropertiesItem item;

    public PropEditorView(PropertySheet.Item item) {
        if (!ConfigPropertiesItem.class.isAssignableFrom(item.getClass())) {
            throw new RuntimeException("item 必须为ConfigPropertiesItem类的对象。");
        }
        this.item = (ConfigPropertiesItem) item;
    }

    public PropEditorView(ConfigPropertiesItem item) {
        this.item = item;
    }

    public ConfigPropertiesItem getItem() {
        return item;
    }


}
