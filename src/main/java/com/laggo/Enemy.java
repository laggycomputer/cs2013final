package com.laggo;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Enemy extends BoardObject {
    private final String name;

    public Enemy(BoardLocation loc) {
        super(loc);
        this.name = Enemy.getRandomName();
    }

    private static String getRandomName() {
        final String[] NAME_BANK = new String[]{"Dylan", "Dante", "sans undertale", "Smiley Face", "Mr. Onomatopoeia", "Chandler"};
        return NAME_BANK[(int) (Math.random() * NAME_BANK.length)];
    }

    @Override
    public void onWalkedOn(GameBoard board, Terminal terminal) throws IOException {
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

            KeyStroke in;
            do {
                in = terminal.readInput();
            } while (in == null || in.getKeyType() != KeyType.Character);

            if (in.getCharacter() != this.name.codePointAt(this.name.length() - 1 - numCorrectCharacters)) {
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
    }

    @Override
    public String getIcon() {
        return "E";
    }
}
