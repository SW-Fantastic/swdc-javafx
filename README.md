# Application-FX

配合本系列的应用环境使用的JavaFX应用基础框架。

目前正在开发中，我将会使用此框架替换原有的JavaFX使用的
基础框架支持，此版本的框架将javafx的GUI和DI环境进行了分离，
FX单纯用于GUI。

## 快速开始

首先，请clone本项目依赖的其他项目以及本项目， 并且安装到maven的本地仓库中,
请在合适的位置运行下面的脚本，它将会安装本项目和依赖的项目。

```bash
mkdir swdc-projects
git clone https://github.com/SW-Fantastic/swdc-dependency.git ./swdc-projects/swdc-dependency
cd swdc-projects/swdc-dependency
mvn install -Dmaven.test.skip=true
cd ../../

git clone https://github.com/SW-Fantastic/swdc-configure.git ./swdc-projects/swdc-configure
cd swdc-projects/swdc-configure
mvn install -Dmaven.test.skip=true
cd ../../

git clone https://github.com/SW-Fantastic/application-db.git ./swdc-projects/application-db
cd swdc-projects/application-db
mvn install -Dmaven.test.skip=true
cd ../../

git clone https://github.com/SW-Fantastic/swdc-javafx.git ./swdc-projects/swdc-javafx
cd swdc-projects/swdc-javafx
mvn install -Dmaven.test.skip=true
cd ../../
```
你也可以手动安装相关的依赖项目到maven的本地仓库，记得使用前及时更新它们，
目前我不会手动发布release，在使用前通过maven重新编译安装是必要的。

## 创建第一个项目

在确保了上述的各项目已经顺利安装的情况下，你可以通过任何你喜欢的方式创建一个Maven空白项目，然后添加以下内容到它的 pom中：

```xml
    <properties>
        <!-- 本项目使用UTF-8作为默认编码，如果你在使用国际化功能，
            请自行修改properties的编码格式以免乱码 -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <encoding>UTF-8</encoding>
        <!-- 本项目基于Java11，更高的版本是可以运行的 -->
        <java.version>11</java.version>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- 这个是依赖注入容器（IOC/DI），必须添加 -->
        <dependency>
            <groupId>org.swdc</groupId>
            <artifactId>application-component</artifactId>
            <version>1.3-SNAPSHOT</version>
        </dependency>
        <!-- 这个是本项目的maven依赖，基于javafx的应用框架，必须添加 -->
        <dependency>
            <groupId>org.swdc</groupId>
            <artifactId>application-fx</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <!-- 这个是本项目的配置文件处理器，必须添加 -->
        <dependency>
            <groupId>org.swdc</groupId>
            <artifactId>application-configure</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```
接下来需要项目添加必要的文件和文件夹：
```text

Project Folder
|
|——assets（资源文件夹，包含主题的样式，应用的配置文件）
|   |——config.json
|   |——skin
|       |——default
|           |——stage.less
|           |——font.ttf
|   
|——src
    |——main
        |——java
        |   |——module-info.java （本框架基于JPMS，必须使用module-info）
        |   >> Other classes
        |
        |——resource
            |——icons（应用的窗口图标，根据需要添加，不用每一种尺寸都有）
            |   |——icon_16.png
            |   |——icon_24.png
            |   |——icon_32.png
            |   |——icon_64.png
            |   |——icon_128.png
            |   |——icon_256.png
            |   |——icon_512.png
            |
            |——views（存放FXML的位置，可以使用SceneBuilder编辑它们）
            |   |——MainView.fxml
            |
            |——lang（可以没有，如果不做国际化请忽略，文件名是string_语言代码.properties）
                |——string_zh.properties
                |——string_en.properties
```

