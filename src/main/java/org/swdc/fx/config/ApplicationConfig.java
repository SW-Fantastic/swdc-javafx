package org.swdc.fx.config;

import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.Property;
import org.swdc.fx.config.editors.FolderSelectEditor;

public abstract class ApplicationConfig extends AbstractConfig {

    @Property("theme")
    @PropEditor(
            editor = FolderSelectEditor.class,
            name = "主题",
            description = "应用的主题风格。",
            resource = "skin"
    )
    private String theme;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
