package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by luba_yaronskaya on 16.11.14.
 */

public class StoreableDataTable implements Table, AutoCloseable {
    protected File curDB;
    public String dbPath;
    private List<Class<?>> columnTypes;
    private StoreableDataTableProvider provider;
    private Map<String, Storeable> committedData = new HashMap<>();
    private ThreadLocal<Map<String, Storeable>> deltaAdded = new ThreadLocal<>();
    private ThreadLocal<Map<String, Storeable>> deltaChanged = new ThreadLocal<>();
    private ThreadLocal<Set<String>> deltaRemoved = new ThreadLocal<>();
    private Lock lock;
    private boolean closed = false;

    protected StoreableDataTable(StoreableDataTableProvider provider, String dbPath) {
        if (!Files.exists(Paths.get(dbPath))) {
            try {
                Files.createDirectory(Paths.get(dbPath));
            } catch (IOException e) {
                throw new IllegalArgumentException(dbPath + "is not a directory");
            }
        }
        if (!Files.isDirectory(Paths.get(dbPath))) {
            throw new IllegalArgumentException(dbPath + "is not a directory");
        }
        this.dbPath = dbPath;
        this.provider = provider;
        curDB = new File(dbPath);
        committedData = new HashMap<>();
        deltaAdded = new ThreadLocal<>();
        deltaChanged = new ThreadLocal<>();
        deltaRemoved = new ThreadLocal<>();
        lock = new ReentrantLock();
        loadDBData();
    }

    @Override
    public String getName() {
        return curDB.getName();
    }

    @Override
    public Storeable get(String key) {
        CheckParameters.checkKey(key);
        checkUnclosed();

        if (deltaRemoved.get().contains(key)) {
            return null;
        }
        if (deltaAdded.get().containsKey(key)) {
            return deltaAdded.get().get(key);
        }
        if (deltaChanged.get().containsKey(key)) {
            return deltaChanged.get().get(key);
        }
        return committedData.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        CheckParameters.checkKey(key);
        CheckParameters.checkValue(value);
        checkUnclosed();

        if (deltaChanged.get().containsKey(key)) {
            return deltaChanged.get().put(key, value);
        } else {
            if (committedData.containsKey(key)) {
                deltaChanged.get().put(key, value);
                return committedData.get(key);
            } else {
                return deltaAdded.get().put(key, value);
            }
        }
    }

    @Override
    public Storeable remove(String key) {
        CheckParameters.checkKey(key);
        checkUnclosed();

        if (committedData.containsKey(key)) {
            if (deltaChanged.get().containsKey(key)) {
                return deltaChanged.get().remove(key);
            } else {
                deltaRemoved.get().add(key);
            }
        } else {
            if (deltaAdded.get().containsKey(key)) {
                return deltaAdded.get().remove(key);
            }
        }
        return null;
    }

    @Override
    public int size() {
        checkUnclosed();
        int delta1 = deltaAdded.get() == null ? 0 : deltaAdded.get().size();
        int delta2 = deltaRemoved.get() == null ? 0 : deltaRemoved.get().size();
        return committedData.size() + delta1 - delta2/*+ deltaAdded.get().size() - deltaRemoved.get().size()*/;
    }

    @Override
    public int commit() {
        checkUnclosed();
        int deltaCount;
        lock.lock();
        try {
            Map<String, Storeable> realAdded = new HashMap(deltaAdded.get());
            Map<String, Storeable> realChanged = new HashMap(deltaChanged.get());
            Map<String, Storeable> tempAdded = new HashMap(deltaAdded.get());
            Map<String, Storeable> tempChanged = new HashMap(deltaChanged.get());

            realAdded.keySet().removeAll(committedData.keySet());
            tempChanged.keySet().removeAll(committedData.keySet());
            realAdded.putAll(tempChanged);

            realChanged.keySet().retainAll(committedData.keySet());
            tempAdded.keySet().retainAll(committedData.keySet());
            realChanged.putAll(tempAdded);

            deltaRemoved.get().retainAll(committedData.keySet());

            deltaCount = realAdded.size() + realChanged.size() + deltaRemoved.get().size();

            committedData.putAll(realAdded);
            committedData.putAll(realChanged);
            committedData.keySet().removeAll(deltaRemoved.get());
        } finally {
            lock.unlock();
        }

        clearDelta();
        return deltaCount;
    }

    @Override
    public int rollback() {
        checkUnclosed();
        int delta1 = (deltaAdded.get() == null) ? 0 : deltaAdded.get().size();
        int delta3 = deltaRemoved.get() == null ? 0 : deltaRemoved.get().size();
        int delta2 = deltaChanged.get() == null ? 0 : deltaChanged.get().size();
        int deltaCount = delta1 + delta2 + delta3/*deltaAdded.get().size()
                /*+ deltaChanged.get().size() +
                deltaRemoved.get().size()*/;
        clearDelta();
        return deltaCount;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return deltaAdded.get().size()
                + deltaChanged.get().size()
                + deltaRemoved.get().size();
    }

    private void clearDelta() {
        deltaAdded.set(new HashMap<String, Storeable>());
        deltaChanged.set(new HashMap<String, Storeable>());
        deltaRemoved.set(new HashSet<String>());
    }

