package com.mazesolver.ui;

import com.mazesolver.model.DataStructureType;
import com.mazesolver.model.Position;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class DataStructureVisualization extends VBox {
    private ScrollPane scrollPane;
    private VBox contentBox;
    private Label titleLabel;
    private Label countLabel;
    private DataStructureType type;

    public DataStructureVisualization(DataStructureType type) {
        this.type = type;
        setSpacing(10);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        titleLabel = new Label(getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        countLabel = new Label("0 items");
        countLabel.setStyle("-fx-font-size: 12px; -fx-font-family: monospace;");

        header.getChildren().addAll(titleLabel, spacer, countLabel);
        getChildren().add(header);

        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        contentBox = new VBox(5);
        contentBox.setPadding(new Insets(5));
        scrollPane.setContent(contentBox);

        getChildren().add(scrollPane);
    }

    private String getTitle() {
        switch (type) {
            case STACK:
                return "Stack";
            case QUEUE:
                return "Queue";
            case LINKED_LIST:
                return "Linked List";
            case A_STAR:
                return "A* Open Set";
            default:
                return "Data Structure";
        }
    }

    public void update(List<Position> positions) {
        contentBox.getChildren().clear();
        countLabel.setText(positions.size() + " items");

        if (positions.isEmpty()) {
            Label emptyLabel = new Label("Empty");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 14px;");
            emptyLabel.setPadding(new Insets(20));
            emptyLabel.setAlignment(javafx.geometry.Pos.CENTER);
            contentBox.getChildren().add(emptyLabel);
            return;
        }

        // Show last 10 items (for stack/linkedlist) or first 10 (for queue/A*)
        int maxVisible = 10;
        List<Position> visibleItems;
        
        if (type == DataStructureType.QUEUE || type == DataStructureType.A_STAR) {
            visibleItems = positions.subList(0, Math.min(maxVisible, positions.size()));
        } else {
            int start = Math.max(0, positions.size() - maxVisible);
            visibleItems = positions.subList(start, positions.size());
        }

        // Reverse for stack/linkedlist to show top/last first
        if (type == DataStructureType.STACK || type == DataStructureType.LINKED_LIST) {
            for (int i = visibleItems.size() - 1; i >= 0; i--) {
                Position pos = visibleItems.get(i);
                int actualIndex = positions.size() - visibleItems.size() + i;
                boolean isTop = (i == visibleItems.size() - 1);
                contentBox.getChildren().add(createItemBox(pos, actualIndex, isTop));
            }
        } else {
            for (int i = 0; i < visibleItems.size(); i++) {
                Position pos = visibleItems.get(i);
                boolean isFront = (i == 0);
                contentBox.getChildren().add(createItemBox(pos, i, isFront));
            }
        }

        if (positions.size() > maxVisible) {
            Label moreLabel = new Label("+" + (positions.size() - maxVisible) + " more items");
            moreLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 12px; -fx-alignment: center;");
            moreLabel.setPadding(new Insets(5));
            contentBox.getChildren().add(moreLabel);
        }
    }

    private HBox createItemBox(Position pos, int index, boolean isHighlighted) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8));
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        if (isHighlighted) {
            item.setStyle("-fx-background-color: #007bff; -fx-background-radius: 5;");
        } else {
            item.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 5;");
        }

        Label indexLabel = new Label("[" + index + "]");
        indexLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (isHighlighted ? "white" : "#666") + ";");

        Label posLabel = new Label(pos.toString());
        posLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: monospace; -fx-text-fill: " + (isHighlighted ? "white" : "black") + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label tagLabel = new Label();
        if (isHighlighted) {
            if (type == DataStructureType.STACK) {
                tagLabel.setText("TOP");
            } else if (type == DataStructureType.QUEUE) {
                tagLabel.setText("FRONT");
            } else if (type == DataStructureType.A_STAR) {
                tagLabel.setText("BEST");
            } else {
                tagLabel.setText("CURRENT");
            }
            tagLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        }

        item.getChildren().addAll(indexLabel, posLabel, spacer, tagLabel);
        return item;
    }
}

