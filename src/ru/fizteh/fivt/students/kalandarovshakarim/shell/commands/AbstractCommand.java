/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

/**
 *
 * @author Shakarim
 * @param <State>
 */
public abstract class AbstractCommand<State> implements Command<State> {

    private final String name;
    private final int argNum;

    public AbstractCommand() {
        this.name = null;
        this.argNum = 0;
    }

    public AbstractCommand(String name, int argNum) {
        this.name = name;
        this.argNum = argNum;
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
