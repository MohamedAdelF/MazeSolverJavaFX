package com.mazesolver.ui;

import com.mazesolver.model.CellState;
import com.mazesolver.model.Position;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class MazeGrid extends GridPane {
    private CellState[][] cellStates;
    private final int gridSize;
    private java.util.function.BiConsumer<Integer, Integer> onCellClick;
    private java.util.function.BiConsumer<Integer, Integer> onSetStart;
    private java.util.function.BiConsumer<Integer, Integer> onSetEnd;
    private boolean editingDisabled = false;
    private Position currentPosition;

    public MazeGrid(int gridSize) {
        this.gridSize = gridSize;
        this.cellStates = new CellState[gridSize][gridSize];
        initializeGrid();
    }

    private void initializeGrid() {
        setHgap(1);
        setVgap(1);
        setPadding(new Insets(5));
        setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                final int fx = x;
                final int fy = y;
                CellPane cell = new CellPane(fx, fy);
                cell.setOnMouseClicked(e -> handleCellClick(fx, fy, e));
                add(cell, fx, fy);
            }
        }
    }

    public void setCellStates(CellState[][] cellStates) {
        this.cellStates = cellStates;
        updateDisplay();
    }

    public void setCurrentPosition(Position position) {
        this.currentPosition = position;
        updateDisplay();
    }

    public void setEditingDisabled(boolean disabled) {
        this.editingDisabled = disabled;
        setDisable(disabled);
    }

    public void setOnCellClick(java.util.function.BiConsumer<Integer, Integer> callback) {
        this.onCellClick = callback;
    }

    public void setOnSetStart(java.util.function.BiConsumer<Integer, Integer> callback) {
        this.onSetStart = callback;
    }

    public void setOnSetEnd(java.util.function.BiConsumer<Integer, Integer> callback) {
        this.onSetEnd = callback;
    }

    private void handleCellClick(int x, int y, MouseEvent e) {
        if (editingDisabled) return;
        
        if (e.isShiftDown()) {
            if (onSetStart != null) onSetStart.accept(x, y);
        } else if (e.isAltDown()) {
            if (onSetEnd != null) onSetEnd.accept(x, y);
        } else {
            if (onCellClick != null) onCellClick.accept(x, y);
        }
    }

    private void updateDisplay() {
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                CellPane cell = (CellPane) getChildren().get(y * gridSize + x);
                cell.updateCell(cellStates[y][x], 
                    currentPosition != null && currentPosition.getX() == x && currentPosition.getY() == y);
            }
        }
    }

    private class CellPane extends StackPane {
        private final int x, y;
        private Rectangle background;
        private Circle indicator;

        public CellPane(int x, int y) {
            this.x = x;
            this.y = y;
            setPrefSize(40, 40);
            setMinSize(40, 40);
            setMaxSize(40, 40);

            background = new Rectangle(40, 40);
            background.setFill(getColorForState(CellState.EMPTY));
            background.setStroke(Color.LIGHTGRAY);
            background.setStrokeWidth(1);

            indicator = new Circle(8);
            indicator.setVisible(false);

            getChildren().addAll(background, indicator);
        }

        public void updateCell(CellState state, boolean isCurrent) {
            background.setFill(getColorForState(state));
            
            if (state == CellState.START) {
                indicator.setFill(Color.WHITE);
                indicator.setVisible(true);
            } else if (state == CellState.END) {
                indicator.setFill(Color.WHITE);
                indicator.setVisible(true);
            } else if (isCurrent && state == CellState.CURRENT) {
                indicator.setFill(Color.WHITE);
                indicator.setVisible(true);
            } else {
                indicator.setVisible(false);
            }

            if (isCurrent && state == CellState.CURRENT) {
                background.setStroke(Color.CYAN);
                background.setStrokeWidth(2);
            } else {
                background.setStroke(Color.LIGHTGRAY);
                background.setStrokeWidth(1);
            }
        }

        private Color getColorForState(CellState state) {
            switch (state) {
                case EMPTY:
                    return Color.WHITE;
                case WALL:
                    return Color.DARKGRAY;
                case START:
                    return Color.GREEN;
                case END:
                    return Color.RED;
                case VISITED:
                    return Color.LIGHTBLUE;
                case CURRENT:
                    return Color.BLUE;
                case PATH:
                    return Color.LIGHTGREEN;
                case BACKTRACKED:
                    return Color.LIGHTGRAY;
                default:
                    return Color.WHITE;
            }
        }
    }
}

