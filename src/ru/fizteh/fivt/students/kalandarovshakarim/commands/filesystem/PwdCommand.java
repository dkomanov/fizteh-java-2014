/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands.filesystem;

import ru.fizteh.fivt.students.kalandarovshakarim.commands.AbstractCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.filesystem.FileSystemUtils;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Shakarim
 */
public class PwdCommand extends AbstractCommand<FileSystemUtils> {

    public PwdCommand(FileSystemUtils context) {
        super("pwd", 0, context);
    }

    @Override
    public void exec(String[] args) throws FileNotFoundException, IOException {
        System.out.println(context.getCwd());
    }

}
