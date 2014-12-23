package ru.fizteh.fivt.students.sautin1.telnet;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by sautin1 on 12/16/14.
 */
public abstract class ShellState {
    protected BufferedReader inStream;
    protected PrintWriter outStream;

    public ShellState(BufferedReader inStream, PrintWriter outStream) {
        this.inStream = inStream;
        this.outStream = outStream;
    }

    public BufferedReader getInStream() {
        return inStream;
    }

    public PrintWriter getOutStream() {
        return outStream;
    }
}
