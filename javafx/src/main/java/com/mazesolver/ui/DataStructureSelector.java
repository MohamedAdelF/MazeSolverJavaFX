package com.mazesolver.ui;

import com.mazesolver.model.DataStructureType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class DataStructureSelector extends VBox {
    private ToggleGroup toggleGroup;
    private Runnable onTypeChanged;

    public DataStructureSelector() {
        setSpacing(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

        Label title = new Label("Data Structure");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        getChildren().add(title);

        toggleGroup = new ToggleGroup();

        RadioButton stackButton = new RadioButton("Stack (DFS)");
        stackButton.setToggleGroup(toggleGroup);
        stackButton.setSelected(true);
        stackButton.setUserData(DataStructureType.STACK);
        stackButton.setOnAction(e -> {
            if (onTypeChanged != null) onTypeChanged.run();
        });

        RadioButton queueButton = new RadioButton("Queue (BFS)");
        queueButton.setToggleGroup(toggleGroup);
        queueButton.setUserData(DataStructureType.QUEUE);
        queueButton.setOnAction(e -> {
            if (onTypeChanged != null) onTypeChanged.run();
        });

        RadioButton linkedListButton = new RadioButton("Linked List");
        linkedListButton.setToggleGroup(toggleGroup);
        linkedListButton.setUserData(DataStructureType.LINKED_LIST);
        linkedListButton.setOnAction(e -> {
            if (onTypeChanged != null) onTypeChanged.run();
        });

        RadioButton aStarButton = new RadioButton("A* (AI)");
        aStarButton.setToggleGroup(toggleGroup);
        aStarButton.setUserData(DataStructureType.A_STAR);
        aStarButton.setOnAction(e -> {
            if (onTypeChanged != null) onTypeChanged.run();
        });

        getChildren().addAll(stackButton, queueButton, linkedListButton, aStarButton);
    }

    public DataStructureType getSelectedType() {
        RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
        return selected != null ? (DataStructureType) selected.getUserData() : DataStructureType.STACK;
    }

    public void setSelectedType(DataStructureType type) {
        for (javafx.scene.Node node : getChildren()) {
            if (node instanceof RadioButton) {
                RadioButton rb = (RadioButton) node;
                if (rb.getUserData() == type) {
                    rb.setSelected(true);
                    break;
                }
            }
        }
    }

    public void setRadioButtonsDisabled(boolean disabled) {
        for (javafx.scene.Node node : getChildren()) {
            if (node instanceof RadioButton) {
                ((RadioButton) node).setDisable(disabled);
            }
        }
    }

    public void setOnTypeChanged(Runnable callback) {
        this.onTypeChanged = callback;
    }
}

