package com.laggo;

import java.io.IOException;

public abstract class BoardObject {
    public boolean isVisible = true;

    public abstract void onWalkedOn(GameBoard board, TerminalInterface terminalInterface) throws IOException;

    public abstract String getIcon();
}
