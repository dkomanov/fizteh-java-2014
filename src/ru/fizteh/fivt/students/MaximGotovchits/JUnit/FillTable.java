package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FillTable extends CommandTools {
    int dirAndFileNum = 16;
    public void fillTableFunction(String tableName) throws Exception {
        for (Map.Entry<String, String> entry : commitStorage.entrySet()) {
            int hashCode = entry.getKey().hashCode();
            Integer nDirectory = hashCode % dirAndFileNum;
            Integer nFile = hashCode / dirAndFileNum % dirAndFileNum;
            Path fileName = Paths.get(dataBaseName + "/" + tableName, nDirectory.toString() + ".dir");
            File file = new File(fileName.toString());
            if (!file.exists()) {
                file.mkdir();
            }
            fileName = Paths.get(fileName.toString(), nFile.toString() + ".dat");
            file = new File(fileName.toString());
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bytesKey = (" " + entry.getKey() + " ").getBytes(StandardCharsets.UTF_8);
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(fileName.toString(), true));
            stream.write((int) bytesKey.length);
            stream.write(bytesKey);
            byte[] bytesVal = ((" " + entry.getValue() + " ").getBytes(StandardCharsets.UTF_8));
            stream.write((int) bytesVal.length);
            stream.write(bytesVal);
            stream.close();
        }
    }
}
