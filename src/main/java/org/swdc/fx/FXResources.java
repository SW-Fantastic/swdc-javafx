package org.swdc.fx;

import javafx.scene.image.Image;
import org.swdc.fx.config.ApplicationConfig;
import org.swdc.fx.config.LanguageEntry;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

public class FXResources {

    private File assetsFolder;

    private Class splash;

    private List<Class> configures;

    private List<Image> icons;

    private List<String> args;

    private ThreadPoolExecutor executor;

    private Class<? extends ApplicationConfig> defaultConfig;

    private Map<String,LanguageEntry> supportedLanguages;

    private MultipleSourceResourceBundle resourceBundle;

    private Locale locale = Locale.getDefault();

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public void setDefaultConfig(Class<? extends ApplicationConfig> defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Class<? extends ApplicationConfig> getDefaultConfig() {
        return defaultConfig;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public Class getSplash() {
        return splash;
    }

    public File getAssetsFolder() {
        return assetsFolder;
    }

    public List<Class> getConfigures() {
        return configures;
    }

    public List<Image> getIcons() {
        return icons;
    }

    public void setAssetsFolder(File assetsFolder) {
        this.assetsFolder = assetsFolder;
    }

    public void setConfigures(List<Class> configures) {
        this.configures = configures;
    }

    public void setIcons(List<Image> icons) {
        this.icons = icons;
    }

    public void setSplash(Class splash) {
        this.splash = splash;
    }

    public MultipleSourceResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setSupportedLanguages(Map<String, LanguageEntry> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public Map<String, LanguageEntry> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setResourceBundle(MultipleSourceResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
}
