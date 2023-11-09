package com.laggo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameBoard {
    private final int width;
    private final int height;

    private final Hashtable<BoardLocation, BoardCell> cells;

    private final BoardLocation playerLocation = new BoardLocation(0, 0);
    private final Random rand = new Random(1337L);

    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;

        this.cells = new Hashtable<>(width * height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                BoardLocation loc = new BoardLocation(i, j);
                BoardCell cell = new BoardCell(loc);
                this.cells.put(loc, cell);
            }
        }

        this.populate();
    }

    public BoardCell getCellAt(BoardLocation loc) {
        return this.cells.get(loc);
    }

    public BoardCell getCellAt(int x, int y) {
        return this.getCellAt(new BoardLocation(x, y));
    }

    public Set<BoardCell> getNeighborsOf(BoardCell cell) {
        return Stream.of(new BoardCell[]{
                this.getCellAt(cell.getLocation().offsetBy(0, -1)),
                this.getCellAt(cell.getLocation().offsetBy(0, 1)),
                this.getCellAt(cell.getLocation().offsetBy(-1, 0)),
                this.getCellAt(cell.getLocation().offsetBy(1, 0))
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private char[][] toSkinnyMatrix() {
        char[][] ret = new char[this.height * 2 + 1][this.width * 2 + 1];

        // start with all walls
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[0].length; j++) {
                ret[i][j] = 'O';
            }
        }

        for (final BoardCell cell : this.cells.values()) {
            final int xInArray = cell.getLocation().getX() * 2 + 1;
            final int yInArray = cell.getLocation().getY() * 2 + 1;

            ret[yInArray][xInArray] = this.playerLocation == cell.getLocation() ? '@' : ' ';

            // then open up some walls
            if (cell.canLeave(WallDirection.UP) && cell.getLocation().getY() > 0) {
                ret[yInArray - 1][xInArray] = ' ';
            } else if (cell.canLeave(WallDirection.DOWN) && cell.getLocation().getY() < this.height - 1) {
                ret[yInArray + 1][xInArray] = ' ';
            } else if (cell.canLeave(WallDirection.LEFT) && cell.getLocation().getX() > 0) {
                ret[yInArray][xInArray - 1] = ' ';
            } else if (cell.canLeave(WallDirection.RIGHT) && cell.getLocation().getX() < this.width - 1) {
                ret[yInArray][xInArray + 1] = ' ';
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        char[][] asMatrix = this.toSkinnyMatrix();

        return Arrays.stream(asMatrix).map(String::new).collect(Collectors.joining("\n"));
    }

    private BoardCell getCellIn(Collection<BoardCell> cells) {
        return cells.stream().toList().get(this.rand.nextInt(cells.size()));
    }

    private void doWalk() {
        Set<BoardCell> visitedCells = new HashSet<>(this.width * this.height);

        WallDirection[][] directions = new WallDirection[this.height][this.width];

    }
}