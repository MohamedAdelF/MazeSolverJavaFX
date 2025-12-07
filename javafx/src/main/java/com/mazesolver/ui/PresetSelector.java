package com.mazesolver.ui;

import com.mazesolver.model.MazePreset;
import com.mazesolver.util.MazePresets;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class PresetSelector extends VBox {
    private Consumer<MazePreset> onPresetSelected;

    public PresetSelector() {
        setSpacing(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

        Label title = new Label("Presets");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        getChildren().add(title);

        for (MazePreset preset : MazePresets.PRESETS) {
            Button button = new Button(preset.getName());
            button.setMaxWidth(Double.MAX_VALUE);
            button.setStyle("-fx-padding: 8;");
            button.setUserData(preset);
            
            // Store preset reference directly in the lambda
            final MazePreset presetRef = preset;
            button.setOnAction(e -> {
                if (onPresetSelected != null && presetRef != null) {
                    onPresetSelected.accept(presetRef);
                }
            });
            getChildren().add(button);
        }
    }

    public void setOnPresetSelected(Consumer<MazePreset> callback) {
        this.onPresetSelected = callback;
    }

    public MazePreset getSelectedPreset(Button button) {
        return (MazePreset) button.getUserData();
    }
}

