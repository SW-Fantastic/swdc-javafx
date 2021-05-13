module swdc.application.fx {

    requires swdc.application.configs;
    requires swdc.application.dependency;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.web;
    requires jakarta.inject;
    requires lesscss.engine;
    requires slf4j.api;

    exports org.swdc.fx.config;
    exports org.swdc.fx.view;
    exports org.swdc.fx;
    exports org.swdc.fx.font;

    opens fonts;
    opens banner;

    opens org.swdc.fx.config to swdc.application.configs;

}