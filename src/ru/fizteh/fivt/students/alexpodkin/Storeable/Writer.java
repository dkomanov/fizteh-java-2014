package ru.fizteh.fivt.students.alexpodkin.Storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Writer {

    private String fileMapPath;
    private TableProvider storeableTableProvider;
    private Table storeableTable;

    public Writer() {
    }

    public Writer(String path, TableProvider stp, Table st) {
        fileMapPath = path;
        storeableTableProvider = stp;
        storeableTable = st;
    }

    public void writeWord(DataOutputStream dataOutputStream, String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        dataOutputStream.writeInt(byteWord.length);
        dataOutputStream.write(byteWord);
    }

    public void writeDataToFile(HashMap<String, Storeable> fileMap, String dirPath) throws IOException {
        if (fileMap.isEmpty()) {
            return;
        }
        File dir = new File(dirPath);
        if (!dir.mkdir()) {
            throw new IOException("Can't create new directory");
        }
        File file = new File(fileMapPath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Can't create new file");
            }
        }
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
        for (HashMap.Entry<String, Storeable> entry : fileMap.entrySet()) {
            writeWord(dataOutputStream, entry.getKey());
            String value = storeableTableProvider.serialize(storeableTable, entry.getValue());
            writeWord(dataOutputStream, value);
        }
    }

}
