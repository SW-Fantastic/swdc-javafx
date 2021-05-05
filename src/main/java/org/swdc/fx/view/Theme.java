package org.swdc.fx.view;

import com.asual.lesscss.LessEngine;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.swdc.dependency.utils.AnnotationDescription;
import org.swdc.dependency.utils.AnnotationUtil;

import java.io.File;
import java.util.List;

public class Theme {

    private String name;

    private File assetsRoot;

    public Theme(String name, File assets) {
        this.name = name;
        this.assetsRoot = assets;
    }

    /**
     * 编译主题的less
     */
    private void prepare() {
        File themeFolder = assetsRoot.toPath()
                .resolve("skin")
                .resolve(this.name)
                .toFile();
        if (!themeFolder.exists()) {
            throw new RuntimeException("样式的文件夹不存在：" + themeFolder.getAbsolutePath());
        }
        File[] files = themeFolder.listFiles();
        if (files == null) {
            throw new RuntimeException("样式文件夹是空的：" + themeFolder.getAbsolutePath());
        }
        try {
            for (File file: files) {
                if (file.isFile() && file.getName().endsWith("less")) {
                    String cssName = file.getName().replace("less", "css");
                    File css = new File(file.getParent() + File.separator + cssName);
                    if (css.exists()) {
                        css.delete();
                    }
                    LessEngine lessEngine = new LessEngine();
                    lessEngine.compile(file,css);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("less编译失败：",e);
        }
    }

    /**
     * 给view添加样式
     * @param view
     */
    public void applyWithView(AbstractView view) {
        this.prepare();

        File skinAssets = assetsRoot.toPath()
                .resolve("skin")
                .resolve(this.name)
                .toFile();

        AnnotationDescription desc = AnnotationUtil.findAnnotation(view.getClass(),View.class);
        List<String> stylesList = null;
        Node root = null;

        Stage stage = view.getStage();
        try {
            String defaultStyle = skinAssets
                    .toPath()
                    .resolve("stage.css")
                    .toUri()
                    .toURL()
                    .toExternalForm();

            if (stage != null) {
                root = stage.getScene().getRoot();
                stylesList = stage.getScene().getStylesheets();
            } else {
                Node node = view.getView();
                if (node instanceof Parent) {
                    root = node;
                    stylesList = ((Parent)node).getStylesheets();
                }
            }

            if (stylesList == null) {
                return;
            }

            String background = desc.getProperty(String.class,"background");
            String backgroundUri = null;
            if (!background.isBlank()) {
                backgroundUri = skinAssets.toPath().resolve("images")
                        .resolve(background)
                        .toUri()
                        .toURL()
                        .toExternalForm();
            }

            stylesList.add(defaultStyle);

            String[] additionalStyleSheets = desc.getProperty(String[].class,"css");
            for (String styleName: additionalStyleSheets) {
                String styleUri = skinAssets
                        .toPath()
                        .resolve(styleName)
                        .toUri()
                        .toURL()
                        .toExternalForm();
                stylesList.add(styleUri);
            }
            root.setStyle("-fx-background-image: url(" + backgroundUri + ");" );
        } catch (Exception e){
            throw new RuntimeException("渲染出现异常：",e);
        }
    }

}
