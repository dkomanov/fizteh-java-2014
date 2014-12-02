package ru.fizteh.fivt.students.alexpodkin.JUnit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Writer {

    private String fileMapPath;

    public Writer(String path) {
        fileMapPath = path;
    }

    private void writeWord(DataOutputStream dataOutputStream, String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        dataOutputStream.writeInt(byteWord.length);
        dataOutputStream.write(byteWord);
    }

    public void writeDataToFile(HashMap<String, String> fileMap, String dirPath) throws IOException {
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
        for (HashMap.Entry<String, String> entry : fileMap.entrySet()) {
            writeWord(dataOutputStream, entry.getKey());
            writeWord(dataOutputStream, entry.getValue());
        }
    }

}
