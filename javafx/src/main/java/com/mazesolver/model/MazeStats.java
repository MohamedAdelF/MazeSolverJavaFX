package com.mazesolver.model;

public class MazeStats {
    private int stepsTaken;
    private int backtracks;
    private int cellsVisited;
    private int pathLength;
    private long timeElapsed;

    public MazeStats() {
        reset();
    }

    public void reset() {
        stepsTaken = 0;
        backtracks = 0;
        cellsVisited = 0;
        pathLength = 0;
        timeElapsed = 0;
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public void incrementStepsTaken() {
        this.stepsTaken++;
    }

    public int getBacktracks() {
        return backtracks;
    }

    public void setBacktracks(int backtracks) {
        this.backtracks = backtracks;
    }

    public void incrementBacktracks() {
        this.backtracks++;
    }

    public int getCellsVisited() {
        return cellsVisited;
    }

    public void setCellsVisited(int cellsVisited) {
        this.cellsVisited = cellsVisited;
    }

    public int getPathLength() {
        return pathLength;
    }

    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}

