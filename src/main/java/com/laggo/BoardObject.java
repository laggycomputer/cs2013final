package com.laggo;

import com.googlecode.lanterna.terminal.Terminal;

public abstract class BoardObject {
    final BoardLocation loc;
    public boolean isStopping;
    public boolean isVisible = true;

    public abstract void onWalkedOn(GameBoard board, Terminal terminal);

    public BoardLocation getLoc() {
        return this.loc;
    }

    public BoardObject() {
        this(new BoardLocation(0, 0));
    }

    public BoardObject(BoardLocation loc) {
        this.loc = loc;
    }

    public abstract String getIcon();
}
