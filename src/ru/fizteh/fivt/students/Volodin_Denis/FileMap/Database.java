package ru.fizteh.fivt.students.Volodin_Denis.FileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DataBase {

    private String databasePath;
    private HashMap<String, String> database;

    public DataBase(final String dbPath) throws Exception {
        databasePath = dbPath;
        database = new HashMap<String, String>();
        if (Paths.get(databasePath).normalize().toFile().exists()) {
            readFromDisk();
        } else {
            File file = new File(databasePath);
            file.createNewFile();
        }
    }

    public String[] list() throws Exception {
        try {
            if (database.isEmpty()) {
                return new String[0];
            }
            Set<String> keysList = database.keySet();
            Iterator<String> itKeys = keysList.iterator();
            String[] listKeys = new String[database.size()];
            int i = 0;
            while (itKeys.hasNext()) {
                listKeys[i] = itKeys.next();
                ++i;
            }
            return listKeys;
        } catch (Exception except) {
            filemapSmthWrong("list", except.getMessage());
        }
        return new String[0]; //warning
    }

    public void put(final String key, final String value) throws Exception {
        try {
            database.put(key, value);
        } catch (Exception except) {
            filemapSmthWrong("put", except.getMessage());
        }
    }

    public String search(final String key) throws Exception {
        try {
            return database.get(key);
        } catch (Exception except) {
            filemapSmthWrong("search", except.getMessage());
        }
        return database.get(key); //warning
    }

    public void remove(final String key) throws Exception {
        try {
            database.remove(key);
        } catch (Exception except) {
            filemapSmthWrong("remove", except.getMessage());
        }
    }

    public void readFromDisk() throws Exception {
        String key, value;
        FileInputStream input = new FileInputStream(databasePath);
        FileChannel channel = input.getChannel();
        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        try {
            while (buffer.hasRemaining()) {
                byte[] word = new byte[buffer.getInt()];
                buffer.get(word, 0, word.length);
                key = new String(word, "UTF-8");
                
                word = new byte[buffer.getInt()];
                buffer.get(word, 0, word.length);
                value = new String(word, "UTF-8");
                
                database.put(key, value);
            }
        } catch (Exception except) {
            input.close();
            filemapErrorRead("read");
        }
        input.close();
    }

    public void writeOnDisk() throws Exception {
        FileOutputStream output = new FileOutputStream(databasePath);
        FileOutputStream output2 = new FileOutputStream("home\student\info.txt");
        Set<String> keyList = database.keySet();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        Iterator<String> itKeys = keyList.iterator();
        try {
            while (itKeys.hasNext()) {
                String key = itKeys.next();
                byte[] keyByte = key.getBytes("UTF-8");
                byte[] valueByte = database.get(key).getBytes("UTF-8");
                
                output.write(buffer.putInt(0, keyByte.length).array());
                output.write(keyByte);
                
                output2.write(key, " ", database.get(key), "\n");
                
                output.write(buffer.putInt(0, valueByte.length).array());
                output.write(valueByte, "\n");
            }
        } catch (Exception except) {
            output.close();
            output2.close();
            filemapErrorWrite("write");
        }
        output.close();
        output2.close();
    }

    private void filemapErrorRead (final String commandName) throws Exception {
        throw new Exception(commandName + " : error reading from file");
    }
    
    private void filemapErrorWrite (final String commandName) throws Exception {
        throw new Exception(commandName + " : error writing to file");
    }
    
    private void filemapSmthWrong (final String commandName, final String message) throws Exception {
        throw new Exception(commandName + " :" + message);
    }
}
