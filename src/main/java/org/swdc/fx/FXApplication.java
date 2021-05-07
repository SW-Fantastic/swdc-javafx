package org.swdc.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.swdc.config.AbstractConfig;
import org.swdc.config.Configure;
import org.swdc.dependency.AnnotationLoader;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EnvironmentLoader;
import org.swdc.dependency.application.SWApplication;
import org.swdc.dependency.layer.Layer;
import org.swdc.dependency.layer.LayerLoader;
import org.swdc.dependency.utils.AnnotationDescription;
import org.swdc.dependency.utils.AnnotationUtil;
import org.swdc.fx.config.ApplicationConfig;
import org.swdc.fx.config.ConfigFormat;
import org.swdc.fx.config.ConfigureSource;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.fx.font.MaterialIconsService;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public abstract class FXApplication extends Application implements SWApplication {

    private ThreadPoolExecutor asyncPool;

    private FXResources resources;
    private DependencyContext context;

    private static LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    @Override
    public void onConfig(EnvironmentLoader loader) {

    }

    @Override
    public void onLaunch(Layer layer) {

    }

    @Override
    public void stop() throws Exception {
        this.onShutdown(context);
        if (context instanceof Closeable){
            Closeable ctx = (Closeable) context;
            ctx.close();
        } else if (context instanceof AutoCloseable) {
            AutoCloseable ctx = (AutoCloseable) context;
            ctx.close();
        }
        ApplicationHolder.onStop(this.getClass());
        asyncPool.shutdown();
    }

    @Override
    public void start(Stage stage) throws Exception {

        SplashView view = (SplashView) resources.getSplash()
                .getConstructor(new Class[]{ FXResources.class })
                .newInstance(resources);

        Stage window = view.getSplash();
        window.show();

        AnnotationLoader loader = new AnnotationLoader();
        CompletableFuture
                .supplyAsync(() -> this.loadConfigs(loader),asyncPool)
                .thenApplyAsync(this::loadLayers,asyncPool)
                .thenApplyAsync(ctx -> this.context = ctx,asyncPool)
                .thenApply((ctx) -> {
                    Platform.runLater(() -> {
                        window.close();
                        this.onStarted(ctx);
                    });
                    return ctx;
                });
    }

    private DependencyContext loadLayers(AnnotationLoader loader) {
        DependencyContext context = loader.load();
        Layer layer = new Layer(context);
        this.onLaunch(layer);
        // 加载其他的环境层
        // FIXME 不知道为什么这里无法加载Service
        /*ServiceLoader<LayerLoader> layerLoaders = ServiceLoader.load(LayerLoader.class);
        for (LayerLoader layerLoader: layerLoaders) {
            layerLoader.setEnvironmentModule(this.getClass().getModule());
            DependencyContext ctx = layerLoader.load();
            layer.based(ctx);
        }*/
        return layer.asContext();
    }

    private AnnotationLoader loadConfigs(AnnotationLoader loader) {
        this.onConfig(loader);
        File folder = this.resources.getAssetsFolder();
        List<Class> configures = resources.getConfigures();
        for (Class conf: configures) {
            ConfigureSource source = (ConfigureSource) conf.getAnnotation(ConfigureSource.class);
            if (source == null) {
                continue;
            }
            String path = source.value();
            ConfigFormat format = source.format();
            Path configFilePath = folder.toPath().resolve(path);
            try {
                AbstractConfig config;
                if (source.external()) {
                    Constructor<? extends AbstractConfig> constructor = format.getConfigClass().getConstructor(File.class);
                    config = constructor.newInstance(configFilePath.toFile());
                } else {
                    Constructor<? extends AbstractConfig> constructor = format.getConfigClass().getConstructor(InputStream.class);
                    InputStream in = this.getClass().getModule().getResourceAsStream(path);
                    if (in != null) {
                        config = constructor.newInstance(in);
                    } else {
                        continue;
                    }
                }

                Object confInstance = conf
                        .getConstructor(Configure.class)
                        .newInstance(config);

                loader.withInstance(conf,confInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loader.withInstance(FXResources.class,resources);
        loader.withInstance(ThreadPoolExecutor.class,asyncPool);
        loader.withInstance(FXApplication.class,this);
        loader.withInstance(FontawsomeService.class,new FontawsomeService());
        loader.withInstance(MaterialIconsService.class,new MaterialIconsService());
        return loader;
    }

    @Override
    public void init() throws Exception {
        ApplicationHolder.onLaunched(this.getClass(),this);
        Map<Class, AnnotationDescription> annotations = AnnotationUtil.getAnnotations(this.getClass());
        AnnotationDescription appDesc = AnnotationUtil.findAnnotationIn(annotations,SWFXApplication.class);
        System.out.println(appDesc.getProperty(String.class,"assetsFolder"));
        Class[] configs = appDesc.getProperty(Class[].class,"configs");
        Class splash = appDesc.getProperty(Class.class,"splash");
        File file = new File(appDesc.getProperty(String.class,"assetsFolder"));

        Optional<Class> config = Stream.of(configs)
                .filter(ApplicationConfig.class::isAssignableFrom).findAny();

        if (config.isEmpty()) {
            throw new RuntimeException("请在SWFXApplication注解的configs里面添加一个继承自" +
                    "ApplicationConfig的配置类，来为应用提供Theme");
        }

        String[] icons = appDesc.getProperty(String[].class,"icons");
        List<Image> images = new ArrayList<>();
        for (String icon: icons) {
            InputStream in = this.getClass().getModule().getResourceAsStream("icons/" + icon);
            if (in == null) {
                continue;
            }
            Image iconImg = new Image(in);
            images.add(iconImg);
        }
        resources = new FXResources();
        resources.setArgs(this.getParameters().getRaw());
        resources.setDefaultConfig(config.get());
        resources.setAssetsFolder(file);
        resources.setConfigures(Arrays.asList(configs));
        resources.setSplash(splash);
        resources.setIcons(images);

        this.asyncPool = new ThreadPoolExecutor(1,3,30, TimeUnit.MINUTES,new LinkedBlockingQueue<>());

    }

    @Override
    public void onShutdown(DependencyContext context) {

    }

    public void applicationLaunch(String ...args) throws ExecutionException, InterruptedException {


        Thread launcher = new Thread(()->Application.launch(this.getClass(),args));
        launcher.start();

        FXApplication application = ApplicationHolder.getApplication(this.getClass());
        while (application == null) {
            application = ApplicationHolder.getApplication(this.getClass());
        }
    }
}
