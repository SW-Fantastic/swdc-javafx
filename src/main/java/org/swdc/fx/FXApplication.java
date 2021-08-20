package org.swdc.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.config.AbstractConfig;
import org.swdc.dependency.AnnotationLoader;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EnvironmentLoader;
import org.swdc.dependency.LoggerProvider;
import org.swdc.dependency.application.SWApplication;
import org.swdc.dependency.utils.AnnotationDescription;
import org.swdc.dependency.utils.AnnotationUtil;
import org.swdc.fx.config.ApplicationConfig;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.util.ApplicationIOUtil;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Stream;

public abstract class FXApplication extends Application implements SWApplication {

    private ThreadPoolExecutor asyncPool;

    private FXResources resources;
    private DependencyContext context;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    @Override
    public void onConfig(EnvironmentLoader loader) {

    }


    @Override
    public void stop() throws Exception {
        logger.info(" application closing...");

        synchronized (this) {
            while (asyncPool.getActiveCount() > 0) {
                logger.info("waiting for threads complete...");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (FXApplication.this) {
                            if (asyncPool.getActiveCount() == 0) {
                                FXApplication.this.notify();
                            }
                        }
                    }
                },5000);
                this.wait();
                timer.cancel();
            }
        }

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
        logger.info(" application has been shutdown");
        System.exit(0);
    }

    @Override
    public void start(Stage stage) throws Exception {

        logger.info(" load splash stage");

        Splash view = (Splash) resources.getSplash()
                .getConstructor(new Class[]{ FXResources.class })
                .newInstance(resources);

        view.show();

        AnnotationLoader loader = new AnnotationLoader();
        CompletableFuture
                .supplyAsync(() -> this.loadConfigs(loader),asyncPool)
                .thenApplyAsync(ctx -> this.context = ctx.load(),asyncPool)
                .thenApply((ctx) -> {
                    Platform.runLater(() -> {
                        logger.info(" application ready.");
                        this.onStarted(ctx);
                        view.hide();
                        logger.info(" application started.");
                    });
                    return ctx;
                });
    }


    private AnnotationLoader loadConfigs(AnnotationLoader loader) {
        this.onConfig(loader);
        List<Class> configures = resources.getConfigures();
        for (Class conf: configures) {
            try {
                AbstractConfig confInstance = (AbstractConfig) conf.getConstructor().newInstance();
                loader.withInstance(conf,confInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        logger.info(" config loaded.");
        loader.withProvider(LoggerProvider.class);
        loader.withInstance(FXResources.class,resources);
        loader.withInstance(ThreadPoolExecutor.class,asyncPool);
        loader.withInstance(FXApplication.class,this);
        loader.withInstance(FontawsomeService.class,new FontawsomeService());
        loader.withInstance(MaterialIconsService.class,new MaterialIconsService());
        return loader;
    }

    @Override
    public void init() throws Exception {

        InputStream bannerInput = this.getClass().getModule().getResourceAsStream("banner/banner.txt");
        if (bannerInput == null) {
            bannerInput = FXApplication.class.getModule().getResourceAsStream("banner/banner.txt");
        }
        String banner = ApplicationIOUtil.readStreamAsString(bannerInput);
        System.out.println(banner);
        bannerInput.close();
        logger.info(" Application initializing..");

        ApplicationHolder.onLaunched(this.getClass(),this);
        Map<Class, AnnotationDescription> annotations = AnnotationUtil.getAnnotations(this.getClass());
        AnnotationDescription appDesc = AnnotationUtil.findAnnotationIn(annotations,SWFXApplication.class);
        logger.info(" using assets: " + appDesc.getProperty(String.class,"assetsFolder"));
        Class[] configs = appDesc.getProperty(Class[].class,"configs");
        Class splash = appDesc.getProperty(Class.class,"splash");

        File file = null;
        String osName = System.getProperty("os.name").trim().toLowerCase();
        logger.info(" starting at : " + osName);
        if (osName.contains("mac")) {
            String url = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            String base = URLDecoder.decode(url, StandardCharsets.UTF_8);
            if (base.indexOf(".app") > 0) {
                // 位于MacOS的Bundle（.app软件包）内部，特殊处理以获取正确的路径。
                String location = base.substring(0,base.indexOf(".app")) + ".app/Contents/";
                Path target = new File(location).toPath();
                target = target.resolve(appDesc.getProperty(String.class,"assetsFolder"));
                file = target.toFile();
            }
        } else {
            file = new File(appDesc.getProperty(String.class,"assetsFolder"));
        }

        logger.info(" dependency environment loading...");
        Optional<Class> config = Stream.of(configs)
                .filter(ApplicationConfig.class::isAssignableFrom).findAny();

        if (config.isEmpty()) {
            RuntimeException ex = new RuntimeException("请在SWFXApplication注解的configs里面添加一个继承自" +
                    "ApplicationConfig的配置类，来为应用提供Theme");
            logger.error(" 启动失败",ex);
            throw ex;
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

        this.asyncPool = new ThreadPoolExecutor(4,12,30, TimeUnit.MINUTES,new LinkedBlockingQueue<>());

        resources.setExecutor(asyncPool);
        logger.info(" javafx initializing...");
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
