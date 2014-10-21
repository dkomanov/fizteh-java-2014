/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author Shakarim
 */
public class CpCommand extends AbstractCommand<ShellUtils> {

    public CpCommand(ShellUtils context) {
        super("cp", 2, context);
    }

    @Override
    public void exec(String[] args) throws NoSuchFileException, IOException {
        context.cp(args[0], args[1], (args.length == 3));
    }
}
