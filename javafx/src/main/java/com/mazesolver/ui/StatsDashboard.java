package com.mazesolver.ui;

import com.mazesolver.model.MazeStats;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StatsDashboard extends VBox {
    private Label stepsLabel;
    private Label backtracksLabel;
    private Label cellsVisitedLabel;
    private Label pathLengthLabel;
    private Label timeElapsedLabel;
    
    private HBox stepsItem;
    private HBox backtracksItem;
    private HBox cellsVisitedItem;
    private HBox pathLengthItem;
    private HBox timeElapsedItem;

    public StatsDashboard() {
        setSpacing(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

        Label title = new Label("Statistics");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        getChildren().add(title);

        stepsItem = createStatItem("Steps Taken", "0");
        stepsLabel = new Label();
        stepsLabel.setGraphic(stepsItem);
        
        backtracksItem = createStatItem("Backtracks", "0");
        backtracksLabel = new Label();
        backtracksLabel.setGraphic(backtracksItem);
        
        cellsVisitedItem = createStatItem("Cells Visited", "0");
        cellsVisitedLabel = new Label();
        cellsVisitedLabel.setGraphic(cellsVisitedItem);
        
        pathLengthItem = createStatItem("Path Length", "-");
        pathLengthLabel = new Label();
        pathLengthLabel.setGraphic(pathLengthItem);
        
        timeElapsedItem = createStatItem("Time Elapsed", "0ms");
        timeElapsedLabel = new Label();
        timeElapsedLabel.setGraphic(timeElapsedItem);

        getChildren().addAll(stepsLabel, backtracksLabel, cellsVisitedLabel, pathLengthLabel, timeElapsedLabel);
    }

    private HBox createStatItem(String label, String value) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8));
        item.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 5;");
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label nameLabel = new Label(label + ":");
        nameLabel.setStyle("-fx-font-size: 12px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: monospace;");

        item.getChildren().addAll(nameLabel, spacer, valueLabel);
        item.setUserData(valueLabel);
        
        return item;
    }

    public void updateStats(MazeStats stats) {
        ((Label) stepsItem.getUserData()).setText(String.valueOf(stats.getStepsTaken()));
        ((Label) backtracksItem.getUserData()).setText(String.valueOf(stats.getBacktracks()));
        ((Label) cellsVisitedItem.getUserData()).setText(String.valueOf(stats.getCellsVisited()));
        ((Label) pathLengthItem.getUserData()).setText(stats.getPathLength() > 0 ? String.valueOf(stats.getPathLength()) : "-");
        ((Label) timeElapsedItem.getUserData()).setText(formatTime(stats.getTimeElapsed()));
    }

    private String formatTime(long ms) {
        if (ms < 1000) {
            return ms + "ms";
        }
        return String.format("%.1fs", ms / 1000.0);
    }
}

