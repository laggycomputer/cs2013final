package com.laggo;

import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public abstract class BoardObject {
    public boolean isStopping;
    public boolean isVisible = true;

    public abstract void onWalkedOn(GameBoard board, TerminalInterface terminalInterface) throws IOException;

    public abstract String getIcon();
}
