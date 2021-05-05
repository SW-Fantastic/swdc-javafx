package org.swdc.fx.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.DependencyScope;
import org.swdc.dependency.utils.AnnotationDescription;
import org.swdc.dependency.utils.AnnotationUtil;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ApplicationConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class ViewManager implements DependencyScope {
    
    private Map<Class,List<AbstractView>> views = new ConcurrentHashMap<>();
    private Map<String, AbstractView> namedViews = new ConcurrentHashMap<>();
    private Map<Class, List<AbstractView>> abstractViews = new ConcurrentHashMap<>();

    private DependencyContext context;

    private <T> T initialize(Class clazz,T component) {
        AbstractView view = (AbstractView)component;
        AnnotationDescription description = AnnotationUtil.findAnnotation(clazz,View.class);
        String fxml = description.getProperty(String.class,"viewLocation");
        if (!fxml.isBlank()) {
            try {
                InputStream inputStream = clazz.getModule().getResourceAsStream(fxml);
                if (inputStream == null){
                    throw new RuntimeException("找不到fxml：" + fxml);
                }
                // 加载fxml
                FXMLLoader loader = new FXMLLoader();
                loader.setControllerFactory(context::getByClass);
                Parent parent = loader.load(inputStream);
                view.setView(parent);
                view.setController(loader.getController());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 应用theme
        FXResources resources = context.getByClass(FXResources.class);
        ApplicationConfig config = context.getByClass(resources.getDefaultConfig());
        String themeName = config.getTheme();

        Theme theme = new Theme(themeName,resources.getAssetsFolder());
        theme.applyWithView(view);

        boolean isCell = description.getProperty(boolean.class,"cell");
        if (!isCell) {
            Stage stage = new Stage();
            stage.setTitle(description.getProperty(String.class,"title"));
            stage.setResizable(description.getProperty(boolean.class,"resizeable"));
            stage.initStyle(description.getProperty(StageStyle.class,"windowStyle"));
            boolean isDialog = description.getProperty(boolean.class,"dialog");
            Class parent = description.getProperty(Class.class,"dialogParent");
            if (!parent.equals(Object.class) && isDialog) {
                AbstractView parentView = (AbstractView) this.context.getByClass(parent);
                if (parentView != null) {
                    stage.initOwner(parentView.getStage());
                    stage.initModality(Modality.APPLICATION_MODAL);
                }
            }
            view.setStage(stage);
        }


        return (T)view;
    }

    @Override
    public Class getScopeType() {
        return View.class;
    }

    @Override
    public <T> T put(String name, Class clazz, Class multiple, T component) {
        AbstractView view = (AbstractView) this.put(name,clazz,component);
        if (multiple != null) {
            List<AbstractView> list = abstractViews.getOrDefault(multiple,new ArrayList<>());
            list.add(view);
            abstractViews.put(multiple,list);
        }
        return (T)view;
    }

    @Override
    public <T> T put(String name, Class clazz, T component) {
        if (!AbstractView.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("不是一个View：" + clazz.getName());
        }
        FutureTask<T> task = new FutureTask<>(() -> this.initialize(clazz,component));
        try {
            Platform.runLater(task);
            T target = task.get();
            List<AbstractView> list = views.getOrDefault(clazz,new ArrayList<>());
            list.add((AbstractView) target);
            views.put(clazz,list);

            if (!name.equals(clazz.getName())) {
                namedViews.put(name,(AbstractView) component);
            }
            return target;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setContext(DependencyContext context) {
        this.context = context;
    }

    @Override
    public <T> T getByClass(Class<T> clazz) {
        return (T)views.get(clazz);
    }

    @Override
    public <T> T getByName(String name) {
        return (T)namedViews.get(name);
    }

    @Override
    public <T> List<T> getByAbstract(Class<T> parent) {
        return (List<T>) abstractViews.get(parent);
    }

    @Override
    public List<Object> getAllComponent() {
        return views.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
