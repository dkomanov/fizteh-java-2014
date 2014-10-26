package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by denny_000 on 08.10.2014.
 */
public class TableFileDAT implements TableInterface, SaveInMemoryInterface {

    private RandomAccessFile binFile;
    private Map<String, String> data = new TreeMap<>();
    private boolean isLoaded = false;
    private int directoryNumber;
    private int fileNumber;
    Path parentTablePath;


    public TableFileDAT(Path pathToParentTable, int numOfDirectory, int numOfFile) {
        data.clear();
        directoryNumber = numOfDirectory;
        fileNumber = numOfFile;
        parentTablePath = pathToParentTable;
    }

    private void init() {
        Path pathToDirectory = getDirectoryPath();
        File dir = pathToDirectory.toFile();
        if (!dir.exists()) {
            return;
        }
        Path pathToBinFile = getDATFilePath();
        try { // Creating binfile.
            try {
                RandomAccessFile datFile
                        = new RandomAccessFile(pathToBinFile.toString(), "rw");
                binFile = datFile;
                if (datFile.length() > 0) {
                    read(datFile);
                }

            } catch (FileNotFoundException e) {
                pathToBinFile.toFile().createNewFile();
                RandomAccessFile datFile
                        = new RandomAccessFile(pathToBinFile.toString(), "r");
                binFile = datFile;
            }
        } catch (IOException e) {
            System.err.println("Can't create file.");
            System.err.println("Reason: " + e.getMessage());
            System.exit(-1);
        }
        isLoaded = true;
    }

    private Path getDirectoryPath() {
        return parentTablePath.resolve(directoryNumber + ".dir");
    }

    private Path getDATFilePath() {
        return parentTablePath.resolve(directoryNumber + ".dir" + File.separator
        + fileNumber + ".dat");
    }

    @Override
    public String put(String key, String value) {
        if (!isLoaded) {
            init();
        }
        String result = data.put(key, value);
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(result);
        }
        return result;
    }

    @Override
    public String get(String key) {
        if (!isLoaded) {
            init();
        }
        String result = data.get(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(result);
        }
        return result;
    }

    public String getValue(String key) {
        if (!isLoaded) {
            init();
        }
        String result = data.get(key);
        return result;
    }

    @Override
    public boolean remove(String key) {
        if (!isLoaded) {
            init();
        }
        String result = data.remove(key);
        if (result == null) {
            System.out.println("not found");
            return false;
        } else {
            System.out.println("removed");
            return true;
        }
    }

    @Override
    public List<String> list() {
        if (!isLoaded) {
            File file = getDATFilePath().toFile();
            if (file.exists()) {
                init();
            }
        }
        List<String> result = new ArrayList<>(data.size());
        Set<String> keys = data.keySet();
        for (String key : keys) {
            result.add(key);
        }
        return result;
    }

    public int getCount() {
        if (!isLoaded) {
            File file = getDATFilePath().toFile();
            if (file.exists()) {
                init();
            }
        }
        return data.size();
    }

    @Override
    public void write(RandomAccessFile whereTo) {
        if (isLoaded) {
            File datFile = getDATFilePath().toFile();
            if (datFile.exists()) {
                if (!datFile.delete()) {
                    System.out.println("can't delete");
                }
            }
        }
        if (data.size() == 0) {
            return;
        }

        File dir = getDirectoryPath().toFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        init();
        whereTo = binFile;
        try {
            whereTo.setLength(0);
            Set<String> keys = data.keySet();
            List<Integer> offsetsPos = new LinkedList<Integer>();
            for (String currentKey : keys) {
                whereTo.write(currentKey.getBytes("UTF-8"));
                whereTo.write('\0');
                offsetsPos.add((int) whereTo.getFilePointer());
                whereTo.writeInt(0);
            }
            List<Integer> offsets = new LinkedList<Integer>();
            for (String currentKey : keys) {
                offsets.add((int) whereTo.getFilePointer());
                whereTo.write(data.get(currentKey).getBytes("UTF-8"));
            }
            Iterator<Integer> offIter = offsets.iterator();
            for (int offsetPos : offsetsPos) {
                whereTo.seek(offsetPos);
                whereTo.writeInt(offIter.next());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void read(final RandomAccessFile whereFrom) {
        Path pathToDirectory = getDirectoryPath();
        File dir = pathToDirectory.toFile();
        if (!dir.exists()) {
            return;
        }
        try {
            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream();
            List<Integer> offsets = new LinkedList<Integer>();
            List<String> keys = new LinkedList<String>();
            byte b;
            int bytesCounter = 0;
            int firstOffset = -1;
            do {
                while ((b = whereFrom.readByte()) != 0) {
                    bytesCounter++;
                    bytesBuffer.write(b);
                }
                bytesCounter++;
                if (firstOffset == -1) {
                    firstOffset = whereFrom.readInt();
                } else {
                    offsets.add(whereFrom.readInt());
                }
                bytesCounter += 4;
                keys.add(bytesBuffer.toString("UTF-8"));
                bytesBuffer.reset();
            } while (bytesCounter < firstOffset);
            // Reading values until reaching the end of file.
            offsets.add((int) whereFrom.length());
            Iterator<String> keyIter = keys.iterator();
            for (int nextOffset: offsets) {
                while (bytesCounter < nextOffset) {
                    bytesBuffer.write(whereFrom.readByte());
                    bytesCounter++;
                }
                if (bytesBuffer.size() > 0) {
                    data.put(keyIter.next(), bytesBuffer.toString("UTF-8"));
                    bytesBuffer.reset();
                } else {
                    throw new IOException("File ends before reading last value.");
                }
            }
            bytesBuffer.close();
            binFile = whereFrom;
            whereFrom.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void clear() {
        data.clear();
        binFile = null;
    }

    public RandomAccessFile getBinFile() {
        return binFile;
    }

    public void setBinFile(RandomAccessFile dbFile) {
        binFile = dbFile;
    }

    private void create() {
    }
}


