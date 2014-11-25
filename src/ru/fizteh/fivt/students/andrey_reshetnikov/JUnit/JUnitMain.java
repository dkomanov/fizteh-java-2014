package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.*;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

public class JUnitMain {
    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("No database directory name specified");
            System.exit(1);
        }
        boolean interactive = false;
        try {
            JUnitDataBaseDir jUnitDbDir = new JUnitDataBaseDir(path);
            CommandGetter getter;
            if (args.length == 0) {
                interactive = true;
                getter = new InteractiveGetter();
            } else {
                getter = new BatchGetter(args);
            }
            boolean exitStatus = false;
            do {
                try {
                    String s = getter.nextCommand();
                    JUnitCommand command = JUnitCommand.fromString(s);
                    command.execute(jUnitDbDir);
                } catch (ExitCommandException e) {
                    exitStatus = true;
                } catch (NoSuchElementException e) {
                    System.err.println("No such element");
                    System.err.flush();
                } catch (Exception e) {
                    if (interactive) {
                        System.err.println(e.getMessage());
                        System.err.flush();
                    } else {
                        throw e;
                    }
                }
            } while (!exitStatus);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error: UTF-8 encoding is not supported");
            System.exit(1);
        } catch (TwoSameKeysException e) {
            System.out.println("Two same keys in database file");
            System.exit(1);
        } catch (CannotCreateNewDatabaseFileException e) {
            System.out.println("Can not create new database file");
            System.exit(1);
        } catch (MyDirectoryNotEmptyException e) {
            System.out.println("Can not remove table directory. Redundant files");
            System.exit(1);
        } catch (CannotDeleteDataBaseFileException e) {
            System.out.println(e.message);
            System.exit(1);
        } catch (FileFromDataBaseIsNotDirectoryException e) {
            System.out.println(e.childName + " from databases directory is not a directory");
            System.exit(1);
        } catch (ParentDirectoryIsNotDirectory e) {
            System.out.println("Specified fizteh.db.dir is not a directory");
            System.exit(1);
        } catch (CannotCreateDirectoryException e) {
            System.out.println("Can not create working directory");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
