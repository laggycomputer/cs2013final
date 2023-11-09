package com.laggo;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.IOException;

public class InputTest {
    public static void main(String[] args) throws IOException {
//        try (Terminal terminal = new UnixTerminal()) {
        try (Terminal terminal = new DefaultTerminalFactory().createTerminalEmulator()) {
            GameBoard board = new GameBoard(15, 5);
            System.out.println(board);
//            KeyStroke keyPressed = terminal.readInput();
//            System.out.println("keyPressed: " + keyPressed.getKeyType());

            KeyStroke in;
            do {
                in = terminal.readInput();
                System.out.println(in);
            } while (in.getCharacter() != 'q');
        }
    }
}
