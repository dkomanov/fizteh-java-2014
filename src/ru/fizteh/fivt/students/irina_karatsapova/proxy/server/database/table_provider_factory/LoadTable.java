package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.exceptions.ThreadInterruptException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.TypeTransformer;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.utils.Utils;

import java.io.*;

public class LoadTable {
    private static final String SIGNATURE_FILENAME = "signature.tsv";

    public static void start(MyTable table) throws ThreadInterruptException {
        try {
            table.currentMap().clear();
            keysClear(table);
            if (!table.tablePath.exists()) {
                throw new ThreadInterruptException("The table does not exist");
            }
            loadColumnsType(table);
            for (File dir : table.tablePath.listFiles()) {
                if (dir.isDirectory()) {
                    for (File file : dir.listFiles()) {
                        loadFile(table, file);
                    }
                }
            }
        } catch (ThreadInterruptException e) {
            throw new ThreadInterruptException("Load: " + e.getMessage());
        }
    }

    private static void loadColumnsType(MyTable table) {
        File signatureFile = Utils.makePathAbsolute(table.tablePath, SIGNATURE_FILENAME).toFile();
        try (BufferedReader bufReader = new BufferedReader(new FileReader(signatureFile))) {
            String[] types = bufReader.readLine().split(" ");

            for (String typeName : types) {
                table.columnsType.add(TypeTransformer.getTypeByName(typeName));
            }
        } catch (FileNotFoundException e) {
            throw new ThreadInterruptException("Signature file does not exist");
        } catch (IOException e) {
            throw new ThreadInterruptException("Can't read from the signature file");
        }
    }

    private static void loadFile(MyTable table, File file) throws ThreadInterruptException {
        DataInputStream inStream;
        try {
            inStream = new DataInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new ThreadInterruptException("Can't read from the file");
        }

        int filePos = 0;
        while (filePos < file.length()) {
            int keyLength;
            int valueLength;
            String key;
            String value;

            try {
                keyLength = inStream.readInt();
                key = readBytes(inStream, keyLength);
                valueLength = inStream.readInt();
                value = readBytes(inStream, valueLength);
            } catch (IOException e) {
                throw new ThreadInterruptException("Wrong format of file " + file.toString());
            }

            if (table.currentMap().containsKey(key)) {
                throw new ThreadInterruptException("Two same keys in file " + file.toString());
            }
            table.addKey(key);

            try {
                table.currentMap().put(key, table.tableProvider.deserialize(table, value));
            } catch (Exception e) {
                throw new ThreadInterruptException("Not suitable values in file " + file.toString());
            }

            filePos += 8 + keyLength + valueLength;
        }

        try {
            inStream.close();
        } catch (IOException e) {
            throw new ThreadInterruptException("Error while closing the dir");
        }
    }

    private static String readBytes(DataInputStream inStream, int length) throws IOException {
        byte[] stringInBytes = new byte[length];
        inStream.readFully(stringInBytes);
        return new String(stringInBytes, "UTF-8");
    }

    static void keysClear(MyTable table) {
        for (int dir = 0; dir < 16; dir++) {
            for (int file = 0; file < 16; file++) {
                table.currentKeys()[dir][file].clear();
            }
        }
    }
}
