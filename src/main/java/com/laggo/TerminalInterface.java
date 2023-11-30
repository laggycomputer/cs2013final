package com.laggo;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.*;

public class TerminalInterface {
    private Terminal lanternaTerminal = null;
    private BufferedReader terminalReader = null; // Scanner would ignore whitespace
    private boolean usingLanterna = false;

    public TerminalInterface() {
        try {
            this.lanternaTerminal = new UnixTerminal();
            this.usingLanterna = true;
        } catch (Exception e) {
            this.terminalReader = new BufferedReader(new InputStreamReader( System.in));
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
            return this.terminalReader.readLine();
        }
    }

    public boolean isUsingLanterna() {
        return this.usingLanterna;
    }

    public int getTerminalWidth() throws IOException {
        return this.usingLanterna ? lanternaTerminal.getTerminalSize().getColumns() : 120;
    }

    public int getTerminalHeight() throws IOException {
        return this.usingLanterna ? lanternaTerminal.getTerminalSize().getRows() : 30;
    }
}
