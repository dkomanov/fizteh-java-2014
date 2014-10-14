package ru.fizteh.fivt.students.valentine_lebedeva.filemap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class DB {
    private RandomAccessFile dbFile;
    private Map<String, String> base;

    public DB() throws IOException {
        dbFile = new RandomAccessFile(System.getProperty("db.file"), "rw");
        base = new HashMap<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte symbol;
        int offset;
        String[] pair = new String[2];
        int j = 0;
        while (dbFile.length() - dbFile.getFilePointer() > 4) {
            offset = dbFile.readInt();
            dbFile.seek(dbFile.getFilePointer() + 2);
            for (int i = 0; i < offset; i++) {
                symbol = dbFile.readByte();
                buffer.write(symbol);
            }
            pair[j % 2] = buffer.toString("UTF-8");
            if (j % 2 == 1) {
                base.put(pair[0], pair[1]);
            }
            if (dbFile.length() - dbFile.getFilePointer() > 2) {
                dbFile.seek(dbFile.getFilePointer() + 2);
            }
            j++;
            buffer.reset();
        }
    }

    public void write() throws IOException {
        dbFile.setLength(0);
        for (Entry<String, String> item : base.entrySet()) {
            writeItem(item.getKey());
            writeItem(item.getValue());
        }
    }

    public void writeItem(final String arg) throws IOException {
        dbFile.writeInt(arg.getBytes(StandardCharsets.UTF_8).length);
        dbFile.writeChar(' ');
        dbFile.write(arg.getBytes(StandardCharsets.UTF_8));
        dbFile.writeChar(' ');
    }

    public void close() throws IOException {
        this.write();
        dbFile.close();
    }

    public Map<String, String> getBase() {
        return base;
    }

    public void putBase(final String key, final String value) {
        base.put(key, value);
    }

    public void removeBase(final String key) {
        base.remove(key);
    }
}
