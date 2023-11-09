package com.laggo;

public enum CellType {
    NONE,
    OBSTACLE_RHYTHM,
    OBSTACLE_PASSWORD;

    char toChar() {
        return new char[]{'o', 'r', 'p'}[this.ordinal()];
    }
}
