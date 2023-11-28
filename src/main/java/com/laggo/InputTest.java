package com.laggo;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class InputTest {
    public static void main(String[] args) throws IOException {
        TerminalInterface terminalInterface = new TerminalInterface();
        // these dimensions leave room for the additional text pop-ups and account for the wide printing
        GameBoard board = new GameBoard(
                terminalInterface.getTerminalWidth() / 4 - 1, terminalInterface.getTerminalHeight() / 2 - 2);
        board.run(terminalInterface);
    }
}
