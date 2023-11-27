package com.laggo;

import com.googlecode.lanterna.terminal.Terminal;

public class Exit extends BoardObject {
    @Override
    public void onWalkedOn(GameBoard board, Terminal terminal) {
        if (board.hasPickedUpAllKeys()) {
            board.timeToQuit = true;
            board.addThingToPrint("You did it!");
        } else {
            board.addThingToPrint("Not done yet! Pick up all keys first.");
        }
    }

    @Override
    public String getIcon() {
        return "E";
    }
}
