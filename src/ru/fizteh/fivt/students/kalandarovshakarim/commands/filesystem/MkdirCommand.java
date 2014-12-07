/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands.filesystem;

import ru.fizteh.fivt.students.kalandarovshakarim.commands.AbstractCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.filesystem.FileSystemUtils;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 *
 * @author Shakarim
 */
public class MkdirCommand extends AbstractCommand<FileSystemUtils> {

    public MkdirCommand(FileSystemUtils context) {
        super("mkdir", 1, context);
    }

    @Override
    public void exec(String[] args) throws NoSuchFileException, IOException {
        context.mkDir(args[0]);
    }
}
