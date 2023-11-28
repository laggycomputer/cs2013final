package com.laggo;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.IOException;
import java.util.Scanner;

public class TerminalInterface {
    private Terminal lanternaTerminal = null;
    private Scanner scanner = null;
    private boolean usingLanterna = false;

    public TerminalInterface() {
        try {
            this.lanternaTerminal = new UnixTerminal();
            this.usingLanterna = true;
        } catch (Exception e) {
            this.scanner = new Scanner(System.in);
        }
    }

    public String getNextString() throws IOException {
        if (this.usingLanterna) {
            KeyStroke in;
            do {
                in = this.lanternaTerminal.readInput();
            } while (in == null || in.getKeyType() != KeyType.Character);

            return in.getCharacter().toString();
        } else {
            return this.scanner.next();
        }
    }

    public boolean isUsingLanterna() {
        return this.usingLanterna;
    }
}
