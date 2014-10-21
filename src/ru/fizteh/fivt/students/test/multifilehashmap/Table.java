package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by deserg on 04.10.14.
 */
public class Table extends HashMap<String, String> {

    private Path tablePath;

    Table(Path path) {
        tablePath = path;
    }

    private void readKeyValue(Path filePath, int dir, int file) throws MyIOException{

        if (Files.exists(filePath)) {
            try (DataInputStream is = new DataInputStream(Files.newInputStream(filePath))) {

                if (is.available() == 0) {
                    throw new MyIOException("File is empty: " + filePath);
                }

                while (is.available() > 0) {

                    int keyLen = is.readInt();
                    if (is.available() < keyLen) {
                        throw new MyIOException("Wrong key size");
                    }

                    byte[] key = new byte[keyLen];
                    is.read(key, 0, keyLen);


                    int valLen = is.readInt();
                    if (is.available() < valLen) {
                        throw new MyIOException("Wrong value size");
                    }
                    byte[] value = new byte[valLen];
                    is.read(value, 0, valLen);

                    String keyStr = new String(key, "UTF-8");
                    String valueStr = new String(value, "UTF-8");

                    int hashValue = keyStr.hashCode();
                    if (hashValue % 16 != dir || hashValue / 16 % 16 != file) {
                        throw new MyIOException("Wrong key file");
                    }

                    put(keyStr, valueStr);
                }

            } catch (IOException e) {
                throw new MyIOException("Reading from disk failed");
            }


        }
    }

    public void read() throws MyIOException {

        clear();

        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                Path filePath = tablePath.resolve(dir + ".dir").resolve(file + ".dat");
                try {
                    readKeyValue(filePath, dir, file);
                } catch (MyException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }
            }
        }

    }

    private void writeKeyValue(Path filePath, String keyStr, String valueStr) throws MyIOException {

        try (DataOutputStream os = new DataOutputStream(Files.newOutputStream(filePath))) {
            byte[] key = keyStr.getBytes("UTF-8");
            byte[] value = valueStr.getBytes("UTF-8");
            os.writeInt(key.length);
            os.write(key);
            os.writeInt(value.length);
            os.write(value);

        } catch (IOException ex) {
            throw new MyIOException("Writing to dist failed");
        }


    }

    public void write() throws MyIOException {

        if (Files.exists(tablePath)) {
            throw new MyIOException("Directory exists but it should not");
        } else {
            try {
                Files.createDirectory(tablePath);
            } catch (IOException ex) {
                throw new MyIOException("Error has occurred while creating table directory");
            }
        }

        for (HashMap.Entry<String, String> entry : entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            int hashCode = key.hashCode();
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;

            Path dirPath = tablePath.resolve(dir + ".dir");
            Path filePath = dirPath.resolve(file + ".dat");

            if (!Files.exists(dirPath)) {
                try {
                    Files.createDirectory(dirPath);
                } catch (IOException ex) {
                    throw new MyIOException(dirPath + ": unable to create");
                }
            }
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                } catch (IOException ex) {
                    throw new MyIOException(filePath + ": unable to create");
                }
            }

            writeKeyValue(filePath, key, value);

        }

    }


}
