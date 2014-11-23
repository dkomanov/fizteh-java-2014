package ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.FileMap.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static Shell<MFileHashMap> shellForThreadsFirst;
    public static Shell<MFileHashMap> shellForThreadsSecond;

    public static class MyRunnableFirst implements Runnable {
        @Override
        public void run() {
            shellForThreadsFirst.interactiveMode();
        }
    }

    public static class MyRunnableSecond implements Runnable {
        @Override
        public void run() {
            shellForThreadsSecond.interactiveMode();
        }
    }

    public static void main(String[] args) {

        if (System.getProperty("fizteh.db.dir") == null) {
            System.out.println("we need working directory");
            System.exit(6);
        }
        Path dataBaseDirectory
                = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));

        boolean allRight = true;
        if (Files.exists(dataBaseDirectory)) {
            if (!Files.isDirectory(dataBaseDirectory)) {
                System.err.println("working directory is a file");
                System.exit(4);
            }
        } else {
            try {
                Files.createDirectory(dataBaseDirectory);
            } catch (IOException e) {
                System.err.println("error while creating directory");
                allRight = false;
            } finally {
                if (!allRight) {
                    System.exit(5);
                }
            }
        }

        MFileHashMap myMFileHashMap = new MFileHashMap(dataBaseDirectory.toString());
        if (!myMFileHashMap.init()) {
            System.exit(3);
        }

        shellForThreadsFirst = new Shell<MFileHashMap>(myMFileHashMap);
        shellForThreadsSecond = new Shell<MFileHashMap>(myMFileHashMap);
        setUpShell(shellForThreadsFirst);
        setUpShell(shellForThreadsSecond);

        /*if (args.length > 0) {
            allRight = myShell.packetMode(args);
        } else {
            allRight = myShell.interactiveMode();
        }
        if (allRight) {
            System.exit(0);
        } else {
            System.exit(1);
        }*/

        Thread firstThread = new Thread(new MyRunnableFirst());
        Thread secondThread = new Thread(new MyRunnableSecond());
        firstThread.start();
        secondThread.start();
    }


    public static void setUpShell(Shell<MFileHashMap> myShell) {
        myShell.addCommand(new CommandCreate());
        myShell.addCommand(new CommandDrop());
        myShell.addCommand(new CommandUse());
        myShell.addCommand(new CommandGetDistribute());
        myShell.addCommand(new CommandPutDistribute());
        myShell.addCommand(new CommandListDistribute());
        myShell.addCommand(new CommandRemoveDistribute());
        myShell.addCommand(new CommandShowTables());
        myShell.addCommand(new CommandRollback());
        myShell.addCommand(new CommandCommit());
        myShell.addCommand(new CommandSize());
    }
}
