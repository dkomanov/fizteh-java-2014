package ru.fizteh.fivt.students.titov.FileMap;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DbMain {
    public static void main(String[] args) {
        String dataBasePath = System.getProperty("db.file");
        if (dataBasePath == null || !Files.exists(Paths.get(dataBasePath))) {
            System.out.println("cannot access the database");
            System.exit(1);
        }
        FileMap myFileMap = new FileMap(dataBasePath);
        boolean errorOccuried = false;
        if (!myFileMap.init()) {
            System.exit(3);
        }

        Shell<FileMap> myShell = new Shell<>(myFileMap);
        myShell.addCommand(new FmCommandGet());
        myShell.addCommand(new FmCommandList());
        myShell.addCommand(new FmCommandPut());
        myShell.addCommand(new FmCommandRemove());

        if (args.length > 0) {
            if (!myShell.packetMode(args)) {
                errorOccuried = true;
            }
        } else {
            if (!myShell.interactiveMode()) {
                errorOccuried = true;
            }
        }
        if (errorOccuried) {
            System.exit(2);
        } else {
            System.exit(0);
        }
    }
}
