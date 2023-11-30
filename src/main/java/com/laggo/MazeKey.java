package com.laggo;

public class MazeKey extends BoardObject {
    @Override
    public void onWalkedOn(GameBoard board, TerminalInterface terminalInterface) {
        this.isVisible = false;
        if (board.hasPickedUpAllKeys()) {
            board.addThingToPrint("You've picked up both keys, head to the exit!");
        } else {
            board.addThingToPrint("You've picked up a key, you need one more to leave!");
        }
    }

    @Override
    public String getIcon() {
        return this.isVisible ? "K" : " ";
    }
}