module-info.java的内容如下：
```java
module your.app.name {
    
    // 这些功能都已经集成在本项目和本项目依赖的项目
    // 中，不需要额外添加就能够直接使用。
    // 请require它们。
    requires swdc.application.fx;
    requires swdc.application.dependency;
    requires swdc.application.configs;

    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.desktop;
    requires jakarta.annotation;
    requires jakarta.inject;
    requires org.slf4j;

    // 向这些模块公开你的FXMLView类，
    // JavaFX使用的Controller类
    // 只要你需要依赖注入和JavaFX，都需要把对应的Package
    // 对下面的package开放，它们需要深度反射的权限来实现对应的功能。
    opens your.packages to
            swdc.application.dependency,
            swdc.application.fx,
            swdc.application.configs,
            javafx.graphics,
            javafx.controls;
    
    
    opens views; // 公开fxml在resource的package
    opens icons; // 公开icons在resource的package
    opens langs; // 公开langs，如果你在使用国际化请添加这个。
}
```
为工程添加Splash（暂时你必须有一个Splash），
添加一个类，让它继承`org.swdc.fx.SwingSplashView`，例如这样：
```java
public class SplashScene extends SwingSplashView {

    private JWindow splash;

    public SplashScene(FXResources resources) {
        super(resources);
    }

    @Override
    public JWindow getSplash() {
        if (splash != null) {
            return splash;
        }
        JWindow window = new JWindow();
        window.setBackground(new Color(0,0,0,0));
        ImageIcon image = new ImageIcon();

        try (InputStream in = new FileInputStream(resources.getAssetsFolder().getAbsolutePath() + File.separator +"splash.png")){
            image.setImage(ImageIO.read(in));
            JLabel imgLab = new JLabel(image);
            window.setContentPane(imgLab);
            window.setSize(image.getIconWidth(),image.getIconHeight());
            window.setLocationRelativeTo(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.splash = window;
        return window;
    }

}
```
FXResource在应用启动后就会被初始化的，可以安全的在各处注入和使用，
其中的getAssetFolder所获取的目录就是工程目录中的`asset`目录。
因此，上面的Demo需要在asset中存在一个`splash.png`文件。


接下来，请继承`org.swdc.fx.config.ApplicationConfig`并且为它标注此注解：
`org.swdc.config.annotations.ConfigureSource`。

```java
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.configs.JsonConfigHandler;
import org.swdc.fx.config.ApplicationConfig;

@ConfigureSource(value = "assets/config.json", handler = JsonConfigHandler.class)
public class YourAppConfig extends ApplicationConfig {
}
```

`ConfigureSource`的value属性允许你选择配置文件的路径，它不一定是assets目录内的文件，
并且asset的名称和位置是可配置的，但是它最好处于asset目录，且被填写为以工程目录为根目录的相对路径。

`ConfigureSource`的handler属性允许你选择配置文件的处理器，这里使用的是`JsonConfigHandler`
并且配合json文件来存储配置，除此之外，还支持yaml，xml，properties三种配置文件格式，请填写对应的handler
来配合这些格式的文件来使用。

但是无论是哪一种配置文件，继承自`ApplicationConfig`的配置，必须含有`language`和`theme`

如果需要操作其他配置文件，可以通过继承`org.swdc.fx.config.AbstractConfig`进行操作，
`ApplicationConfig`在每一个JavaFX应用程序仅需要一个。

接下来为应用添加应用程序主类：

```java
@SWFXApplication(
        // 这里是Asset目录相对于工程目录的路径
        assetsFolder = "./assets",
        // 指定启动的Splash类
        splash = SplashScene.class,
        // 指定启动的ApplicationConfig
        configs = { YourAppConfig.class },
        // 指定应用的图标集
        icons = { 
                "icon_16.png",
                "icon_24.png",
                "icon_32.png",
                "icon_64.png",
                "icon_128.png",
                "icon_256.png",
                "icon_512.png" 
        })
public class YourApplication extends FXApplication {


    @Override
    public void onStarted(DependencyContext dependencyContext) {
        // 这里应用已经就绪了，通过dependencyContext读取需要的view来显示主界面就可以了
        // MainView view = dependencyContext.getByClass(MainView.class);
        // view.show();
    }

}

```

```java
public class Launcher {

    public static void main(String[] args) {
        // 通过JavaFX的launch方法可以直接启动应用。
        // Application.launch(YourApplication.class,args);
        // 但是更加建议通过如下方式启动它。
        YourApplication app = new YourApplication();
        app.applicationLaunch(args);
    }

}

```

如何创建一个View：

继承`org.swdc.fx.view.AbstractView`，并且标注`org.swdc.fx.view.View`注解。

View注解中，通过viewLocation提供fxml的位置，默认view是一个窗口，因此包含Stage，通过
把它的stage置为false可以不创建窗口，如果需要在title使用国际化，请在key的开头添加“%”。

如果需要在view创建完毕后进行初始化，可以使用PostConstruct，并且AbstractView提供了
findById以方便查找fxml的各个组件。

View可以不是单例的，但是如果非单例的FXML指定了Controller，Controller需要使用Prototype注解，
Controller可以通过实现`org.swdc.fx.view.ViewController`接口来进行初始化。

# 关于

这里介绍比较简略，目前作者还没有足够的时间完成文档，仅仅作为一个项目的基本参考用于说明本项目的意图。