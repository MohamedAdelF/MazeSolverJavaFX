package com.mazesolver.model;

public class MazePreset {
    private final String id;
    private final String name;
    private final int[][] grid;
    private final Position start;
    private final Position end;

    public MazePreset(String id, String name, int[][] grid, Position start, Position end) {
        this.id = id;
        this.name = name;
        this.grid = grid;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int[][] getGrid() {
        return grid;
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }
}

