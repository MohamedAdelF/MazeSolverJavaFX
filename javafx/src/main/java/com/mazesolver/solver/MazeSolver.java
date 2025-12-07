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
    
    // For BFS: track parent of each position to reconstruct path
    private final Map<String, Position> parentMap = new HashMap<>();
    
    // For Linked List: track current position index (sequential traversal)
    private int linkedListCurrentIndex = 0;
    
    // For A* Algorithm (AI): Priority Queue with f(n) = g(n) + h(n)
    private final PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
    private final Map<String, Double> gScore = new HashMap<>();  // g(n) = cost from start
    private final Map<String, Position> aStarParentMap = new HashMap<>();  // For path reconstruction
    private final Set<String> closedSet = new HashSet<>();  // Already evaluated nodes
    
    private AlgorithmState algorithmState = AlgorithmState.IDLE;
    private long startTime;
    private Thread solverThread;
    
    private Runnable onStateChanged;
    private Runnable onStatsChanged;
    private Runnable onCellStatesChanged;
    
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}}; // right, down, left, up
    
    // A* Node class for Priority Queue
    private static class AStarNode {
        private final Position position;
        private final double f;  // f(n) = g(n) + h(n)
        private final double g;  // g(n) = cost from start
        private final double h;  // h(n) = heuristic (estimated cost to end)
        
        public AStarNode(Position position, double g, double h) {
            this.position = position;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
        
        public Position getPosition() { return position; }
        public double getF() { return f; }
        public double getG() { return g; }
        public double getH() { return h; }
    }
    
    // Heuristic function for A*: Manhattan distance
    private double heuristic(Position a, Position b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

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
    
    public List<Position> getAStarOpenSet() {
        List<Position> result = new ArrayList<>();
        for (AStarNode node : openSet) {
            result.add(node.getPosition());
        }
        return result;
    }

    public CellState[][] getCellStates() {
        return cellStates;
    }

    public Position getCurrentPosition() {
        List<Position> currentDS = getCurrentDataStructure();
        if (currentDS.isEmpty()) return null;
        
        switch (dataStructureType) {
            case STACK:
                return currentDS.get(currentDS.size() - 1);
            case QUEUE:
                return currentDS.get(0);
            case LINKED_LIST:
                // For linked list, return the current node at the index
                if (linkedListCurrentIndex < linkedList.size()) {
                    return linkedList.get(linkedListCurrentIndex);
                }
                return null;
            case A_STAR:
                // Return the node with lowest f(n) from open set
                if (!openSet.isEmpty()) {
                    return openSet.peek().getPosition();
                }
                return null;
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
            case A_STAR:
                return getAStarOpenSet();
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

        // Stack: LIFO - get from top (last element)
        Position current = stack.get(stack.size() - 1);

        if (current.getX() == end.getX() && current.getY() == end.getY()) {
            // Found the end! Mark the path (stack contains the path from start to end)
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
            // DFS: Take first neighbor and push to stack
            Position next = neighbors.get(0);
            visited.add(posToKey(next));
            stack.add(next); // Push to stack (add to end)

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
            // No neighbors: backtrack (pop from stack)
            stack.remove(stack.size() - 1); // Pop from stack (remove from end)

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

        // Queue: FIFO - remove from front (first element)
        Position current = queue.remove(0);

        if (current.getX() == end.getX() && current.getY() == end.getY()) {
            // Found the end! Reconstruct path using parent map
            List<Position> path = reconstructPath(current);
            for (Position pos : path) {
                if (!(pos.getX() == start.getX() && pos.getY() == start.getY()) &&
                    !(pos.getX() == end.getX() && pos.getY() == end.getY())) {
                    updateCellState(pos.getX(), pos.getY(), CellState.PATH);
                }
            }
            stats.setPathLength(path.size());
            algorithmState = AlgorithmState.FINISHED;
            notifyStateChanged();
            notifyStatsChanged();
            return false;
        }

        // BFS: Explore all neighbors and add to queue
        List<Position> neighbors = getNeighbors(current);
        for (Position neighbor : neighbors) {
            if (!visited.contains(posToKey(neighbor))) {
                visited.add(posToKey(neighbor));
                queue.add(neighbor); // Enqueue (add to end)
                parentMap.put(posToKey(neighbor), current); // Track parent for path reconstruction
            }
        }

        if (!(current.getX() == start.getX() && current.getY() == start.getY())) {
            updateCellState(current.getX(), current.getY(), CellState.VISITED);
        }
        if (!queue.isEmpty()) {
            Position next = queue.get(0); // Next to process (front of queue)
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
    
    private List<Position> reconstructPath(Position endPos) {
        List<Position> path = new ArrayList<>();
        Position current = endPos;
        
        // Reconstruct path from end to start using parent map
        while (current != null) {
            path.add(0, current); // Add to front to reverse the path
            String key = posToKey(current);
            current = parentMap.get(key);
            
            // Stop if we reached the start (start has no parent)
            if (current != null && current.getX() == start.getX() && current.getY() == start.getY()) {
                path.add(0, current);
                break;
            }
        }
        
        return path;
    }

    private boolean stepLinkedList() {
        if (linkedList.isEmpty() || linkedListCurrentIndex >= linkedList.size()) {
            algorithmState = AlgorithmState.NO_PATH;
            notifyStateChanged();
            return false;
        }

        // Linked List: Sequential traversal from head to tail
        // We traverse the list sequentially, moving forward one node at a time
        Position current = linkedList.get(linkedListCurrentIndex);

        if (current.getX() == end.getX() && current.getY() == end.getY()) {
            // Found the end! Mark the path from start to current index
            for (int i = 0; i <= linkedListCurrentIndex; i++) {
                Position pos = linkedList.get(i);
                if (!(pos.getX() == start.getX() && pos.getY() == start.getY()) &&
                    !(pos.getX() == end.getX() && pos.getY() == end.getY())) {
                    updateCellState(pos.getX(), pos.getY(), CellState.PATH);
                }
            }
            stats.setPathLength(linkedListCurrentIndex + 1);
            algorithmState = AlgorithmState.FINISHED;
            notifyStateChanged();
            notifyStatsChanged();
            return false;
        }

        List<Position> neighbors = getNeighbors(current);

        if (!neighbors.isEmpty()) {
            // Add next node to linked list (append new node at the end)
            Position next = neighbors.get(0);
            visited.add(posToKey(next));
            linkedList.add(next); // Add new node at the end
            
            // Move to the next node in the list (sequential traversal)
            linkedListCurrentIndex++;

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
            // No neighbors: backtrack by moving to previous node
            // In a linked list, we can't easily remove nodes, so we move the index back
            if (linkedListCurrentIndex > 0) {
                linkedListCurrentIndex--; // Move back to previous node
                
                if (!(current.getX() == start.getX() && current.getY() == start.getY()) &&
                    !(current.getX() == end.getX() && current.getY() == end.getY())) {
                    updateCellState(current.getX(), current.getY(), CellState.BACKTRACKED);
                }
                
                Position prev = linkedList.get(linkedListCurrentIndex);
                if (!(prev.getX() == start.getX() && prev.getY() == start.getY()) &&
                    !(prev.getX() == end.getX() && prev.getY() == end.getY())) {
                    updateCellState(prev.getX(), prev.getY(), CellState.CURRENT);
                }
            } else {
                // Can't go back further - no path found
                algorithmState = AlgorithmState.NO_PATH;
                notifyStateChanged();
                return false;
            }

            stats.incrementStepsTaken();
            stats.incrementBacktracks();
            stats.setTimeElapsed(System.currentTimeMillis() - startTime);
            notifyStatsChanged();
            return linkedListCurrentIndex < linkedList.size();
        }
    }

    private boolean stepAStar() {
        if (openSet.isEmpty()) {
            algorithmState = AlgorithmState.NO_PATH;
            notifyStateChanged();
            return false;
        }

        // Get node with lowest f(n) from open set
        AStarNode current = openSet.poll();
        Position currentPos = current.getPosition();
        String currentKey = posToKey(currentPos);

        // Move from open set to closed set
        closedSet.add(currentKey);

        // Check if we reached the end
        if (currentPos.getX() == end.getX() && currentPos.getY() == end.getY()) {
            // Reconstruct path using parent map
            List<Position> path = reconstructAStarPath(currentPos);
            for (Position pos : path) {
                if (!(pos.getX() == start.getX() && pos.getY() == start.getY()) &&
                    !(pos.getX() == end.getX() && pos.getY() == end.getY())) {
                    updateCellState(pos.getX(), pos.getY(), CellState.PATH);
                }
            }
            stats.setPathLength(path.size());
            algorithmState = AlgorithmState.FINISHED;
            notifyStateChanged();
            notifyStatsChanged();
            return false;
        }

        // Mark current as visited
        if (!(currentPos.getX() == start.getX() && currentPos.getY() == start.getY())) {
            updateCellState(currentPos.getX(), currentPos.getY(), CellState.VISITED);
        }

        // Explore neighbors
        List<Position> neighbors = getNeighborsForAStar(currentPos);
        for (Position neighbor : neighbors) {
            String neighborKey = posToKey(neighbor);
            
            // Skip if already in closed set
            if (closedSet.contains(neighborKey)) {
                continue;
            }

            // Calculate tentative g score (cost from start to neighbor)
            double tentativeG = gScore.get(currentKey) + 1.0; // Each step costs 1

            // If neighbor not in open set, add it
            if (!gScore.containsKey(neighborKey)) {
                gScore.put(neighborKey, Double.MAX_VALUE);
            }

            // If this path to neighbor is better, update it
            if (tentativeG < gScore.get(neighborKey)) {
                aStarParentMap.put(neighborKey, currentPos);
                gScore.put(neighborKey, tentativeG);
                
                double h = heuristic(neighbor, end);
                double f = tentativeG + h;
                
                // Add to open set
                openSet.add(new AStarNode(neighbor, tentativeG, h));
                
                if (!(neighbor.getX() == end.getX() && neighbor.getY() == end.getY())) {
                    updateCellState(neighbor.getX(), neighbor.getY(), CellState.CURRENT);
                }
            }
        }

        // Update stats
        stats.incrementStepsTaken();
        stats.setCellsVisited(closedSet.size());
        stats.setTimeElapsed(System.currentTimeMillis() - startTime);
        notifyStatsChanged();

        return !openSet.isEmpty();
    }
    
    private List<Position> getNeighborsForAStar(Position pos) {
        List<Position> neighbors = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int newX = pos.getX() + dir[0];
            int newY = pos.getY() + dir[1];
            // For A*, we check if it's valid (not wall and within bounds)
            // visited check is done in stepAStar using closedSet
            if (newX >= 0 && newX < grid[0].length &&
                newY >= 0 && newY < grid.length &&
                grid[newY][newX] == 0) {
                neighbors.add(new Position(newX, newY));
            }
        }
        return neighbors;
    }
    
    private List<Position> reconstructAStarPath(Position endPos) {
        List<Position> path = new ArrayList<>();
        Position current = endPos;
        
        while (current != null) {
            path.add(0, current);
            String key = posToKey(current);
            current = aStarParentMap.get(key);
            
            if (current != null && current.getX() == start.getX() && current.getY() == start.getY()) {
                path.add(0, current);
                break;
            }
        }
        
        return path;
    }

    private boolean step() {
        switch (dataStructureType) {
            case STACK:
                return stepStack();
            case QUEUE:
                return stepQueue();
            case LINKED_LIST:
                return stepLinkedList();
            case A_STAR:
                return stepAStar();
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
                    stack.add(start); // Push start to stack
                    break;
                case QUEUE:
                    queue.add(start); // Enqueue start
                    parentMap.put(posToKey(start), null); // Start has no parent
                    break;
                case LINKED_LIST:
                    linkedList.add(start); // Add start as first node
                    linkedListCurrentIndex = 0; // Start from the beginning
                    break;
                case A_STAR:
                    // Initialize A* algorithm
                    String startKey = posToKey(start);
                    gScore.put(startKey, 0.0);
                    double hStart = heuristic(start, end);
                    openSet.add(new AStarNode(start, 0.0, hStart));
                    aStarParentMap.put(startKey, null);
                    break;
            }
        }
        algorithmState = AlgorithmState.RUNNING;
        notifyStateChanged();

        solverThread = new Thread(() -> {
            while (algorithmState == AlgorithmState.RUNNING) {
                boolean canContinue = step();
                if (!canContinue) {
                    break;
                }
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
                    parentMap.put(posToKey(start), null);
                    break;
                case LINKED_LIST:
                    linkedList.add(start);
                    linkedListCurrentIndex = 0;
                    break;
                case A_STAR:
                    String startKey = posToKey(start);
                    gScore.put(startKey, 0.0);
                    double hStart = heuristic(start, end);
                    openSet.add(new AStarNode(start, 0.0, hStart));
                    aStarParentMap.put(startKey, null);
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

    public void stopAlgorithm() {
        // Stop the algorithm thread if running
        if (algorithmState == AlgorithmState.RUNNING || algorithmState == AlgorithmState.PAUSED) {
            algorithmState = AlgorithmState.IDLE;
            if (solverThread != null && solverThread.isAlive()) {
                solverThread.interrupt();
                try {
                    solverThread.join(100); // Wait up to 100ms for thread to finish
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void resetVisualization() {
        // Stop algorithm first if running
        stopAlgorithm();
        
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
        parentMap.clear(); // Clear parent map for BFS
        linkedListCurrentIndex = 0; // Reset linked list index
        openSet.clear(); // Clear A* open set
        gScore.clear(); // Clear A* g scores
        aStarParentMap.clear(); // Clear A* parent map
        closedSet.clear(); // Clear A* closed set
        stats.reset();
        algorithmState = AlgorithmState.IDLE;
        notifyStateChanged();
        notifyStatsChanged();
        notifyCellStatesChanged();
    }
}

