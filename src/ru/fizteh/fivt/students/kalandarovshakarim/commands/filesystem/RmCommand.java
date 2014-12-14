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
public class RmCommand extends AbstractCommand<FileSystemUtils> {

    public RmCommand(FileSystemUtils context) {
        super("rm", 1, context);
    }

    @Override
    public void exec(String[] args) throws FileNotFoundException, IOException {
        context.rm(args[0], (args.length == 2));
    }
}
