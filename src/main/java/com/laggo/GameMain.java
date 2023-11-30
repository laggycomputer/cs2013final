package com.laggo;

import java.io.IOException;

public class GameMain {
    public static void main(String[] args) throws IOException {
        TerminalInterface terminalInterface = new TerminalInterface();
        // these dimensions leave room for the additional text pop-ups and account for the wide printing
        GameBoard board = new GameBoard(
                terminalInterface.getTerminalWidth() / 4 - 1, terminalInterface.getTerminalHeight() / 2 - 2);
        board.run(terminalInterface);
    }
}
