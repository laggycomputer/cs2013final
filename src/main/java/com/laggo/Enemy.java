package com.laggo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends BoardObject {
    private static final List<String> NAMES = new ArrayList<>(List.of(new String[]{
            "Dylan", "Dante", "sans undertale",
            "Smiley Face", "Mr. Onomatopoeia", "racecar",
            "Isopropanal", "BLT", "Ramen Noodles",
            "Gordon Freeman"}));

    private final String name;

    public Enemy() {
        super();
        this.name = Enemy.getRandomName();
    }

    private static String getRandomName() {
        return NAMES.remove((int) (Math.random() * NAMES.size()));
    }

    @Override
    public void onWalkedOn(GameBoard board, TerminalInterface terminalInterface) throws IOException {
        System.out.println("You've met an enemy! Type its name backwards to... deal with it!");
        int numCorrectCharacters = 0;
        char[] progress = new char[this.name.length()];
        for (int i = 0; i < this.name.length(); i++) {
            progress[i] = ' ';
        }

        while (numCorrectCharacters < this.name.length()) {
            System.out.println("Name    : " + this.name);
            System.out.println("Progress: " + new String(progress));
            System.out.println("Type the rightmost missing character!");

            String strIn = terminalInterface.getNextString();

            if (strIn.length() != 1 || strIn.codePointAt(0) != this.name.codePointAt(this.name.length() - 1 - numCorrectCharacters)) {
                System.out.println("You got it wrong! You've got to restart now.");
                for (int i = 0; i < this.name.length(); i++) {
                    progress[i] = ' ';
                }
                numCorrectCharacters = 0;
            } else {
                progress[this.name.length() - 1 - numCorrectCharacters] = 'x';
                numCorrectCharacters++;
            }
        }

        board.addThingToPrint("Enemy " + this.name + " defeated!");
        this.isVisible = false;
    }

    @Override
    public String getIcon() {
        return this.isVisible ? "M" : " ";
    }
}
