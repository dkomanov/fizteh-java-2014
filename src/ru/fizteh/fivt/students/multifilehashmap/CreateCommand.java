package ru.fizteh.fivt.students.multifilehashmap;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Lenovo on 20.10.2014.
 */
public class CreateCommand implements CommandsForTables {
    public void execute(String[] args, MultiFileHashMap multiFileHashMap) throws MyException {

        if (args.length == 1) {
            throw new MyException("create: not enough arguments");
        }
        if (args.length > 2) {
            throw new MyException("create: too many arguments");
        }

        try {
            Path newDirectory = Files.createDirectory(MultiFileHashMapMain.rootPath.resolve(args[1]));
            FileMap filemap = new FileMap(newDirectory);
            multiFileHashMap.put(args[1], filemap);
            System.out.println("created");
        } catch (FileAlreadyExistsException e) {
            System.out.println(args[1] + " exists");
        } catch (IOException e) {
            throw new MyException("mkdir: directory doesn't exist");
        }

    }

    public String getName() {
        return "create";
    }
}
