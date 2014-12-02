package ru.fizteh.fivt.students.SibgatullinDamir.junit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Lenovo on 10.10.2014.
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Lenovo on 10.10.2014.
 */
public class FileMap extends HashMap<String, String> {

    private Path location;
    String name;
    Set<String> changedKeys = new LinkedHashSet<>();

    FileMap() {
    }

    FileMap(Path path) {
        location = path;
        name = path.getFileName().toString();
        try {
            read();
        } catch (MyException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void read() throws MyException {
        clear();

        File currentDir = new File(location.toString());
        File[] directories = currentDir.listFiles();

        for (File directory: directories) {
            if (!directory.isDirectory()) {
                throw new MyException("Waste file in " + directory.toString());
            }

            File dir = new File(directory.toString());
            File[] files = dir.listFiles();

            if (files.length == 0) {
                removeFromTable(dir.toPath());
                continue;
            }

            for (File file : files) {

                if (file.length() == 0) {
                    removeFromTable(file.toPath());
                    continue;
                }

                try {
                    DataInputStream inputStream = new DataInputStream(Files.newInputStream(file.toPath()));

                    while (inputStream.available() > 0) {
                        int keyLen = inputStream.readInt();

                        if (inputStream.available() < keyLen || keyLen < 0) {
                            throw new MyException("Wrong key size in " + file.toString());
                        }

                        byte[] key = new byte[keyLen];
                        inputStream.read(key, 0, keyLen);

                        int valLen = inputStream.readInt();

                        if (inputStream.available() < valLen || valLen < 0) {
                            throw new MyException("Wrong value size in " + file.toString());
                        }

                        byte[] value = new byte[valLen];
                        inputStream.read(value, 0, valLen);

                        put(new String(key, "UTF-8"), new String(value, "UTF-8"));

                    }

                    inputStream.close();
                } catch (IOException e) {
                    throw new MyException("Reading failed");
                }
            }
        }
    }

    void removeFromTable(Path path) throws MyException {
        try {
            if (Files.isDirectory(path)) {
                removeFromTableFinal(path);
            } else {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new MyException("Cannot remove empty file or directory" + path.toString());
        }
    }

    void removeFromTableFinal(Path path) throws MyException {
        if (!Files.exists(path)) {
            throw new MyException("Cannot remove empty file or directory" + path.toString());
        }

        File dir = new File(path.toString());
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new MyException("I/O error occurs while removing " + file.toPath().toString());
                }
            } else {
                removeFromTableFinal(file.toPath());
            }
        }
    }

    public void write() throws MyException {
        try {

            for (Entry<String, String> entry : entrySet()) {

                int hashcode = entry.getKey().hashCode();

                int ndirectory = hashcode % 16;
                Path destinationDirectory = location.resolve(Integer.toString(ndirectory));
                if (!Files.exists(destinationDirectory)) {
                    Files.createDirectory(destinationDirectory);
                }

                int nfile = hashcode / 16 % 16;
                Path destinationFile = destinationDirectory.resolve(Integer.toString(nfile));
                if (!Files.exists(destinationFile)) {
                    Files.createFile(destinationFile);
                }

                DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(destinationFile));

                byte[] key = entry.getKey().getBytes("UTF-8");
                byte[] value = entry.getValue().getBytes("UTF-8");
                outputStream.writeInt(key.length);
                outputStream.write(key);
                outputStream.writeInt(value.length);
                outputStream.write(value);

                outputStream.close();
            }
        } catch (IOException e) {
            throw new MyException("Writing failed");
        }
    }
}
