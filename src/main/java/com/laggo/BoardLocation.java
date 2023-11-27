package com.laggo;

public class BoardLocation implements Cloneable {
    private final int x;
    private final int y;

    public BoardLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public BoardLocation offsetBy(int x, int y) {
        return new BoardLocation(this.x + x, this.y + y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoardLocation other)) {
            return false;
        }

        return other.x == this.x && other.y == this.y;
    }

    @Override
    public BoardLocation clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return new BoardLocation(this.x, this.y);
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int manhattanDistTo(BoardLocation other) {
        return Math.abs(this.getX() - other.getX()) + Math.abs(this.getY() - other.getY());
    }

    @Override
    public String toString() {
        return "BoardLocation{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public boolean equals(BoardLocation other) {
        return other != null && this.x == other.x && this.y == other.y;
    }
}
