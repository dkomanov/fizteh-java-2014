package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileMap {
    private HashMap<String, String> dataBase;
    private String diskFile;

    public FileMap(String newDiskFile) {
        diskFile = newDiskFile;
        dataBase = new HashMap<>();
    }

    public String get(String key) {
        return dataBase.get(key);
    }

    public String remove(String key) {
        return dataBase.remove(key);
    }

    public String put(String key, String value) {
        return dataBase.put(key, value);
    }

    public Set<String> keySet() {
        return dataBase.keySet();
    }

    public int getNumberOfPairs() {
        return dataBase.size();
    }

    public boolean init() {
        try (FileInputStream inStream = new FileInputStream(diskFile)) {
            FileChannel inputChannel;
            inputChannel = inStream.getChannel();
            ByteBuffer bufferFromDisk;
            try {
                bufferFromDisk =
                        inputChannel.map(MapMode.READ_ONLY, 0, inputChannel.size());
            } catch (IOException e) {
                System.out.println("io exception");
                return false;
            }
            try {
                while (bufferFromDisk.hasRemaining()) {
                    byte[] key;
                    byte[] value;
                    int keySize;
                    int valueSize;
                    if (bufferFromDisk.remaining() >= 4) {
                        keySize = bufferFromDisk.getInt();
                        key = new byte[keySize];
                    } else {
                        throw new BadFileException();
                    }

                    if (bufferFromDisk.remaining() >= keySize) {
                        bufferFromDisk.get(key, 0, key.length);
                    } else {
                        throw new BadFileException();
                    }

                    if (bufferFromDisk.remaining() >= 4) {
                        valueSize = bufferFromDisk.getInt();
                        value = new byte[valueSize];
                    } else {
                        throw new BadFileException();
                    }
                    if (bufferFromDisk.remaining() >= valueSize) {
                        bufferFromDisk.get(value, 0, value.length);
                    } else {
                        throw new BadFileException();
                    }
                    try {
                        dataBase.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("unsupported encoding");
                        return false;
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("null pointer exception");
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return false;
        } catch (BadFileException e) {
            System.out.println("problems with database file");
            return false;
        } catch (IOException e) {
            System.out.println("io exception");
            return false;
        }
        return true;
    }

    public boolean load(String addKey) {
        HashSet<String> keySet = new HashSet<>(dataBase.keySet());
        boolean appendFile = false;
        if (addKey != null) {
            appendFile = true;
            keySet.clear();
            keySet.add(addKey);
        }
        try (FileOutputStream outputStream = new FileOutputStream(diskFile, appendFile)) {
            ByteBuffer bufferForSize = ByteBuffer.allocate(4);
            for (String key : keySet) {
                if (!appendFile | key.equals(addKey)) {
                    try {
                        byte[] keyByte = key.getBytes("UTF-8");
                        byte[] valueByte = dataBase.get(key).getBytes("UTF-8");
                        outputStream.write(bufferForSize.putInt(0, keyByte.length).array());
                        outputStream.write(keyByte);
                        outputStream.write(bufferForSize.putInt(0, valueByte.length).array());
                        outputStream.write(valueByte);
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("unsupported encoding");
                        return false;
                    } catch (IOException e) {
                        System.out.println("io exception");
                        return false;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return false;
        } catch (IOException e) {
            System.out.println("io exception");
            return false;
        }
        return true;
    }
}
