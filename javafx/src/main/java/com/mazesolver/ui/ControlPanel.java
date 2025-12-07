package com.mazesolver.ui;

import com.mazesolver.model.AlgorithmState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ControlPanel extends VBox {
    private Button playPauseButton;
    private Button stepButton;
    private Button resetButton;
    private Button clearButton;
    private Slider speedSlider;
    private Label speedLabel;
    private Label statusLabel;

    private Runnable onStart;
    private Runnable onPause;
    private Runnable onStepForward;
    private Runnable onReset;
    private Runnable onClear;
    private Runnable onSpeedChange;

    public ControlPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

        Label title = new Label("Controls");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        getChildren().add(title);

        // Play/Pause and Step buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        playPauseButton = new Button("▶");
        playPauseButton.setPrefSize(50, 50);
        playPauseButton.setStyle("-fx-font-size: 16px;");
        playPauseButton.setOnAction(e -> handlePlayPause());

        stepButton = new Button("⏭");
        stepButton.setPrefSize(40, 40);
        stepButton.setStyle("-fx-font-size: 14px;");
        stepButton.setOnAction(e -> {
            if (onStepForward != null) onStepForward.run();
        });

        buttonBox.getChildren().addAll(playPauseButton, stepButton);
        getChildren().add(buttonBox);

        // Speed slider
        VBox speedBox = new VBox(5);
        HBox speedHeader = new HBox();
        speedHeader.setAlignment(Pos.CENTER_LEFT);
        Label speedTitle = new Label("Speed:");
        speedLabel = new Label("Normal");
        speedHeader.getChildren().addAll(speedTitle, new Region(), speedLabel);
        HBox.setHgrow(new Region(), Priority.ALWAYS);

        speedSlider = new Slider(50, 500, 300);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(100);
        speedSlider.setMinorTickCount(1);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSpeedLabel(newVal.intValue());
            if (onSpeedChange != null) onSpeedChange.run();
        });

        HBox speedLabels = new HBox();
        speedLabels.setAlignment(Pos.CENTER);
        speedLabels.getChildren().addAll(new Label("Fast"), new Region(), new Label("Slow"));
        HBox.setHgrow(new Region(), Priority.ALWAYS);

        speedBox.getChildren().addAll(speedHeader, speedSlider, speedLabels);
        getChildren().add(speedBox);

        // Reset and Clear buttons
        resetButton = new Button("Reset");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setOnAction(e -> {
            if (onReset != null) onReset.run();
        });

        clearButton = new Button("Clear Maze");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setOnAction(e -> {
            if (onClear != null) onClear.run();
        });

        getChildren().addAll(resetButton, clearButton);

        // Status label
        statusLabel = new Label();
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setPadding(new Insets(10));
        statusLabel.setStyle("-fx-background-color: transparent;");
        getChildren().add(statusLabel);
    }

    private void handlePlayPause() {
        if (playPauseButton.getText().equals("▶")) {
            if (onStart != null) onStart.run();
        } else {
            if (onPause != null) onPause.run();
        }
    }

    private void updateSpeedLabel(int speed) {
        if (speed <= 150) {
            speedLabel.setText("Fast");
        } else if (speed <= 350) {
            speedLabel.setText("Normal");
        } else {
            speedLabel.setText("Slow");
        }
    }

    public void updateState(AlgorithmState state) {
        boolean isRunning = state == AlgorithmState.RUNNING;
        boolean isFinished = state == AlgorithmState.FINISHED || state == AlgorithmState.NO_PATH;
        boolean canPlay = state == AlgorithmState.IDLE || state == AlgorithmState.PAUSED;

        playPauseButton.setText(isRunning ? "⏸" : "▶");
        playPauseButton.setDisable(isFinished);
        stepButton.setDisable(isRunning || isFinished);
        clearButton.setDisable(state != AlgorithmState.IDLE);
        speedSlider.setDisable(isRunning);

        if (state == AlgorithmState.FINISHED) {
            statusLabel.setText("Path Found!");
            statusLabel.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-padding: 10;");
        } else if (state == AlgorithmState.NO_PATH) {
            statusLabel.setText("No Path Found");
            statusLabel.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-padding: 10;");
        } else {
            statusLabel.setText("");
            statusLabel.setStyle("-fx-background-color: transparent;");
        }
    }

    public int getSpeed() {
        return (int) speedSlider.getValue();
    }

    public void setOnStart(Runnable callback) {
        this.onStart = callback;
    }

    public void setOnPause(Runnable callback) {
        this.onPause = callback;
    }

    public void setOnStepForward(Runnable callback) {
        this.onStepForward = callback;
    }

    public void setOnReset(Runnable callback) {
        this.onReset = callback;
    }

    public void setOnClear(Runnable callback) {
        this.onClear = callback;
    }

    public void setOnSpeedChange(Runnable callback) {
        this.onSpeedChange = callback;
    }
}

