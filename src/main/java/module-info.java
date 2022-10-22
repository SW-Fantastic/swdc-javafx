module swdc.application.fx {

    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.web;
    requires javafx.swing;
    requires jakarta.inject;
    requires lesscss.engine;
    requires org.slf4j;
    requires swdc.application.dependency;
    requires swdc.application.configs;
    requires org.controlsfx.controls;
    requires java.desktop;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    exports org.swdc.fx.config;
    exports org.swdc.fx.config.editors;
    exports org.swdc.fx.view;
    exports org.swdc.fx;
    exports org.swdc.fx.font;

    opens fonts;
    opens banner;

    opens org.swdc.fx.config to swdc.application.configs;

}