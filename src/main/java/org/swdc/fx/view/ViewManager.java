package org.swdc.fx.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.swdc.dependency.AbstractDependencyScope;
import org.swdc.dependency.DependencyContext;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ApplicationConfig;
import org.swdc.ours.common.annotations.AnnotationDescription;
import org.swdc.ours.common.annotations.Annotations;

import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.concurrent.FutureTask;

public class ViewManager extends AbstractDependencyScope {
    
    private <T> T initialize(Class clazz,T component) {

        FXResources resources = context.getByClass(FXResources.class);
        ApplicationConfig config = context.getByClass(resources.getDefaultConfig());

        AbstractView view = (AbstractView)component;
        AnnotationDescription description = Annotations.findAnnotation(clazz,View.class);
        String fxml = description.getProperty(String.class,"viewLocation");
        if (!fxml.isBlank()) {
            try {
                InputStream inputStream = clazz.getModule().getResourceAsStream(fxml);
                if (inputStream == null){
                    throw new RuntimeException("找不到fxml：" + fxml);
                }
                // 加载fxml
                FXMLLoader loader = new FXMLLoader();
                loader.setResources(resources.getResourceBundle());
                loader.setControllerFactory(context::getByClass);
                Parent parent = loader.load(inputStream);
                view.setView(parent);

                Object ctrl = loader.getController();
                view.setController(ctrl);
                if (ctrl instanceof ViewController) {
                    ViewController controller = (ViewController) ctrl;
                    controller.setView(view);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        ResourceBundle bundle = resources.getResourceBundle();
        Boolean isStage = description.getProperty(Boolean.class,"stage");
        if (isStage) {

            Stage stage = new Stage();
            String title = description.getProperty(String.class,"title");
            stage.setTitle(title.startsWith("%") ? bundle.getString(title.substring(1)): title);
            stage.setResizable(description.getProperty(Boolean.class,"resizeable"));
            stage.initStyle(description.getProperty(StageStyle.class,"windowStyle"));
            Boolean isDialog = description.getProperty(Boolean.class,"dialog");
            stage.getIcons().addAll(resources.getIcons());
            if (isDialog) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            view.setStage(stage);
        }

        String themeName = config.getTheme();
        Theme theme = Theme.getTheme(themeName,resources.getAssetsFolder());
        theme.applyWithView(view);
        view.setTheme(theme);
        view.setContext(this.context);
        view.setDialog(description.getProperty(boolean.class,"dialog"));

        return (T)view;
    }

    @Override
    public Class getScopeType() {
        return View.class;
    }

    @Override
    public <T> T put(String name, Class clazz, T component) {
        if (!AbstractView.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("不是一个View：" + clazz.getName());
        }
        T target = null;
        if (Platform.isFxApplicationThread()){
            target = this.initialize(clazz,component);
        } else {
            try {
                FutureTask<T> task = new FutureTask<>(() -> this.initialize(clazz,component));
                Platform.runLater(task);
                target = task.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (target == null) {
            return null;
        }

        AnnotationDescription desc = Annotations.findAnnotation(clazz,View.class);
        boolean multipleViews = desc.getProperty(boolean.class,"multiple");
        if (multipleViews) {
            return target;
        }
        return super.put(name,clazz,target);
    }

    @Override
    public void setContext(DependencyContext context) {
        this.context = context;
    }

    @Override
    public <T> T getByClass(Class<T> clazz) {
        if (!AbstractView.class.isAssignableFrom(clazz)) {
            return null;
        }
        return super.getByClass(clazz);
    }


    /**
     * 创建一个不在环境中的view，
     * 如果一些特殊的需求，不允许view处于环境中,例如在应用中动态创建
     * 而不是通过注入获取。
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends AbstractView> T createView(Class<T> clazz) {
        try {
            T view = clazz.getConstructor().newInstance();
            AnnotationDescription description = Annotations.findAnnotation(clazz,View.class);
            String fxml = description.getProperty(String.class,"viewLocation");
            if (!fxml.isBlank()) {
                try {
                    InputStream inputStream = clazz.getModule().getResourceAsStream(fxml);
                    if (inputStream == null){
                        throw new RuntimeException("找不到fxml：" + fxml);
                    }
                    // 加载fxml
                    FXMLLoader loader = new FXMLLoader();
                    Parent parent = loader.load(inputStream);
                    view.setView(parent);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            Boolean isStage = description.getProperty(Boolean.class,"stage");
            if (isStage) {
                Stage stage = new Stage();
                stage.setTitle(description.getProperty(String.class,"title"));
                stage.setResizable(description.getProperty(Boolean.class,"resizeable"));
                stage.initStyle(description.getProperty(StageStyle.class,"windowStyle"));
                Boolean isDialog = description.getProperty(Boolean.class,"dialog");
                if (isDialog) {
                    stage.initModality(Modality.APPLICATION_MODAL);
                }
                view.setStage(stage);
            }

            return view;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
