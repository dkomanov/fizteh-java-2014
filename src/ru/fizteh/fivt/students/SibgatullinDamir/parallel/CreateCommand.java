package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lenovo on 20.10.2014.
 */
public class CreateCommand implements CommandsForTables {
    public void execute(String[] args, MyTableProvider provider) throws MyException {

        if (args.length < 3) {
            throw new MyException("create: not enough arguments");
        }

        String name = args[1];
        Path newDirectory = null;
        if (provider.tables.containsKey(name)) {
            throw new MyException(name + " exists");
        }
        try {
            newDirectory = Files.createDirectory(provider.getMainDirectory().toPath().resolve(name));
        } catch (IOException e) {
            throw new MyException("create: cannot create the directory");
        }
        FileMap filemap = new FileMap(name);
        List<Class<?>> columnTypes = new LinkedList<>();
        String line = "";
        for (int i = 2; i < args.length; ++i) {
            if (args.length == 3 && args[i].startsWith("(")  && args[i].endsWith(")")) {
                line = args[i].substring(1, args[i].length() - 1);
            } else {
                if (i == 2 && args[i].startsWith("(")) {
                    line = args[i].substring(1);
                } else if (i == args.length - 1 && args[i].endsWith(")")) {
                    line = args[i].substring(0, args[i].length() - 1);
                } else {
                    line = args[i];
                }
            }
            try {
                columnTypes.add(TypeTransformer.typeFromName(line));
            } catch (IOException e) {
                throw new MyException("wrong type: " + line);
            }
        }
        String typesInString = TypeTransformer.stringFromTypeList(columnTypes);
        File signature = new File(new File(provider.getMainDirectory(), name), "signature.tsv");
        try {
            if (!signature.createNewFile()) {
                throw new IOException("cannot create signature file");
            }
            provider.writeSignature(signature, typesInString);
        } catch (IOException e) {
            throw new MyException("create: cannot create signature file");
        }
        MyTable table = new MyTable(name, filemap, columnTypes, provider, newDirectory);
        provider.tables.put(name, table);
        provider.signatures.put(name, signature);
        System.out.println("created");
    }

    public String getName() {
        return "create";
    }
}
