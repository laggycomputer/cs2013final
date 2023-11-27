package com.laggo;

import com.googlecode.lanterna.terminal.Terminal;

public class MazeKey extends BoardObject {
    public MazeKey(BoardLocation loc) {
        super(loc);
        this.isStopping = false;
    }

    @Override
    public void onWalkedOn(GameBoard board, Terminal terminal) {
        this.isVisible = false;
    }

    @Override
    public String getIcon() {
        return this.isVisible ? "K" : " ";
    }
}
