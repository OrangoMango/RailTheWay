// File managed by WebFX (DO NOT EDIT MANUALLY)

module RailTheWay.application {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    requires webfx.platform.resource;
    requires webfx.platform.scheduler;
    requires webfx.platform.shutdown;

    // Exported packages
    exports com.orangomango.railway;
    exports com.orangomango.railway.game;
    exports com.orangomango.railway.ui;

    // Resources packages
    opens audio;
    opens fonts;
    opens images;
    opens worlds;

    // Provided services
    provides javafx.application.Application with com.orangomango.railway.MainApplication;

}