package com.laggo;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class InputTest {
    public static void main(String[] args) throws IOException {
//        System.setProperty("java.awt.headless", "true");
//        try (Terminal terminal = new UnixTerminal()) {
        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            TerminalSize size = terminal.getTerminalSize();
            GameBoard board = new GameBoard(size.getColumns() / 4 - 1, size.getRows() / 2 - 1);
            board.run(terminal);
        }
    }
}
