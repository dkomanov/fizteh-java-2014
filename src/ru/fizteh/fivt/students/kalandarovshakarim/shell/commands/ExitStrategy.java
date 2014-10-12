/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

/**
 *
 * @author Shakarim
 */
public abstract class ExitStrategy<Type> extends AbstractCommand<Type> {

    protected int status;

    public ExitStrategy(Type context) {
        super("exit", 0, context);
        this.status = 0;
    }

    public void setExitStatus(int status) {
        this.status = status;
    }

    @Override
    public void exec(String[] args) {
        onExit();
        System.exit(status);
    }

    protected abstract void onExit();
}
