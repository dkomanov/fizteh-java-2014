/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author Shakarim
 */
public class LsCommand extends AbstractCommand<ShellUtils> {

    public LsCommand(ShellUtils context) {
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
