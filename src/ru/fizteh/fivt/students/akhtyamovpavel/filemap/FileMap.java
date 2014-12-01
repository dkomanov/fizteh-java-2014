package ru.fizteh.fivt.students.akhtyamovpavel.filemap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by user1 on 30.09.2014.
 */
public class FileMap extends HashMap<String, String> {
    Path dataBasePath;

    public FileMap(Path path) throws Exception {
        dataBasePath = path;
    }

    public void loadMap() throws Exception {

        try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(dataBasePath))) {
            clear();

            while (inputStream.available() > 0) {
                put(readString(inputStream), readString(inputStream));
            }
        } catch (IOException ioe) {
            throw new Exception("read from database failed");
        }
    }

    public void saveMap() throws Exception {
        try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(dataBasePath))) {
            for (Entry<String, String> entry : entrySet()) {
                writeString(outputStream, entry.getKey());
                writeString(outputStream, entry.getValue());
            }
        } catch (IOException e) {
            throw new Exception("writing to database failed");
        }
    }

    private String readString(final DataInputStream inputStream) throws Exception {
        try {
            int length = inputStream.readInt();
            if (length <= 0) {
                throw new Exception("read from database failed");
            }
            byte[] byteString = new byte[length];
            inputStream.readFully(byteString);
            return new String(byteString, "UTF-8");
        } catch (EOFException eof) {
            throw new Exception("read from database failed");
        } catch (IOException ioe) {
            throw new Exception("read from database failed");
        }
    }

    private void writeString(final DataOutputStream outputStream, String string) throws Exception {
        try {
            byte[] stringByte = string.getBytes("UTF-8");
            outputStream.writeInt(stringByte.length);
            outputStream.write(stringByte);
        } catch (IOException ioe) {
            throw new Exception("writing to database failed");
        }
    }

}
