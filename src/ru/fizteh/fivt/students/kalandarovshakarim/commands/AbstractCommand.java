/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands;

import java.io.InputStream;
import java.io.PrintStream;

/**
 *
 * @author Shakarim
 * @param <Type>
 */
public abstract class AbstractCommand<Type> implements Command {

    private final String name;
    private final int argNum;
    protected final Type context;
    protected final InputStream in;
    protected final PrintStream out;
    protected final PrintStream err;

    public AbstractCommand(String name, int argNum, Type context) {
        this(name, argNum, context, System.in, System.out, System.err);
    }

    public AbstractCommand(String name, int argNum, Type context, InputStream in, PrintStream out, PrintStream err) {
        this.name = name;
        this.argNum = argNum;
        this.context = context;
        this.in = in;
        this.out = out;
        this.err = err;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgsNum() {
        return argNum;
    }
}
