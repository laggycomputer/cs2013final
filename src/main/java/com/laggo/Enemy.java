package com.laggo;

import com.googlecode.lanterna.terminal.Terminal;

public class Enemy extends BoardObject{
    private final String name;

    public Enemy(BoardLocation loc, String name) {
        super(loc);
        this.name = name;
    }

    @Override
    public void onWalkedOn(GameBoard board, Terminal terminal) {
//        ...
    }

    @Override
    public String getIcon() {
        return "E";
    }
}
