/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

/**
 *
 * @author Shakarim
 * @param <Type>
 */
public abstract class AbstractCommand<Type> implements Command {

    private final String name;
    private final int argNum;
    protected final Type context;

    public AbstractCommand() {
        this.name = null;
        this.argNum = 0;
        this.context = null;
    }

    public AbstractCommand(String name, int argNum, Type context) {
        this.name = name;
        this.argNum = argNum;
        this.context = context;
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
