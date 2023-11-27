package com.laggo;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
General structure and printing code from https://github.com/Gelbpunkt/IdleRPG/.
Maze fill implementation our own based on loop-erased random walks; see Wilson's paper @MIT https://www.cs.cmu.edu/~15859n/RelatedWork/RandomTrees-Wilson.pdf
 */
public class GameBoard {
    private final int width;
    private final int height;
    private final Hashtable<BoardLocation, BoardCell> cells;
    private final Hashtable<BoardLocation, BoardObject> objects = new Hashtable<>();
    private final Random rand = new Random(1337L);
    private final Set<String> NOT_WALL;
    private final LinkedList<String> thingsToPrint = new LinkedList<>();
    boolean hasWon = false;
    boolean timeToQuit = false;
    private BoardLocation playerLocation = new BoardLocation(0, 0);

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
        this.NOT_WALL = new HashSet<>(Arrays.asList("@", " ",
                new MazeKey().getIcon(),
                new Enemy().getIcon(),
                new Exit().getIcon()
        ));
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

    private String getIconAt(BoardLocation loc) {
        if (loc.equals(this.playerLocation)) {
            return "@";
        }
        if (objects.containsKey(loc)) {
            return objects.get(loc).getIcon();
        } else {
            return " ";
        }
    }

    /*
    Creates the most compact representation of this maze possible. An example (reproduce by setting the `rand` seed above to `1337L`:
    OOOOOOOOOOO
    O@        O
    O O O OOO O
    O O O O   O
    OOO O OOO O
    O O O O O O
    O O OOO O O
    O       O O
    OOO O OOOOO
    O   O     O
    OOOOOOOOOOO
    */
    public String[][] toSkinnyMatrix() {
        String[][] ret = new String[this.height * 2 + 1][this.width * 2 + 1];

        // start with all walls
        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < ret[0].length; j++) {
                ret[i][j] = "O";
            }
        }

        for (final BoardCell cell : this.cells.values()) {
            final int xInArray = cell.getLocation().getX() * 2 + 1;
            final int yInArray = cell.getLocation().getY() * 2 + 1;

            ret[yInArray][xInArray] = this.getIconAt(cell.getLocation());

            // then open up some walls
            if (cell.canLeave(WallDirection.UP)) {
                ret[yInArray - 1][xInArray] = " ";
            }
            if (cell.canLeave(WallDirection.DOWN)) {
                ret[yInArray + 1][xInArray] = " ";
            }
            if (cell.canLeave(WallDirection.LEFT)) {
                ret[yInArray][xInArray - 1] = " ";
            }
            if (cell.canLeave(WallDirection.RIGHT)) {
                ret[yInArray][xInArray + 1] = " ";
            }
        }

        return ret;
    }

    /*
    Convert the above "skinny" representation to a fuller-looking one using special Unicode characters at wall junctions
    and doubling the width of the maze to achieve a more square aspect ratio. Example output (same repro steps as above):
    ┌───────────────────┐
    │ @                 │
    │   ╷   ╷   ┌───╴   │
    │   │   │   │       │
    ├───┤   │   ├───┐   │
    │   │   │   │   │   │
    │   ╵   └───┘   │   │
    │               │   │
    ├───╴   ╷   ╶───┴───┤
    │       │           │
    └───────┴───────────┘
     */
    public String toUnicodeString() {
        // start with what we did above
        String[][] skinnyMatrix = this.toSkinnyMatrix();
        String[][] doubleWideMatrix = new String[skinnyMatrix.length][skinnyMatrix[0].length * 2 - 1];
        String[][] result = new String[skinnyMatrix.length][skinnyMatrix[0].length * 2 - 1];

        // double the width of `skinnyMatrix`, but make sure not to copy special symbols like `@` for the player twice
        for (int y = 0; y < skinnyMatrix.length; y++) {
            for (int x = 0; x < skinnyMatrix[y].length; x++) {
                doubleWideMatrix[y][x * 2] = skinnyMatrix[y][x];
                if (x < skinnyMatrix[y].length - 1) {
                    doubleWideMatrix[y][x * 2 + 1] = (NOT_WALL.contains(skinnyMatrix[y][x])) ? " " : skinnyMatrix[y][x];
                }
            }
        }

        // remove any wall character directly to the left of non-wall space, transforming 2-wide walkways into 3-wide
        // ones and leaving exactly one space for a perfectly centred icon
        for (int y = 0; y < doubleWideMatrix.length; y++) {
            for (int x = 0; x < doubleWideMatrix[y].length; x++) {
                if (!isWall(x, y, doubleWideMatrix) && isWall(x - 1, y, doubleWideMatrix)) {
                    doubleWideMatrix[y][x - 1] = " ";
                }
            }
        }

        // replace all generic wall characters with special unicode which is more "aware" of the walls around it
        // copy into a result buffer to avoid clashing with itself
        for (int y = 0; y < doubleWideMatrix.length; y++) {
            for (int x = 0; x < doubleWideMatrix[y].length; x++) {
                if (doubleWideMatrix[y][x].equals("O")) {
                    boolean up = isWall(x, y - 1, doubleWideMatrix);
                    boolean down = isWall(x, y + 1, doubleWideMatrix);
                    boolean left = isWall(x - 1, y, doubleWideMatrix);
                    boolean right = isWall(x + 1, y, doubleWideMatrix);

                    // figure out how this wall ought to connect to others around it
                    result[y][x] = this.getUnicodeCharacterForWall(up, down, left, right);
                } else {
                    // this isn't a wall; just copy it
                    result[y][x] = doubleWideMatrix[y][x];
                }
            }
        }

        // join each line into a `String`, join the lines with newlines, and send it off
        return Arrays.stream(result).map(l -> String.join("", l)).collect(Collectors.joining("\n"));
    }

    private boolean isWall(int x, int y, String[][] matrix) {
        return x >= 0 && x < matrix[0].length && y >= 0 && y < matrix.length && matrix[y][x].equals("O");
    }

    private String getUnicodeCharacterForWall(boolean up, boolean down, boolean left, boolean right) {
        if (up && down && left && right) return "┼";

        if (!up && down && left && right) return "┬";
        if (up && !down && left && right) return "┴";
        if (up && down && !left && right) return "├";
        if (up && down && left && !right) return "┤";

        if (!up && !down && left && right) return "─";
        if (up && !down && !left && right) return "└";
        if (up && down && !left && !right) return "│";
        if (!up && down && !left && right) return "┌";
        if (up && !down && left && !right) return "┘";
        if (!up && down && left && !right) return "┐";

        if (up && !down && !left && !right) return "╵";
        if (!up && down && !left && !right) return "╷";
        if (!up && !down && left && !right) return "╴";
        if (!up && !down && !left && right) return "╶";

        return " ";
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

        // While there are cells left to visit:
        while (visitedCells.size() < this.width * this.height) {
            // Start a walk at an unvisited cell.
            BoardCell walkStart = this.getRandCellFrom(CollectionUtils.disjunction(this.cells.values(), visitedCells));
            CellWalk thisWalk = new CellWalk();
            thisWalk.push(walkStart);

            // Walk to random neighbours of the cell at the head of the stack until we stumble back into a visited cell.
            while (!visitedCells.contains(thisWalk.peek())) {
                thisWalk.push(this.getRandCellFrom(this.getNeighborsOf(thisWalk.peek())));
            }

            // Erase any loops on this walk.
            CellWalk correctedWalk = this.eraseLoops(thisWalk.toList());
            visitedCells.addAll(correctedWalk.toList());
            while (correctedWalk.size() > 1) {
                // For every cell on the stack with another cell below it, link the two cells.
                correctedWalk.pop().connectTo(correctedWalk.peek());
            }

            // Repeat above with a new walk. In this way, we will have a perfectly random maze.
        }

        // place map objects
        // this will freeze if the maze is small; too bad!
        objects.put(new BoardLocation(this.width - 1, this.height - 1), new Exit());

        // solve the maze to place enemies along the path
        CellWalk solution = this.solve(this.getCellAt(this.playerLocation));
        assert solution != null;

        for (int i = 0; i < 2; i++) {
            BoardLocation target;
            do {
                target = new BoardLocation(this.rand.nextInt(width), this.rand.nextInt(height));
            } while (playerLocation.equals(target) || this.objects.containsKey(target));

            objects.put(target, new MazeKey());
        }

        for (int i = 0; i < 2; i++) {
            // place enemies along the maze solution path
            BoardLocation target;
            do {
                target = solution.toList().get(this.rand.nextInt(solution.size())).getLocation();
            } while (this.playerLocation.equals(target) || this.objects.containsKey(target));

            objects.put(target, new Enemy());
        }
    }

    private CellWalk solve(BoardCell start) {
        /*
        A breadth-first approach to find the solution to the maze. Taken from
        https://en.wikipedia.org/wiki/Breadth-first_search.
         */
        Queue<BoardCell> toExplore = new LinkedList<>();
        Set<BoardCell> explored = new HashSet<>(this.width * this.height);
        Map<BoardCell, BoardCell> parents = new HashMap<>();
        toExplore.add(start);
        explored.add(start);

        while (!toExplore.isEmpty()) {
            BoardCell here = toExplore.remove();
            if (this.objects.getOrDefault(here.getLocation(), null) instanceof Exit) {
                // time to go; use the parents lookup table to trace back from the finish to the start
                CellWalk ret = new CellWalk();
                // start at the finish...
                BoardCell toPushOnWalk = here;
                do {
                    // push this cell...
                    ret.push(toPushOnWalk);
                    // then start again at its parent
                    toPushOnWalk = parents.get(toPushOnWalk);
                } while (toPushOnWalk != null);

                return ret;
            }
            for (final BoardCell neighbor : this.getNeighborsOf(here).stream()
                    .filter(n -> here.canLeave(here.getWallTo(n))).collect(Collectors.toSet())) {
                if (!explored.contains(neighbor)) {
                    explored.add(neighbor);
                    parents.put(neighbor, here);
                    toExplore.add(neighbor);
                }
            }
        }

        // should never happen, the maze is connected
        return null;
    }

    public boolean tryMove(WallDirection direction, Terminal terminal) throws IOException {
        if (!this.getCellAt(this.playerLocation).canLeave(direction)) {
            return false;
        }

        switch (direction) {
            case UP:
                this.playerLocation = this.playerLocation.offsetBy(0, -1);
                break;
            case DOWN:
                this.playerLocation = this.playerLocation.offsetBy(0, 1);
                break;
            case LEFT:
                this.playerLocation = this.playerLocation.offsetBy(-1, 0);
                break;
            case RIGHT:
                this.playerLocation = this.playerLocation.offsetBy(1, 0);
                break;
        }

        if (this.objects.containsKey(this.playerLocation) && this.objects.get(this.playerLocation).isVisible) {
            this.objects.get(this.playerLocation).onWalkedOn(this, terminal);
        }

        return true;
    }

    public boolean hasPickedUpAllKeys() {
        return this.objects.values().stream().noneMatch(o -> o.isVisible && o instanceof MazeKey);
    }

    public void addThingToPrint(String string) {
        this.thingsToPrint.push(string);
    }

    public void run(Terminal terminal) throws IOException {
        // no idea why, first character here gets eaten
        System.out.println(" Welcome! This maze game scales in size and difficulty based on your window size.");
        System.out.println("Please take this chance to resize your window before the maze is generated. Press any key when done.");
        System.out.println("E is the exit, M is a monster, K is a key. You need all keys to exit.");
        System.out.println("How to play: wasd to move, q or ^C to quit, some typing may be involved! Have fun!");
        terminal.readInput();

        KeyStroke in;
        do {
            System.out.println(this);
            while (!this.thingsToPrint.isEmpty()) {
                System.out.print(thingsToPrint.pop());
                System.out.print(" ");
            }
            System.out.print("\n");

            if (this.hasWon) {
                this.timeToQuit = true;
            }

            in = terminal.readInput();

            if (in.getKeyType() != KeyType.Character) {
                continue;
            }

            switch (in.getCharacter()) {
                case 'q':
                    this.timeToQuit = true;
                    continue;
                case 'w':
                    this.tryMove(WallDirection.UP, terminal);
                    break;
                case 'a':
                    this.tryMove(WallDirection.LEFT, terminal);
                    break;
                case 's':
                    this.tryMove(WallDirection.DOWN, terminal);
                    break;
                case 'd':
                    this.tryMove(WallDirection.RIGHT, terminal);
                    break;
            }
        } while (!this.timeToQuit);
    }
}