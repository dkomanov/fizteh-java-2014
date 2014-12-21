package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Created by Lenovo on 20.10.2014.
 */
public class DropCommand implements CommandsForTables {
    public void execute(String[] args, MyTableProvider provider) throws MyException {

        if (args.length == 1) {
            throw new MyException("drop: not enough arguments");
        }
        if (args.length > 2) {
            throw new MyException("drop: too many arguments");
        }

        String name = args[1];

        try {
            MyTable deletingTable = provider.tables.get(name);
        } catch (NullPointerException e) {
            throw new MyException("Can't drop " + name);
        }

        if (provider.usingTable != null && name.equals(provider.usingTable.getName())) {
            provider.usingTable = null;
        }
        drop(args[1], provider.getMainDirectory().toPath().resolve(name));
        provider.tables.remove(name);
        System.out.println("dropped");

    }

    void drop(String argument, Path path) throws MyException {
        if (Files.isDirectory(path)) {
            dropFinal(argument, path);
        }
    }

    void dropFinal(String argument, Path path) throws MyException {
        if (!Files.exists(path)) {
            throw new MyException(argument + " not exists");
        }

        File dir = new File(path.toString());
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    throw new MyException("I/O error occurs while removing " + file.toPath().toString());
                }
            } else {
                dropFinal(argument, file.toPath());
            }
        }

        try {
            Files.delete(path);
        } catch (NoSuchFileException e) {
            throw new MyException(argument + " not exists");
        } catch (IOException e) {
            throw new MyException("Cannot remove file or directory " + path.toString());
        }
    }

    public String getName() {
        return "drop";
    }
}