    @Override
    public int getColumnsCount() {
        checkUnclosed();
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkUnclosed();
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException("illegal column index");
        }
        return columnTypes.get(columnIndex);
    }

    @Override
    protected void finalize() throws Throwable {
        save();
        super.finalize();
    }

    public List<String> list() {
        checkUnclosed();
        List<String> keys = new ArrayList<>(committedData.keySet());
        keys.removeAll(deltaRemoved.get());
        keys.addAll(deltaAdded.get().keySet());
        return keys;
    }

    public int unsavedChangesCount() {
        checkUnclosed();
        return deltaAdded.get().size() + deltaChanged.get().size() + deltaRemoved.get().size();
    }

    public void loadDBData() {
        committedData.clear();
        clearDelta();
        File signatureFile = new File(curDB, "signature.tsv");
        if (!signatureFile.exists()) {
            throw new SignatureFileNotFoundException("Signature file not found");
        }
        loadSignature(signatureFile);

        File[] tableDirs = curDB.listFiles();
        if (tableDirs == null) {
            return;
        }

        for (File dir : tableDirs) {
            if (dir.getName().equals(".DS_Store") || dir.getName().equals("signature.tsv")) {
                continue;
            }

            File[] tableFiles = dir.listFiles();
            if (tableFiles == null) {
                continue;
            }
            for (File tableFile : tableFiles) {
                if (tableFile.getName().equals(".DS_Store")) {
                    continue;
                }
                try (FileChannel channel = new FileInputStream(tableFile.getCanonicalPath()).getChannel()) {

                    ByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

                    while (byteBuffer.remaining() > 0) {
                        int keyLength = byteBuffer.getInt();
                        byte[] key = new byte[keyLength];
                        byteBuffer.get(key, 0, keyLength);
                        int valueLength = byteBuffer.getInt();
                        byte[] value = new byte[valueLength];

                        byteBuffer.get(value, 0, valueLength);
                        try {
                            Storeable row = provider.deserialize(this, new String(value, "UTF-8"));
                            committedData.put(new String(key, "UTF-8"), row);
                        } catch (ParseException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("error reading file: " + e.getMessage()
                    );
                }
            }
        }
    }

    public void save() {
        for (int i = 0; i < 16; ++i) {
            try {
                File dir = new File(dbPath, i + ".dir");
                dir.mkdirs();
                if (!dir.exists()) {
                    dir.createNewFile();
                }
                dir.mkdirs();
                for (int j = 0; j < 16; ++j) {
                    File file = new File(dir, j + ".dat");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                }
            } catch (IOException e) {
                System.err.println("error writing on disk: " + e.getMessage());
            }
        }

        FileOutputStream[][] fos = new FileOutputStream[16][16];
        boolean[] usedDirs = new boolean[16];
        boolean[][] usedFiles = new boolean[16][16];
        try {
            for (Map.Entry<String, Storeable> entry : committedData.entrySet()) {
                String key = entry.getKey();
                Storeable row = entry.getValue();
                int hashcode = Math.abs(key.hashCode());
                int ndirectory = hashcode % 16;
                int nfile = hashcode / 16 % 16;
                if (!usedFiles[ndirectory][nfile]) {
                    if (!usedDirs[ndirectory]) {
                        usedDirs[ndirectory] = true;
                    }
                    usedFiles[ndirectory][nfile] = true;
                    fos[ndirectory][nfile] = new FileOutputStream(dbPath + File.separator + ndirectory + ".dir"
                            + File.separator + nfile + ".dat");
                }
                byte[] keyInBytes = key.getBytes("UTF-8");
                String value = provider.serialize(this, row);
                byte[] valueInBytes = value.getBytes("UTF-8");
                ByteBuffer bb = ByteBuffer.allocate(8 + keyInBytes.length + valueInBytes.length);
                bb.putInt(keyInBytes.length);
                bb.put(keyInBytes);
                bb.putInt(valueInBytes.length);
                bb.put(valueInBytes);
                int limit = bb.limit();

                for (int i = 0; i < limit; ++i) {
                    fos[ndirectory][nfile].write(bb.get(i));
                }

            }
        } catch (Exception e) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (fos[i][j] != null) {
                        try {
                            fos[i][j].close();
                        } catch (IOException e1) {
                            continue;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (fos[i][j] != null) {
                    try {
                        fos[i][j].close();
                    } catch (IOException e1) {
                        continue;
                    }
                }
            }
        }

        for (int i = 0; i < 16; ++i) {
            boolean emptyDir = true;
            for (int j = 0; j < 16; ++j) {
                if (!usedFiles[i][j]) {
                    try {
                        Files.delete(Paths.get(dbPath + File.separator + i + ".dir" + File.separator + j + ".dat"));
                    } catch (IOException e) {
                        continue;
                    }
                } else {
                    emptyDir = false;
                }
            }
            if (emptyDir) {
                try {
                    Files.delete(Paths.get(dbPath + File.separator + i + ".dir"));
                } catch (IOException e) {
                    continue;
                }
            }
        }
    }

    public static void fileDelete(File myDir) {
        if (myDir.isDirectory()) {
            File[] content = myDir.listFiles();
            for (int i = 0; i < content.length; ++i) {
                fileDelete(content[i]);
            }
        }
        myDir.delete();
    }

    private void loadSignature(File signatureFile) throws UndefinedColumnTypeException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(signatureFile));
            String stringSignature = reader.readLine();
            List<String> columnTypesList = Arrays.asList(stringSignature.split("\\s+"));
            this.columnTypes = provider.typelistToClassList(columnTypesList);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void close() {
        if (!closed) {
            rollback();
            closed = true;
        }
    }

    private void checkUnclosed() {
        if (closed) {
            throw new IllegalStateException("Command failed: table have been closed");
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), curDB.getAbsolutePath());
    }
}
