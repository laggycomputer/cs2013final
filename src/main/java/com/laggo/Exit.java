package com.laggo;

public class Exit extends BoardObject {
    @Override
    public void onWalkedOn(GameBoard board, TerminalInterface terminalInterface) {
        if (board.hasPickedUpAllKeys()) {
            board.hasWon = true;
            board.addThingToPrint("You've beaten the maze! Press any key to continue.");
        } else {
            board.addThingToPrint("Not done yet! Pick up all keys first.");
        }
    }

    @Override
    public String getIcon() {
        return "E";
    }
}
