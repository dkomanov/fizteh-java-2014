package ru.fizteh.fivt.students.artem_gritsay.MultiFile;

import java.io.IOException;
import java.util.HashMap;
import java.io.DataOutputStream;
import java.io.FileOutputStream;


public class MultiWrite {
    private String fileMapPath;

    public MultiWrite(String path) {
        fileMapPath = path;
    }

    public void writeDataToFile(HashMap<String, String> fileMap) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(fileMapPath));
        for (HashMap.Entry<String, String> entry : fileMap.entrySet()) {
            writeWord(dataOutputStream, entry.getKey());
            writeWord(dataOutputStream, entry.getValue());
        }
    }

    private void writeWord(DataOutputStream dataOutputStream, String word) throws IOException {
        byte[] byteWord = word.getBytes("UTF-8");
        dataOutputStream.writeInt(byteWord.length);
        dataOutputStream.write(byteWord);
    }



}
