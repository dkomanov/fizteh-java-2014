/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author Shakarim
 */
public class CatCommand extends AbstractCommand<ShellUtils> {

    public CatCommand(ShellUtils context) {
        super("cat", 1, context);
    }

    @Override
    public void exec(String[] args) throws FileNotFoundException, IOException {
        context.cat(args[0]);
    }
}
