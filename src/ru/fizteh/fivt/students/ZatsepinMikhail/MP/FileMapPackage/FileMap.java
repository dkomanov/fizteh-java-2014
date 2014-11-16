package ru.fizteh.fivt.students.ZatsepinMikhail.MP.FileMapPackage;

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
            if (!Files.isDirectory(Paths.get(currentDirectory))) {
                continue;
            }
            String[] listOfFiles = new File(currentDirectory).list();
            for (String oneFile : listOfFiles) {
                String currentFile = currentDirectory + System.getProperty("file.separator")
                        + oneFile;
                int numberOfDirectory = oneDirectory.charAt(0) - '0';
                if (oneDirectory.charAt(1) != '.') {
                    numberOfDirectory = 10 * numberOfDirectory + oneDirectory.charAt(1) - '0';
                }
                int numberOfFile = oneFile.charAt(0) - '0';
                if (oneFile.charAt(1) != '.') {
                    numberOfFile = 10 * numberOfFile + oneFile.charAt(1) - '0';
                }
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

                            String keyString = new String(key, "UTF-8");
                            if (getNumberOfDirectory(keyString.hashCode()) != numberOfDirectory
                                    || getNumberOfFile(keyString.hashCode()) != numberOfFile) {
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

    public boolean load(String key, boolean appendFile) {
        HashSet<String> keySet = new HashSet<>();
        ByteBuffer bufferForSize = ByteBuffer.allocate(4);

        int numberOfDirectory = getNumberOfDirectory(key.hashCode());
        int numberOfFile = getNumberOfFile(key.hashCode());
        if (appendFile) {
            keySet.add(key);
        } else {
            Set<String> keySetFromDB = dataBase.keySet();
            for (String oneKey : keySetFromDB) {
                if (numberOfDirectory == getNumberOfDirectory(oneKey.hashCode())
                        & numberOfFile == getNumberOfFile(oneKey.hashCode())) {
                    keySet.add(oneKey);
                }
            }
        }

        Path directoryForLoad;
        Path fileForLoad;
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

        try (FileOutputStream outputStream
                     = new FileOutputStream(fileForLoad.toString(), appendFile)) {
            for (String oneKey : keySet) {
                int keyNumberOfDirectory = getNumberOfDirectory(oneKey.hashCode());
                int keyNnumberOfFiles = getNumberOfFile(oneKey.hashCode());
                if (!appendFile & (numberOfFile == keyNnumberOfFiles)
                        & (numberOfDirectory == keyNumberOfDirectory) | appendFile) {
                    try {
                        byte[] keyByte = oneKey.getBytes("UTF-8");
                        byte[] valueByte = dataBase.get(oneKey).getBytes("UTF-8");
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

        if (!appendFile) {
            deleteEmptyFiles(directoryForLoad, fileForLoad);
        }
        return true;
    }


    public boolean deleteEmptyFiles(Path directory, Path file) {
        try {
            if (Files.size(file) == 0) {
                Files.delete(file);
            }
        } catch (IOException e) {
            System.err.println("error while deleting data base file");
            return false;
        }
        String[] listOfFiles = new File(directory.toString()).list();
        if (listOfFiles.length == 0) {
            try {
                Files.delete(directory);
            } catch (IOException e) {
                System.err.println("error while deleting directory");
                return false;
            }
        }
        return true;
    }
}
