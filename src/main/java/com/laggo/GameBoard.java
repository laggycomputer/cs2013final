package com.laggo;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameBoard {
    // General structure and printing code from https://github.com/Gelbpunkt/IdleRPG/
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

    public char[][] toSkinnyMatrix() {
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
            if (cell.canLeave(WallDirection.UP)) {
                ret[yInArray - 1][xInArray] = ' ';
            }
            if (cell.canLeave(WallDirection.DOWN)) {
                ret[yInArray + 1][xInArray] = ' ';
            }
            if (cell.canLeave(WallDirection.LEFT)) {
                ret[yInArray][xInArray - 1] = ' ';
            }
            if (cell.canLeave(WallDirection.RIGHT)) {
                ret[yInArray][xInArray + 1] = ' ';
            }
        }

        return ret;
    }

    public String toUnicodeString() {
        char[][] skinnyMatrix = this.toSkinnyMatrix();
        char[][] doubleWideMatrix = new char[skinnyMatrix.length][skinnyMatrix[0].length * 2 - 1];
        char[][] result = new char[skinnyMatrix.length][skinnyMatrix[0].length * 2 - 1];

        for (int y = 0; y < skinnyMatrix.length; y++) {
            for (int x = 0; x < skinnyMatrix[y].length; x++) {
                doubleWideMatrix[y][x * 2] = skinnyMatrix[y][x];
                if (x < skinnyMatrix[y].length - 1) {
                    doubleWideMatrix[y][x * 2 + 1] = (skinnyMatrix[y][x] == ' ') ? ' ' : skinnyMatrix[y][x + 1];
                }
            }
        }

        for (int y = 0; y < doubleWideMatrix.length; y++) {
            for (int x = 0; x < doubleWideMatrix[y].length; x++) {
                if (doubleWideMatrix[y][x] == 'O') {
                    boolean north = isWall(x, y - 1, doubleWideMatrix);
                    boolean south = isWall(x, y + 1, doubleWideMatrix);
                    boolean east = isWall(x + 1, y, doubleWideMatrix);
                    boolean west = isWall(x - 1, y, doubleWideMatrix);

                    result[y][x] = getUnicodeCharacterForWall(north, south, east, west);
                } else {
                    result[y][x] = ' ';
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (char[] row : result) {
            for (char c : row) {
                sb.append(c);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private boolean isWall(int x, int y, char[][] matrix) {
        return x >= 0 && x < matrix[0].length && y >= 0 && y < matrix.length && matrix[y][x] == 'O';
    }

    private char getUnicodeCharacterForWall(boolean north, boolean south, boolean east, boolean west) {
        if (north && east && !south && !west) return '└';
        if (north && west && !south && !east) return '┘';
        if (south && east && !north && !west) return '┌';
        if (south && west && !north && !east) return '┐';
        if (south && west && north && east) return '┼';

        if (north && south && east && !west) return '├';
        if (north && south && west && !east) return '┤';
        if (east && west && north && !south) return '┴';
        if (east && west && south && !north) return '┬';

        if (north && south && !east && !west) return '│';
        if (east && west && !north && !south) return '─';

        if (north && !south && !east && !west) return '╵';
        if (south && !north && !east && !west) return '╷';
        if (east && !west && !north && !south) return '╶';
        if (west && !east && !north && !south) return '╴';

        return ' ';
    }

    @Override
    public String toString() {
        return this.toUnicodeString();
    }

    private BoardCell getRandCellFrom(Collection<BoardCell> cells) {
        return cells.stream().toList().get(this.rand.nextInt(cells.size()));
    }

    private List<BoardCell> findLoopPointsIn(List<BoardCell> walk) {
        Set<BoardCell> allWalkCells = new HashSet<>();
        List<BoardCell> repeatCells = new ArrayList<>();
        for (final BoardCell cell : walk) {
            if (!allWalkCells.add(cell)) {
                repeatCells.add(cell);
            }
        }
        return repeatCells;
    }

    private CellWalk eraseLoops(List<BoardCell> walk) {
        List<BoardCell> ret = new ArrayList<>(walk);

        List<BoardCell> loopPoints = this.findLoopPointsIn(walk);
        while (!loopPoints.isEmpty()) {
            int loopStartInd = ret.indexOf(loopPoints.get(0)) + 1;
            int loopStartEnd = ret.lastIndexOf(loopPoints.get(0));
            ret.subList(loopStartInd, loopStartEnd + 1).clear();

            loopPoints = this.findLoopPointsIn(ret);
        }

        return new CellWalk(ret);
    }

    private void populate() {
        // Wilson's algorithm involving a loop-erased random walk.
        // This took too long.
        Set<BoardCell> visitedCells = new HashSet<>(this.width * this.height);

        BoardCell start = this.getCellAt(0, 0);
        visitedCells.add(start);

        while (visitedCells.size() < this.width * this.height) {
            BoardCell walkStart = this.getRandCellFrom(CollectionUtils.disjunction(this.cells.values(), visitedCells));
            CellWalk thisWalk = new CellWalk();
            thisWalk.push(walkStart);
            while (!visitedCells.contains(thisWalk.peek())) {
                thisWalk.push(this.getRandCellFrom(this.getNeighborsOf(thisWalk.peek())));
            }

            CellWalk correctedWalk = this.eraseLoops(thisWalk.toList());
            visitedCells.addAll(correctedWalk.toList());
            while (correctedWalk.size() > 1) {
                correctedWalk.pop().connectTo(correctedWalk.peek());
            }
        }
    }
}