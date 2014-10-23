package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;
import ru.fizteh.fivt.students.ekaterina_pogodina.shell.Rm;

public class Drop extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        if (args.length < 2) {
            table.missingOperand(args[0]);
        }
        if (args.length > 2) {
            table.manyArgs(args[0]);
        }
        boolean flag = table.drop(args[1]);
        /*if (flag) {
            Rm remove = new Rm();
            String[] s = new String[3];
            s[0] = "rm";
            s[1] = "-r";
            s[2] = table.path.resolve(args[1]).toString();
            remove.run(s, true, 2);.
            System.out.print("dropped");
        }*/
    }
}
