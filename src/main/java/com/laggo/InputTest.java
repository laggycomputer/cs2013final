package com.laggo;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class InputTest {
    public static void main(String[] args) throws IOException {
//        System.setProperty("java.awt.headless", "true");
//        try (Terminal terminal = new CygwinTerminal(System.in, System.out, StandardCharsets.UTF_16)) {
        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            TerminalSize size = terminal.getTerminalSize();
            // these dimensions leave room for the additional text pop-ups and account for the wide printing
            GameBoard board = new GameBoard(size.getColumns() / 4 - 1, size.getRows() / 2 - 2);
            board.run(terminal);
        }
    }
}
