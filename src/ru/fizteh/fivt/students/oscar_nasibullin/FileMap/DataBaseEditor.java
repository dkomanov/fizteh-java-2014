package ru.fizteh.fivt.students.oscar_nasibullin.FileMap;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;



public class DataBaseEditor {

    private static Map<String, String> data;

    public DataBaseEditor() throws Exception {
        data = new TreeMap<>();
        Path dataBasePath;
        try {
            dataBasePath = Paths.get(System.getProperty("db.file"));
            try (RandomAccessFile  dataBase
                    = new RandomAccessFile(dataBasePath.toString(), "r")) {
                if (dataBase.length() > 0) {
                    importData(dataBase);
                }
            } catch (FileNotFoundException e) {
                dataBasePath.toFile().createNewFile();
                }
        } catch (Exception e) {
            throw new Exception("cannot open file ");
          }
    }

    public final void closeDataBase() throws Exception {
        try (RandomAccessFile  dataBase
                = new RandomAccessFile(Paths.get(
                        System.getProperty("db.file")).toString(), "rw")) {
            exportData(dataBase);
            dataBase.close();
        } catch (Exception e) {
                throw new Exception("Error writing DataBase to file");
          }
    }

    public static void importData(
            final RandomAccessFile dataBase) throws IOException {
        ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
        List<Integer> offsets = new LinkedList<Integer>();
        List<String> keys = new LinkedList<String>();
        byte b;
        int bytesCounter = 0;
        int firstOffset = -1;

        do {
            while ((b = dataBase.readByte()) != 0) {
                bytesCounter++;
                bytesBuffer.write(b);
            }
            bytesCounter++;
            if (firstOffset == -1) {
                firstOffset = dataBase.readInt();
            } else {
                offsets.add(dataBase.readInt());
            }
            bytesCounter += 4;
            keys.add(bytesBuffer.toString("UTF-8"));
            bytesBuffer.reset();
        } while (bytesCounter < firstOffset);

        offsets.add((int) dataBase.length());
        Iterator<String> keyIter = keys.iterator();
        for (int nextOffset : offsets) {
            while (bytesCounter < nextOffset) {
                bytesBuffer.write(dataBase.readByte());
                bytesCounter++;
            }
            if (bytesBuffer.size() > 0) {
                data.put(keyIter.next(), bytesBuffer.toString("UTF-8"));
                bytesBuffer.reset();
            } else {
                throw new IOException();
            }
        }
        bytesBuffer.close();
    }

    public static void exportData(
            final RandomAccessFile dataBase) throws IOException {
        int offset = 0;
        dataBase.setLength(0);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            offset += entry.getKey().length() + 5;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            dataBase.write(entry.getKey().getBytes("UTF-8"));
            dataBase.write('\0');
            dataBase.writeInt(offset);
            offset += entry.getValue().length();
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            dataBase.write(entry.getValue().getBytes("UTF-8"));
        }
    }


    public final  String put(final List<String> args) throws Exception {
       if (args.size() != 3) {
           return null;
       }
       String rezultMessage = new String();
       if (data.containsKey(args.get(1))) {
           rezultMessage = "overwrite\n" + data.get(args.get(1));
           data.remove(args.get(1));
           data.put(args.get(1), args.get(2));
       } else {
           rezultMessage = "new";
           data.put(args.get(1), args.get(2));
       }
       return rezultMessage;
    }

    public final  String get(final List<String> args) {
        if (args.size() != 2) {
            return null;
        }
        String rezultMessage = new String();
        if (data.containsKey(args.get(1))) {
            rezultMessage = "found\n" + data.get(args.get(1));
        } else {
            rezultMessage =  "not found";
        }
        return rezultMessage;
    }

    public final  String remove(final List<String> args) {
        if (args.size() != 2) {
            return null;
        }
        String rezultMessage = new String();
        if (data.containsKey(args.get(1))) {
            rezultMessage = "removed";
            data.remove(args.get(1));
        } else {
            rezultMessage = "not found";
        }
        return rezultMessage;
    }

    public final  String list(final List<String> args) {
        if (args.size() != 1) {
            return null;
        }
        boolean firstWord = true;
        String rezultMessage = new String();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (firstWord) {
                rezultMessage = entry.getKey();
                firstWord = false;
            } else {
                rezultMessage += ", " + entry.getKey();
            }
        }
        return rezultMessage;
    }
}
