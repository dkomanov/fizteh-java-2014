package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.util.Vector;

public class ParseArguements {
    public static Vector<String[]> execute(String[] args) {
        Vector<String[]> res = new Vector<String[]>();
        Vector<Vector<String>> vectorArgv = new Vector<Vector<String>>();

        boolean newCommand = true;
        int commandCnt = 0;
        for (int i = 0; i < args.length; ++i) {
            if (newCommand) {
                vectorArgv.add(new Vector<String>());
                newCommand = false;
            }
            if (args[i].endsWith(";")) {
                vectorArgv.elementAt(commandCnt).add(
                        args[i].equals(";") ? " " : args[i].substring(0,
                                args[i].length() - 1));
                ++commandCnt;
                newCommand = true;
            } else {
                vectorArgv.elementAt(commandCnt).add(args[i]);
            }
        }
        if (args[args.length - 1].endsWith(";")) {
            --commandCnt;
        }
        for (int i = 0; i <= commandCnt; ++i) {
            String[] arrayArgv = new String[vectorArgv.elementAt(i).size()];
            for (int j = 0; j < vectorArgv.elementAt(i).size(); ++j) {
                arrayArgv[j] = vectorArgv.elementAt(i).elementAt(j);
            }
            res.add(arrayArgv);
        }
        return res;
    }
}
