module swdc.application.fx {

    requires transitive javafx.graphics;
    requires transitive javafx.fxml;
    requires transitive javafx.controls;
    requires transitive javafx.web;
    requires transitive javafx.swing;
    requires transitive jakarta.inject;
    requires transitive lesscss.engine;
    requires transitive org.slf4j;
    requires transitive swdc.application.dependency;
    requires transitive swdc.application.configs;
    requires transitive org.controlsfx.controls;
    requires transitive java.desktop;

    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.annotation;

    requires transitive swdc.commons;

    exports org.swdc.fx.config;
    exports org.swdc.fx.config.editors;
    exports org.swdc.fx.view;
    exports org.swdc.fx;
    exports org.swdc.fx.font;

    opens fonts;
    opens banner;

    opens org.swdc.fx.config to
            swdc.application.configs,
            com.fasterxml.jackson.databind;

}