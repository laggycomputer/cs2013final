package com.laggo;

public class MazeKey extends BoardObject {
    public MazeKey(BoardLocation loc) {
        super(loc);
        this.isStopping = false;
    }

    @Override
    public void onWalkedOn() {
        this.isVisible = false;
    }

    @Override
    public String getIcon() {
        return this.isVisible ? "K" : " ";
    }
}
