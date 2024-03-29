package org.swdc.fx.config;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.property.editor.PropertyEditor;
import org.swdc.fx.FXResources;
import org.swdc.ours.common.type.Converters;

/**
 * View，这个是显示配置或者属性编辑器的那个view，
 * 需要注意的是，setValue和getValue之前需要首先检查
 * ui组件是否存在，这两个方法可以先于getEditor执行。
 */
public abstract class PropEditorView implements PropertyEditor {

    protected static FontAwesome fontAwesome = new FontAwesome();

    protected Converters converters = new Converters();

    private ConfigPropertiesItem item;

    private FXResources resources;

    public PropEditorView(PropertySheet.Item item) {
        if (!ConfigPropertiesItem.class.isAssignableFrom(item.getClass())) {
            throw new RuntimeException("item 必须为ConfigPropertiesItem类的对象。");
        }
        this.item = (ConfigPropertiesItem) item;
    }

    public void setResources(FXResources resources) {
        this.resources = resources;
    }

    public FXResources getResources() {
        return resources;
    }

    public PropEditorView(ConfigPropertiesItem item) {
        this.item = item;
    }

    public ConfigPropertiesItem getItem() {
        return item;
    }


}
