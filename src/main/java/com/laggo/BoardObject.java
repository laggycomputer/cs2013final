package com.laggo;

public abstract class BoardObject {
    private final BoardLocation loc;
    public boolean isStopping;
    public boolean isVisible = true;

    public abstract void onWalkedOn();

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
