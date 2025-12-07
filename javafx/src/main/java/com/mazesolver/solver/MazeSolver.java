package com.mazesolver.solver;

import com.mazesolver.model.*;
import javafx.application.Platform;
import java.util.*;

public class MazeSolver {
    private final int[][] grid;
    private final CellState[][] cellStates;
    private final Position start;
    private final Position end;
    private final DataStructureType dataStructureType;
    private final int speed;
    
    private final List<Position> stack = new ArrayList<>();
    private final List<Position> queue = new ArrayList<>();
    private final List<Position> linkedList = new ArrayList<>();
    private final Set<String> visited = new HashSet<>();
    private final MazeStats stats = new MazeStats();
    
    private AlgorithmState algorithmState = AlgorithmState.IDLE;
    private long startTime;
    
    private Runnable onStateChanged;
    private Runnable onStatsChanged;
    private Runnable onCellStatesChanged;
    
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}}; // right, down, left, up

    public MazeSolver(int[][] grid, CellState[][] cellStates, Position start, Position end,
                     DataStructureType dataStructureType, int speed) {
        this.grid = grid;
        this.cellStates = cellStates;
        this.start = start;
        this.end = end;
        this.dataStructureType = dataStructureType;
        this.speed = speed;
    }

    public void setOnStateChanged(Runnable callback) {
        this.onStateChanged = callback;
    }

    public void setOnStatsChanged(Runnable callback) {
        this.onStatsChanged = callback;
    }

    public void setOnCellStatesChanged(Runnable callback) {
        this.onCellStatesChanged = callback;
    }

    public AlgorithmState getAlgorithmState() {
        return algorithmState;
    }

    public MazeStats getStats() {
        return stats;
    }

    public List<Position> getStack() {
        return new ArrayList<>(stack);
    }

    public List<Position> getQueue() {
        return new ArrayList<>(queue);
    }

    public List<Position> getLinkedList() {
        return new ArrayList<>(linkedList);
    }

    public CellState[][] getCellStates() {
        return cellStates;
    }

    public Position getCurrentPosition() {
        List<Position> currentDS = getCurrentDataStructure();
        if (currentDS.isEmpty()) return null;
        
        switch (dataStructureType) {
            case STACK:
            case LINKED_LIST:
                return currentDS.get(currentDS.size() - 1);
            case QUEUE:
                return currentDS.get(0);
            default:
                return null;
        }
    }

    private List<Position> getCurrentDataStructure() {
        switch (dataStructureType) {
            case STACK:
                return stack;
            case QUEUE:
                return queue;
            case LINKED_LIST:
                return linkedList;
            default:
                return stack;
        }
    }

    private String posToKey(Position pos) {
        return pos.getX() + "," + pos.getY();
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < grid[0].length &&
               y >= 0 && y < grid.length &&
               grid[y][x] == 0 &&
               !visited.contains(x + "," + y);
    }

    private List<Position> getNeighbors(Position pos) {
        List<Position> neighbors = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int newX = pos.getX() + dir[0];
            int newY = pos.getY() + dir[1];
            if (isValid(newX, newY)) {
                neighbors.add(new Position(newX, newY));
            }
        }
        return neighbors;
    }

    private void updateCellState(int x, int y, CellState state) {
        cellStates[y][x] = state;
        notifyCellStatesChanged();
    }

    private void notifyStateChanged() {
        if (onStateChanged != null) {
            Platform.runLater(onStateChanged);
        }
    }

    private void notifyStatsChanged() {
        if (onStatsChanged != null) {
            Platform.runLater(onStatsChanged);
        }
    }

    private void notifyCellStatesChanged() {
        if (onCellStatesChanged != null) {
            Platform.runLater(onCellStatesChanged);
        }
    }

    private boolean stepStack() {
        if (stack.isEmpty()) {
            algorithmState = AlgorithmState.NO_PATH;
            notifyStateChanged();
            return false;
        }

        Position current = stack.get(stack.size() - 1);

        if (current.getX() == end.getX() && current.getY() == end.getY()) {
            for (Position pos : stack) {
                if (!(pos.getX() == start.getX() && pos.getY() == start.getY()) &&
                    !(pos.getX() == end.getX() && pos.getY() == end.getY())) {
                    updateCellState(pos.getX(), pos.getY(), CellState.PATH);
                }
            }
            stats.setPathLength(stack.size());
            algorithmState = AlgorithmState.FINISHED;
            notifyStateChanged();
            notifyStatsChanged();
            return false;
        }

        List<Position> neighbors = getNeighbors(current);

        if (!neighbors.isEmpty()) {
            Position next = neighbors.get(0);
            visited.add(posToKey(next));
            stack.add(next);

            if (!(current.getX() == start.getX() && current.getY() == start.getY())) {
                updateCellState(current.getX(), current.getY(), CellState.VISITED);
            }
            if (!(next.getX() == end.getX() && next.getY() == end.getY())) {
                updateCellState(next.getX(), next.getY(), CellState.CURRENT);
            }

            stats.incrementStepsTaken();
            stats.setCellsVisited(visited.size());
            stats.setTimeElapsed(System.currentTimeMillis() - startTime);
            notifyStatsChanged();
            return true;
        } else {
            stack.remove(stack.size() - 1);

            if (!(current.getX() == start.getX() && current.getY() == start.getY()) &&
                !(current.getX() == end.getX() && current.getY() == end.getY())) {
                updateCellState(current.getX(), current.getY(), CellState.BACKTRACKED);
            }
            if (!stack.isEmpty()) {
                Position prev = stack.get(stack.size() - 1);
                if (!(prev.getX() == start.getX() && prev.getY() == start.getY()) &&
                    !(prev.getX() == end.getX() && prev.getY() == end.getY())) {
                    updateCellState(prev.getX(), prev.getY(), CellState.CURRENT);
                }
            }

            stats.incrementStepsTaken();
            stats.incrementBacktracks();
            stats.setTimeElapsed(System.currentTimeMillis() - startTime);
            notifyStatsChanged();
            return !stack.isEmpty();
        }
    }

    private boolean stepQueue() {
        if (queue.isEmpty()) {
            algorithmState = AlgorithmState.NO_PATH;
            notifyStateChanged();
            return false;
        }

        Position current = queue.remove(0);

        if (current.getX() == end.getX() && current.getY() == end.getY()) {
            for (Position pos : queue) {
                if (!(pos.getX() == start.getX() && pos.getY() == start.getY()) &&
                    !(pos.getX() == end.getX() && pos.getY() == end.getY())) {
                    updateCellState(pos.getX(), pos.getY(), CellState.PATH);
                }
            }
            stats.setPathLength(queue.size() + 1);
            algorithmState = AlgorithmState.FINISHED;
            notifyStateChanged();
            notifyStatsChanged();
            return false;
        }

        List<Position> neighbors = getNeighbors(current);
        for (Position neighbor : neighbors) {
            if (!visited.contains(posToKey(neighbor))) {
                visited.add(posToKey(neighbor));
                queue.add(neighbor);
            }
        }

        if (!(current.getX() == start.getX() && current.getY() == start.getY())) {
            updateCellState(current.getX(), current.getY(), CellState.VISITED);
        }
        if (!queue.isEmpty()) {
            Position next = queue.get(0);
            if (!(next.getX() == end.getX() && next.getY() == end.getY())) {
                updateCellState(next.getX(), next.getY(), CellState.CURRENT);
            }
        }

        stats.incrementStepsTaken();
        stats.setCellsVisited(visited.size());
        stats.setTimeElapsed(System.currentTimeMillis() - startTime);
        notifyStatsChanged();
        return !queue.isEmpty();
    }

    private boolean stepLinkedList() {
        if (linkedList.isEmpty()) {
            algorithmState = AlgorithmState.NO_PATH;
            notifyStateChanged();
            return false;
        }

        Position current = linkedList.get(linkedList.size() - 1);

        if (current.getX() == end.getX() && current.getY() == end.getY()) {
            for (Position pos : linkedList) {
                if (!(pos.getX() == start.getX() && pos.getY() == start.getY()) &&
                    !(pos.getX() == end.getX() && pos.getY() == end.getY())) {
                    updateCellState(pos.getX(), pos.getY(), CellState.PATH);
                }
            }
            stats.setPathLength(linkedList.size());
            algorithmState = AlgorithmState.FINISHED;
            notifyStateChanged();
            notifyStatsChanged();
            return false;
        }

        List<Position> neighbors = getNeighbors(current);

        if (!neighbors.isEmpty()) {
            Position next = neighbors.get(0);
            visited.add(posToKey(next));
            linkedList.add(next);

            if (!(current.getX() == start.getX() && current.getY() == start.getY())) {
                updateCellState(current.getX(), current.getY(), CellState.VISITED);
            }
            if (!(next.getX() == end.getX() && next.getY() == end.getY())) {
                updateCellState(next.getX(), next.getY(), CellState.CURRENT);
            }

            stats.incrementStepsTaken();
            stats.setCellsVisited(visited.size());
            stats.setTimeElapsed(System.currentTimeMillis() - startTime);
            notifyStatsChanged();
            return true;
        } else {
            linkedList.remove(linkedList.size() - 1);

            if (!(current.getX() == start.getX() && current.getY() == start.getY()) &&
                !(current.getX() == end.getX() && current.getY() == end.getY())) {
                updateCellState(current.getX(), current.getY(), CellState.BACKTRACKED);
            }
            if (!linkedList.isEmpty()) {
                Position prev = linkedList.get(linkedList.size() - 1);
                if (!(prev.getX() == start.getX() && prev.getY() == start.getY()) &&
                    !(prev.getX() == end.getX() && prev.getY() == end.getY())) {
                    updateCellState(prev.getX(), prev.getY(), CellState.CURRENT);
                }
            }

            stats.incrementStepsTaken();
            stats.incrementBacktracks();
            stats.setTimeElapsed(System.currentTimeMillis() - startTime);
            notifyStatsChanged();
            return !linkedList.isEmpty();
        }
    }

    private boolean step() {
        switch (dataStructureType) {
            case STACK:
                return stepStack();
            case QUEUE:
                return stepQueue();
            case LINKED_LIST:
                return stepLinkedList();
            default:
                return stepStack();
        }
    }

    public void startAlgorithm() {
        if (algorithmState == AlgorithmState.IDLE) {
            resetVisualization();
            visited.add(posToKey(start));
            startTime = System.currentTimeMillis();
            stats.setCellsVisited(1);

            switch (dataStructureType) {
                case STACK:
                    stack.add(start);
                    break;
                case QUEUE:
                    queue.add(start);
                    break;
                case LINKED_LIST:
                    linkedList.add(start);
                    break;
            }
        }
        algorithmState = AlgorithmState.RUNNING;
        notifyStateChanged();

        Thread solverThread = new Thread(() -> {
            while (algorithmState == AlgorithmState.RUNNING) {
                boolean canContinue = step();
                if (!canContinue) {
                    break;
                }
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        solverThread.setDaemon(true);
        solverThread.start();
    }

    public void pauseAlgorithm() {
        algorithmState = AlgorithmState.PAUSED;
        notifyStateChanged();
    }

    public void stepForward() {
        if (algorithmState == AlgorithmState.IDLE) {
            resetVisualization();
            visited.add(posToKey(start));
            startTime = System.currentTimeMillis();
            stats.setCellsVisited(1);

            switch (dataStructureType) {
                case STACK:
                    stack.add(start);
                    break;
                case QUEUE:
                    queue.add(start);
                    break;
                case LINKED_LIST:
                    linkedList.add(start);
                    break;
            }
            algorithmState = AlgorithmState.PAUSED;
            notifyStateChanged();
            return;
        }
        if (algorithmState == AlgorithmState.PAUSED) {
            step();
        }
    }

    public void resetVisualization() {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
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
        stack.clear();
        queue.clear();
        linkedList.clear();
        visited.clear();
        stats.reset();
        algorithmState = AlgorithmState.IDLE;
        notifyStateChanged();
        notifyStatsChanged();
        notifyCellStatesChanged();
    }
}

