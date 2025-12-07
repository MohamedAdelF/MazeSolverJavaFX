package com.mazesolver;

import com.mazesolver.util.PlatformUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MazeSolverApp app = new MazeSolverApp();
            app.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        // Platform-specific initialization
        if (PlatformUtils.isMac()) {
            // macOS specific settings
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Maze Solver");
            // Use software rendering on macOS to avoid known issues
            System.setProperty("prism.order", "sw");
        } else if (PlatformUtils.isWindows()) {
            // Windows optimizations - JavaFX works excellently on Windows
            System.setProperty("javafx.animation.fullspeed", "true");
            // Hardware acceleration works great on Windows
            System.setProperty("prism.order", "d3d,sw");
        } else if (PlatformUtils.isLinux()) {
            // Linux settings
            System.setProperty("prism.order", "es2,sw");
        }
        
        System.out.println("Starting Maze Solver Robot on " + PlatformUtils.getPlatformName());
        
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Failed to launch application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

