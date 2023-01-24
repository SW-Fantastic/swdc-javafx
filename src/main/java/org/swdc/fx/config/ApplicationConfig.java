package org.swdc.fx.config;

import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.Property;
import org.swdc.fx.config.editors.FileSelectionEditor;
import org.swdc.fx.config.editors.FolderSelectEditor;
import org.swdc.fx.config.editors.LanguageSelectionEditor;
import org.swdc.fx.config.editors.SelectionEditor;

public abstract class ApplicationConfig extends AbstractConfig {

    @Property("theme")
    @PropEditor(
            editor = FolderSelectEditor.class,
            name = "%app.theme.name",
            description = "%app.theme.desc",
            resource = "skin"
    )
    private String theme;

    @Property("language")
    @PropEditor(
            editor = LanguageSelectionEditor.class,
            name = "%app.language.name",
            description = "%app.language.desc",
            resource = "%app.languages"
    )
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
