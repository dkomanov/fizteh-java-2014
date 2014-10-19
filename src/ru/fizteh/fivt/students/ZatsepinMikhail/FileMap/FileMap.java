package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileMap {
    private HashMap<String, String> dataBase;
    private String directoryOfTable;

    public FileMap(String newDirectoryFile) {
        directoryOfTable = newDirectoryFile;
        dataBase = new HashMap<>();
    }

    public void setDirectoryOfTable(String newDiskFile) {
        directoryOfTable = newDiskFile;
    }

    public String getDirectoryOfTable() {
        return directoryOfTable;
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

    public int getNumberOfDirectory(int hash) {
        int result = hash % 16;
        if (result < 0) {
            result += 16;
        }
        return result;
    }

    public int getNumberOfFile(int hash) {
        int result = hash / 16 % 16;
        if (result < 0) {
            result += 16;
        }
        return result;
    }

    public boolean init() {
        String[] listOfDirectories = new File(directoryOfTable).list();
        if (listOfDirectories == null) {
            return true;
        }
        for (String oneDirectory: listOfDirectories) {
            String currentDirectory = directoryOfTable + System.getProperty("file.separator")
                    + oneDirectory;
            String[] listOfFiles = new File(currentDirectory).list();
            for (String oneFile : listOfFiles) {
                String currentFile = currentDirectory + System.getProperty("file.separator")
                        + oneFile;

                try (FileInputStream inStream = new FileInputStream(currentFile)) {
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
            }
        }
        return true;
    }

    public boolean load(String key, boolean action) {
        HashSet<String> keySet = new HashSet<>(dataBase.keySet());
        boolean appendFile = false;

        ByteBuffer bufferForSize = ByteBuffer.allocate(4);

        Path directoryForLoad;
        Path fileForLoad;
        int numberOfDirectory = getNumberOfDirectory(key.hashCode());
        int numberOfFile = getNumberOfFile(key.hashCode());
        directoryForLoad = Paths.get(directoryOfTable, numberOfDirectory + ".dir");
        if (!Files.exists(directoryForLoad)) {
            try {
                Files.createDirectory(directoryForLoad);
            } catch (IOException e) {
                System.out.println("error while creating directory for load");
                return false;
            }
        }

        fileForLoad = Paths.get(directoryForLoad.toString(), numberOfFile + ".dat");
        if (!Files.exists(fileForLoad)) {
            try {
                Files.createFile(fileForLoad);
            } catch (IOException e) {
                System.out.println("error while creating file for load");
                return false;
            }
        }

        System.out.println(directoryForLoad.toString());
        System.out.println(fileForLoad.toString());

        try (FileOutputStream outputStream
                         = new FileOutputStream(fileForLoad.toString(), appendFile)) {
            for (String oneKey : keySet) {
                int keyNumberOfDirectory = getNumberOfDirectory(oneKey.hashCode());
                int keyNnumberOfFiles = getNumberOfFile(oneKey.hashCode());
                if (!appendFile & (numberOfFile == keyNnumberOfFiles)
                        & (numberOfDirectory == keyNumberOfDirectory) | appendFile) {
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
        } catch (FileNotFoundException e){
            System.out.println("file not found");
            return false;
        } catch (IOException e) {
            System.out.println("io exception");
            return false;
        }
        return true;
    }
}