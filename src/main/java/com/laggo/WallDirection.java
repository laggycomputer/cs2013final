package com.laggo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum WallDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static Set<WallDirection> all() {
        return new HashSet<>(List.of(new WallDirection[]{UP, DOWN, LEFT, RIGHT}));
    }
}
