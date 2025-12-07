package com.mazesolver;

import com.mazesolver.model.*;
import com.mazesolver.solver.MazeSolver;
import com.mazesolver.ui.*;
import com.mazesolver.util.MazePresets;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class MazeSolverApp {
    private static final int GRID_SIZE = 10;
    
    private Stage stage;
    private MazeGrid mazeGrid;
    private ControlPanel controlPanel;
    private StatsDashboard statsDashboard;
    private DataStructureVisualization dataStructureViz;
    private DataStructureSelector dataStructureSelector;
    private PresetSelector presetSelector;
    
    private int[][] grid;
    private CellState[][] cellStates;
    private Position start;
    private Position end;
    private DataStructureType currentDataType;
    private MazeSolver solver;
    private int currentSpeed = 300;

    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        
        // Initialize with first preset
        MazePreset initialPreset = MazePresets.PRESETS[0];
        grid = copyGrid(initialPreset.getGrid());
        start = initialPreset.getStart();
        end = initialPreset.getEnd();
        currentDataType = DataStructureType.STACK;
        
        initializeCellStates();
        createUI();
        
        primaryStage.setTitle("Maze Solver Robot - JavaFX");
        
        BorderPane root = createMainLayout();
        Scene scene = new Scene(root, 1400, 900);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);
        
        // Center window on screen (works on all platforms)
        primaryStage.centerOnScreen();
        
        // Proper close handling for all platforms
        primaryStage.setOnCloseRequest(e -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });
        
        primaryStage.show();
    }

    private void initializeCellStates() {
        cellStates = new CellState[GRID_SIZE][GRID_SIZE];
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (x == start.getX() && y == start.getY()) {
                    cellStates[y][x] = CellState.START;
                } else if (x == end.getX() && y == end.getY()) {
                    cellStates[y][x] = CellState.END;
                } else if (grid[y][x] == 1) {
                    cellStates[y][x] = CellState.WALL;
                } else {
                    cellStates[y][x] = CellState.EMPTY;
                }
            }
        }
    }

    private int[][] copyGrid(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }

    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        
        // Header
        HBox header = createHeader();
        root.setTop(header);
        
        // Main content
        HBox mainContent = new HBox(15);
        mainContent.setPadding(new Insets(15));
        
        // Left sidebar
        VBox leftSidebar = new VBox(15);
        leftSidebar.setPrefWidth(250);
        leftSidebar.getChildren().addAll(dataStructureSelector, controlPanel, presetSelector);
        
        // Center - Maze grid
        VBox center = new VBox(10);
        center.setAlignment(Pos.CENTER);
        center.getChildren().add(mazeGrid);
        
        // Right sidebar
        VBox rightSidebar = new VBox(15);
        rightSidebar.setPrefWidth(280);
        rightSidebar.getChildren().addAll(statsDashboard, dataStructureViz);
        
        mainContent.getChildren().addAll(leftSidebar, center, rightSidebar);
        HBox.setHgrow(center, Priority.ALWAYS);
        
        root.setCenter(mainContent);
        
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        
        Label title = new Label("Maze Solver Robot");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitle = new Label();
        updateSubtitle(subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button infoButton = new Button("ℹ Info");
        infoButton.setOnAction(e -> showInfoDialog());
        
        header.getChildren().addAll(title, subtitle, spacer, infoButton);
        
        return header;
    }

    private void updateSubtitle(Label subtitle) {
        String text = "";
        switch (currentDataType) {
            case STACK:
                text = "DFS Pathfinding Visualization";
                break;
            case QUEUE:
                text = "BFS Pathfinding Visualization";
                break;
            case LINKED_LIST:
                text = "Linked List Traversal Visualization";
                break;
        }
        subtitle.setText(text);
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    }

    private void createUI() {
        // Create maze grid
        mazeGrid = new MazeGrid(GRID_SIZE);
        mazeGrid.setCellStates(cellStates);
        mazeGrid.setOnCellClick((x, y) -> toggleCell(x, y));
        mazeGrid.setOnSetStart((x, y) -> setStartPosition(x, y));
        mazeGrid.setOnSetEnd((x, y) -> setEndPosition(x, y));
        
        // Create control panel
        controlPanel = new ControlPanel();
        controlPanel.setOnStart(() -> startAlgorithm());
        controlPanel.setOnPause(() -> pauseAlgorithm());
        controlPanel.setOnStepForward(() -> stepForward());
        controlPanel.setOnReset(() -> reset());
        controlPanel.setOnClear(() -> clearMaze());
        controlPanel.setOnSpeedChange(() -> {
            currentSpeed = controlPanel.getSpeed();
            if (solver != null) {
                // Speed change will be applied on next start
            }
        });
        
        // Create stats dashboard
        statsDashboard = new StatsDashboard();
        statsDashboard.updateStats(new MazeStats());
        
        // Create data structure visualization
        dataStructureViz = new DataStructureVisualization(currentDataType);
        dataStructureViz.update(java.util.Collections.emptyList());
        
        // Create data structure selector
        dataStructureSelector = new DataStructureSelector();
        dataStructureSelector.setOnTypeChanged(() -> {
            DataStructureType newType = dataStructureSelector.getSelectedType();
            if (newType != currentDataType) {
                currentDataType = newType;
                dataStructureViz = new DataStructureVisualization(newType);
                // Rebuild UI
                Platform.runLater(() -> {
                    BorderPane root = (BorderPane) stage.getScene().getRoot();
                    HBox mainContent = (HBox) root.getCenter();
                    VBox rightSidebar = (VBox) mainContent.getChildren().get(2);
                    rightSidebar.getChildren().set(1, dataStructureViz);
                    // Update subtitle
                    HBox header = (HBox) root.getTop();
                    updateSubtitle((Label) header.getChildren().get(1));
                });
            }
        });
        
        // Create preset selector
        presetSelector = new PresetSelector();
        for (javafx.scene.Node node : presetSelector.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnAction(e -> {
                    MazePreset preset = presetSelector.getSelectedPreset(button);
                    loadPreset(preset);
                });
            }
        }
    }


    private void toggleCell(int x, int y) {
        if (solver != null && solver.getAlgorithmState() != AlgorithmState.IDLE) return;
        if ((x == start.getX() && y == start.getY()) || (x == end.getX() && y == end.getY())) return;
        
        grid[y][x] = grid[y][x] == 0 ? 1 : 0;
        cellStates[y][x] = cellStates[y][x] == CellState.WALL ? CellState.EMPTY : CellState.WALL;
        mazeGrid.setCellStates(cellStates);
    }

    private void setStartPosition(int x, int y) {
        if (solver != null && solver.getAlgorithmState() != AlgorithmState.IDLE) return;
        if (grid[y][x] == 1) return;
        if (x == end.getX() && y == end.getY()) return;
        
        cellStates[start.getY()][start.getX()] = CellState.EMPTY;
        start = new Position(x, y);
        cellStates[y][x] = CellState.START;
        mazeGrid.setCellStates(cellStates);
    }

    private void setEndPosition(int x, int y) {
        if (solver != null && solver.getAlgorithmState() != AlgorithmState.IDLE) return;
        if (grid[y][x] == 1) return;
        if (x == start.getX() && y == start.getY()) return;
        
        cellStates[end.getY()][end.getX()] = CellState.EMPTY;
        end = new Position(x, y);
        cellStates[y][x] = CellState.END;
        mazeGrid.setCellStates(cellStates);
    }

    private void loadPreset(MazePreset preset) {
        if (solver != null && solver.getAlgorithmState() != AlgorithmState.IDLE) {
            reset();
        }
        
        grid = copyGrid(preset.getGrid());
        start = preset.getStart();
        end = preset.getEnd();
        initializeCellStates();
        mazeGrid.setCellStates(cellStates);
        
        if (solver != null) {
            solver.resetVisualization();
        }
    }

    private void clearMaze() {
        if (solver != null && solver.getAlgorithmState() != AlgorithmState.IDLE) {
            reset();
        }
        
        grid = MazePresets.createEmptyGrid(GRID_SIZE, GRID_SIZE);
        start = new Position(0, 0);
        end = new Position(GRID_SIZE - 1, GRID_SIZE - 1);
        initializeCellStates();
        mazeGrid.setCellStates(cellStates);
        
        if (solver != null) {
            solver.resetVisualization();
        }
    }

    private void startAlgorithm() {
        if (solver != null && solver.getAlgorithmState() == AlgorithmState.RUNNING) {
            return;
        }
        
        // Create new solver with current state
        CellState[][] statesCopy = new CellState[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            statesCopy[i] = cellStates[i].clone();
        }
        
        solver = new MazeSolver(grid, statesCopy, start, end, currentDataType, currentSpeed);
        solver.setOnStateChanged(() -> {
            Platform.runLater(() -> {
                controlPanel.updateState(solver.getAlgorithmState());
                updateVisualization();
            });
        });
        solver.setOnStatsChanged(() -> {
            Platform.runLater(() -> {
                statsDashboard.updateStats(solver.getStats());
            });
        });
        solver.setOnCellStatesChanged(() -> {
            Platform.runLater(() -> {
                mazeGrid.setCellStates(solver.getCellStates());
                mazeGrid.setCurrentPosition(solver.getCurrentPosition());
            });
        });
        
        solver.startAlgorithm();
        controlPanel.updateState(AlgorithmState.RUNNING);
        mazeGrid.setEditingDisabled(true);
        dataStructureSelector.setRadioButtonsDisabled(true);
    }

    private void pauseAlgorithm() {
        if (solver != null) {
            solver.pauseAlgorithm();
            controlPanel.updateState(AlgorithmState.PAUSED);
        }
    }

    private void stepForward() {
        if (solver == null || solver.getAlgorithmState() == AlgorithmState.RUNNING) {
            if (solver == null) {
                startAlgorithm();
                pauseAlgorithm();
            }
            return;
        }
        
        solver.stepForward();
        updateVisualization();
    }

    private void reset() {
        if (solver != null) {
            solver.resetVisualization();
        }
        initializeCellStates();
        mazeGrid.setCellStates(cellStates);
        mazeGrid.setCurrentPosition(null);
        mazeGrid.setEditingDisabled(false);
        dataStructureSelector.setRadioButtonsDisabled(false);
        statsDashboard.updateStats(new MazeStats());
        dataStructureViz.update(java.util.Collections.emptyList());
        controlPanel.updateState(AlgorithmState.IDLE);
    }

    private void updateVisualization() {
        if (solver == null) return;
        
        List<Position> positions;
        switch (currentDataType) {
            case STACK:
                positions = solver.getStack();
                break;
            case QUEUE:
                positions = solver.getQueue();
                break;
            case LINKED_LIST:
                positions = solver.getLinkedList();
                break;
            default:
                positions = java.util.Collections.emptyList();
        }
        dataStructureViz.update(positions);
    }

    private void showInfoDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How It Works");
        alert.setHeaderText("Maze Solver Robot");
        alert.setContentText(
            "Data Structures:\n" +
            "• Stack (DFS): Explores deep paths first, uses LIFO\n" +
            "• Queue (BFS): Explores level by level, uses FIFO\n" +
            "• Linked List: Sequential traversal with node connections\n\n" +
            "Controls:\n" +
            "• Click cells to toggle walls\n" +
            "• Shift+Click to set start position\n" +
            "• Alt+Click to set end position\n" +
            "• Use presets for quick demos"
        );
        alert.showAndWait();
    }
}

