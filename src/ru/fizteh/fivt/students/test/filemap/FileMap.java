package ru.fizteh.fivt.students.deserg.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by deserg on 04.10.14.
 */
public class FileMap extends HashMap<String, String> {

    Path dbPath;

    FileMap(Path path) {
        dbPath = path;
        try {
            read();
        } catch (MyException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    public void read() {

        clear();

        if (Files.exists(dbPath)) {
            try (DataInputStream is = new DataInputStream(Files.newInputStream(dbPath))) {

                clear();
                while (is.available() > 0) {

                    int keyLen = is.readInt();
                    if (is.available() < keyLen) {
                        throw new MyException("Wrong key size");
                    }
                    byte[] key = new byte[keyLen];
                    is.read(key, 0, keyLen);


                    int valLen = is.readInt();
                    if (is.available() < valLen) {
                        throw new MyException("Wrong value size");
                    }
                    byte[] value = new byte[valLen];
                    is.read(value, 0, valLen);


                    put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                }
            } catch (IOException e) {
                throw new MyException("Reading from disk failed");
            }

        }

    }

    public void write() {
        try (DataOutputStream os = new DataOutputStream(Files.newOutputStream(dbPath))) {
            for (HashMap.Entry<String, String> entry : entrySet()) {
                byte[] key = entry.getKey().getBytes("UTF-8");
                byte[] value = entry.getValue().getBytes("UTF-8");
                os.writeInt(key.length);
                os.write(key);
                os.writeInt(value.length);
                os.write(value);
            }
        } catch (IOException e) {
            throw new MyException("Writing to dist failed");
        }



    }

}
