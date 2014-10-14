package ru.fizteh.fivt.students.dnovikov.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class SingleTable implements Table {
    private HashMap<String, String> dataBase;

    private Path dataBasePath;

    public SingleTable(Path path) throws IOException {
        dataBase = new HashMap<String, String>();
        dataBasePath = path;
        this.load();
    }

    public String put(String key, String value) {
        return dataBase.put(key, value);
    }

    public String get(String key) {
        return dataBase.get(key);
    }

    public Set<String> list() {
        Set<String> keys = dataBase.keySet();
        return keys;
    }

    public String remove(String key) {
        return dataBase.remove(key);
    }

    public void save() throws IOException {

        try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(dataBasePath))) {
            Set<Entry<String, String>> list = dataBase.entrySet();
            for (Entry<String, String> entry : list) {
                writeString(outputStream, entry.getKey());
                writeString(outputStream, entry.getValue());
            }
        } catch (IOException e) {
            throw new IOException("cannot write to database");
        }
    }

    public void load() throws IOException {

        try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(dataBasePath))) {
            dataBase.clear();
            while (inputStream.available() > 0) {
                dataBase.put(readString(inputStream), readString(inputStream));
            }
        } catch (IOException e) {
            try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(dataBasePath))) {
                outputStream.flush();
            } catch (IOException exception) {
                throw new IOException("cannot create file with database");
            } catch (SecurityException exception) {
                throw new IOException("cannot create file with database");
            }
        }
    }

    public String readString(DataInputStream inputStream) throws IOException {
        try {
            int length = inputStream.readInt();
            if (length <= 0) {
                throw new IOException("cannot read from database");
            }
            byte[] byteString = new byte[length];
            inputStream.readFully(byteString);
            return new String(byteString, "UTF-8");
        } catch (EOFException e) {
            throw new IOException("cannot read from database");
        } catch (IOException e) {
            throw new IOException("cannot read from database");
        }
    }

    public void writeString(DataOutputStream outputStream, String string) throws IOException {
        try {
            byte[] stringByte = string.getBytes("UTF-8");
            outputStream.writeInt(stringByte.length);
            outputStream.write(stringByte);
        } catch (IOException e) {
            throw new IOException("cannot write to database");
        }
    }
}
