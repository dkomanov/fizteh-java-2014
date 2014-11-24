/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands.filesystem;

import ru.fizteh.fivt.students.kalandarovshakarim.commands.AbstractCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.filesystem.FileSystemUtils;
import java.io.IOException;

/**
 *
 * @author Shakarim
 */
public class LsCommand extends AbstractCommand<FileSystemUtils> {

    public LsCommand(FileSystemUtils context) {
        super("ls", 0, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        String[] names = context.listFiles();
        for (String s : names) {
            if (s.charAt(0) != '.') {
                System.out.println(s);
            }
        }
    }
}
