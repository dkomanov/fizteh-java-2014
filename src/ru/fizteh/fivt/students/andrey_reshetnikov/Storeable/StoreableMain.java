package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.*;

import java.io.UnsupportedEncodingException;

public class StoreableMain {
    public static void main(String[] args) {
        run(args);
    }

    private static void run(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("No database directory name specified");
            System.exit(1);
        }
        boolean interactive = false;
        try {
            TableProviderFactory factory = new MyStoreableTableProviderFactory();
            MyStoreableTableProvider dbDir = (MyStoreableTableProvider) factory.create(path);
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
                    Command command = Command.fromString(s);
                    command.execute(dbDir);
                } catch (ExitCommandException e) {
                    exitStatus = true;
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
            System.err.println("Error: UTF-8 encoding is not supported");
            System.exit(1);
        } catch (TwoSameKeysException e) {
            System.err.println("Two same keys in database file");
            System.exit(1);
        } catch (CannotCreateNewDatabaseFileException e) {
            System.err.println("Can not create new database file");
            System.exit(1);
        } catch (MyDirectoryNotEmptyException e) {
            System.err.println("Can not remove table directory. Redundant files");
            System.exit(1);
        } catch (CannotDeleteDataBaseFileException e) {
            System.err.println(e.message);
            System.exit(1);
        } catch (FileFromDataBaseIsNotDirectoryException e) {
            System.err.println(e.childName + " from databases directory is not a directory");
            System.exit(1);
        } catch (ParentDirectoryIsNotDirectory e) {
            System.err.println("Specified fizteh.db.dir is not a directory");
            System.exit(1);
        } catch (CannotCreateDirectoryException e) {
            System.err.println("Can not create working directory");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
