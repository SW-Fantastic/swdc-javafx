package org.swdc.fx.config;

import org.swdc.config.AbstractConfigure;
import org.swdc.config.Configure;
import org.swdc.config.annotations.Property;

public abstract class ApplicationConfig extends AbstractConfigure {

    @Property("theme")
    private String theme;

    /**
     * 初始化配置
     *
     * @param configure 配置对象
     */
    public ApplicationConfig(Configure configure) {
        super(configure);
    }


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
