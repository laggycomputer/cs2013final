package com.laggo;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.IOException;

public class InputTest {
    public static void main(String[] args) throws IOException {
//        System.setProperty("java.awt.headless", "true");
        try (Terminal terminal = new UnixTerminal()) {
//        try (Terminal terminal = new DefaultTerminalFactory().createHeadlessTerminal()) {
            TerminalSize size = terminal.getTerminalSize();
            GameBoard board = new GameBoard(size.getColumns() / 4 - 1, size.getRows() / 2 - 1);

            boolean timeToQuit = false;
            KeyStroke in;
            do {
                System.out.println(board);
                in = terminal.readInput();
//                System.out.println(in);

                if (in.getKeyType() != KeyType.Character) {
                    continue;
                }

                switch (in.getCharacter()) {
                    case 'q':
                        timeToQuit = true;
                        continue;
                    case 'w':
                        board.tryMove(WallDirection.UP);
                        break;
                    case 'a':
                        board.tryMove(WallDirection.LEFT);
                        break;
                    case 's':
                        board.tryMove(WallDirection.DOWN);
                        break;
                    case 'd':
                        board.tryMove(WallDirection.RIGHT);
                        break;
                }
            } while (!timeToQuit);
//            KeyStroke keyPressed = terminal.readInput();
//            System.out.println("keyPressed: " + keyPressed.getKeyType());
        }
    }
}
