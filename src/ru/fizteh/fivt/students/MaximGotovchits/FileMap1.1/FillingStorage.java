package filemap;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Maxim on 07.10.2014.
 */
public class FillingStorage extends FileMapMain {
    void fillingStorageFunction() throws Exception {
        File file = new File(System.getProperty("db.file"));
        if (!file.exists()) {
            file.createNewFile();
        }
        DataInputStream stream = new DataInputStream(new FileInputStream(System.getProperty("db.file")));
        byte[] data = new byte[(int) file.length()];
        stream.read(data);
        int counter = 0;
        int offset = 0;
        String keyForMap = "";
        String valForMap = "";
        while (counter < file.length()) {
            offset = data[counter];
            keyForMap = new String(data, counter + 2, offset - 2, StandardCharsets.UTF_8);
            counter = counter + offset + 1;
            offset = data[counter];
            valForMap = new String(data, counter + 2, offset - 2, StandardCharsets.UTF_8);
            storage.put(keyForMap, valForMap);
            counter = counter + offset + 1;
        }
        stream.close();
    }
}


