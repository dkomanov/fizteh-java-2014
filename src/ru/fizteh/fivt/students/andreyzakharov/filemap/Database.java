package ru.fizteh.fivt.students.andreyzakharov.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;`
import java.nio.file.Path;
import java.util.HashMap;

public class Database extends HashMap<String, String> {
    Path dbPath;

    public Database(Path path) {
        dbPath = path;
        load();
    }

    public void load() {
        try (DataInputStream is = new DataInputStream(Files.newInputStream(dbPath))) {
            clear();

            while (is.available() > 0) {
                int keyLen = is.readInt();
                byte[] key = new byte[keyLen];
                is.read(key, 0, keyLen);
                int valLen = is.readInt();
                byte[] value = new byte[valLen];
                is.read(value, 0, valLen);
                put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            }
        } catch (IOException e) {
            //
        }
    }

    public void unload() {
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
//            throw new DbWriteException();
        }
    }
}
