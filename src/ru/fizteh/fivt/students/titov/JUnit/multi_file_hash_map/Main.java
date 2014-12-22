package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.Shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final String PROJECTPROPERTY = "fizteh.db.dir";

    public static void main(String[] args) {

        if (System.getProperty(PROJECTPROPERTY) == null) {
            System.err.println("we need working directory");
            System.exit(6);
        }
        Path dataBaseDirectory
                = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty(PROJECTPROPERTY));

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

        Shell<MFileHashMap> myShell = new Shell<>(myMFileHashMap);
        setUpShell(myShell);

        if (args.length > 0) {
            allRight = myShell.batchMode(args);
        } else {
            allRight = myShell.interactiveMode();
        }
        if (allRight) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }


    public static void setUpShell(Shell<MFileHashMap> myShell) {

        myShell.addCommand(new CreateCommand());
        myShell.addCommand(new DropCommand());
        myShell.addCommand(new UseCommand());
        myShell.addCommand(new GetDistributeCommand());
        myShell.addCommand(new PutDistributeCommand());
        myShell.addCommand(new ListDistributeCommand());
        myShell.addCommand(new RemoveDistributeCommand());
        myShell.addCommand(new ShowTablesCommand());
        myShell.addCommand(new RollbackCommand());
        myShell.addCommand(new CommitCommand());
        myShell.addCommand(new SizeCommand());
        myShell.addCommand(new ExitCommand());
    }
}

