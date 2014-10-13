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

    public ExitStrategy(Type context) {
        super("exit", 0, context);
    }

    @Override
    public void exec(String[] args) {
        onExit();
        System.exit(args.length);
    }

    protected abstract void onExit();
}
