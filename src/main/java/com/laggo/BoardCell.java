package com.laggo;

import java.util.HashSet;
import java.util.Set;

public class BoardCell {
    private final BoardLocation location;
    Set<WallDirection> walls;

    public BoardCell(BoardLocation location, Set<WallDirection> walls) {
        this.location = location;
        this.walls = new HashSet<>(walls);

        // more type etc here
    }

    public BoardCell(BoardLocation location) {
        this(location, WallDirection.all());
    }

    public WallDirection getWallTo(BoardCell other) {
        if (this.getLocation().manhattanDistTo(other.getLocation()) != 1) {
            return null;
        }

        if (other.getLocation().getY() < this.getLocation().getY()) {
            return WallDirection.UP;
        } else if (other.getLocation().getY() > this.getLocation().getY()) {
            return WallDirection.DOWN;
        } else if (other.getLocation().getX() < this.getLocation().getX()) {
            return WallDirection.LEFT;
        } else if (other.getLocation().getX() > this.getLocation().getX()) {
            return WallDirection.RIGHT;
        }

        return null;
    }

    public void connectTo(BoardCell other) {
        other.openInDirection(other.getWallTo(this));
        this.openInDirection(this.getWallTo(other));
    }

    public void openInDirection(WallDirection direction) {
        this.walls.remove(direction);
    }

    public boolean canLeave(WallDirection direction) {
        return !this.walls.contains(direction);
    }

    public boolean hasAllWalls() {
        return this.walls.size() == 4;
    }

    public BoardLocation getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "BoardCell{" +
                "location=" + location +
                ", walls=" + walls +
                '}';
    }
}