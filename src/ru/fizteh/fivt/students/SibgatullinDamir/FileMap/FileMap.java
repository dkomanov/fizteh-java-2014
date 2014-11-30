package ru.fizteh.fivt.students.SibgatullinDamir.FileMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by Lenovo on 10.10.2014.
 */
public class FileMap extends HashMap<String, String> {

    Path db;

    FileMap(Path path) {
        db = path;
        try {
            read();
        } catch (MyException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void read() throws MyException {
        clear();
        if (Files.exists(db)) {
            try {
                DataInputStream inputStream = new DataInputStream(Files.newInputStream(db));
                clear();

                while (inputStream.available() > 0) {
                    int keyLen = inputStream.readInt();

                    if (inputStream.available() < keyLen || keyLen < 0) {
                        throw new MyException("Wrong key size");
                    }

                    byte[] key = new byte[keyLen];
                    inputStream.read(key, 0, keyLen);

                    int valLen = inputStream.readInt();

                    if (inputStream.available() < valLen || valLen < 0) {
                        throw new MyException("Wrong value size");
                    }

                    byte[] value = new byte[valLen];
                    inputStream.read(value, 0, valLen);

                    put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                }
            } catch (IOException e) {
                throw new MyException("Reading failed");
            }
        }
    }

    public void write() throws MyException {
        try {
            DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(db));

            for (Entry<String, String> entry : entrySet()) {
                byte[] key = entry.getKey().getBytes("UTF-8");
                byte[] value = entry.getValue().getBytes("UTF-8");
                outputStream.writeInt(key.length);
                outputStream.write(key);
                outputStream.writeInt(value.length);
                outputStream.write(value);
            }
        } catch (IOException e) {
            throw new MyException("Writing failed");
        }
    }
}
