package org.swdc.fx;

import javafx.scene.image.Image;
import org.swdc.fx.config.ApplicationConfig;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class FXResources {

    private File assetsFolder;

    private Class splash;

    private List<Class> configures;

    private List<Image> icons;

    private List<String> args;

    private ThreadPoolExecutor executor;

    private Class<? extends ApplicationConfig> defaultConfig;

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
}
