package com.laggo;

public record BoardLocation(int x, int y) implements Cloneable {
    public BoardLocation {
        if (x <= 0 || y <= 0) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    public BoardLocation offsetBy(int x, int y) {
        return new BoardLocation(this.x + x, this.y + y);
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

    public int manhattanDistTo(BoardLocation other) {
        return Math.abs(this.x() - other.x()) + Math.abs(this.y() - other.y());
    }
}
