package org.swdc.fx.config;

import org.swdc.config.AbstractConfig;
import org.swdc.config.configs.JsonConfig;
import org.swdc.config.configs.PropertiesConfig;
import org.swdc.config.configs.XmlConfig;
import org.swdc.config.configs.YamlConfig;

public enum ConfigFormat {

    PROPERTIES(PropertiesConfig.class),
    YAML(YamlConfig.class),
    JSON(JsonConfig.class),
    XML(XmlConfig.class);

    Class<? extends AbstractConfig> targetResolver;

    ConfigFormat(Class<? extends AbstractConfig> targetResolver) {
        this.targetResolver = targetResolver;
    }

    public Class<? extends AbstractConfig> getConfigClass() {
        return targetResolver;
    }
}
