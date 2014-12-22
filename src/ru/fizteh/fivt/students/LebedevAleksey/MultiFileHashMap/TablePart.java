package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TablePart {
    protected static final Charset CHARSET = Charset.forName("UTF-8");
    protected static final String UNEXPECTED_END_OF_FILE = "Unexpected end of file.";
    protected Map<String, String> data = new TreeMap<>();
    private boolean isLoaded = false;
    private Table table;
    private int folderNum;
    private int fileNum;

    public TablePart(Table parentTable, int folderNumber, int fileNumber) {
        super();
        table = parentTable;
        folderNum = folderNumber;
        fileNum = fileNumber;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void loadChecks() throws LoadOrSaveException, DatabaseFileStructureException {
        File file = getSaveFilePath();
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new DatabaseFileStructureException("'" + file.getAbsolutePath() + "' is directory");
            } else {
                loadWork();
                for (String key : data.keySet()) {
                    if (table.selectPartForKey(key) != this) {
                        throw new LoadOrSaveException("Wrong key '" + key + "' in file " + file.getAbsolutePath());
                    }
                }
            }
        } else {
            File parent = file.getParentFile();
            if (parent.exists() && (!parent.isDirectory())) {
                throw new DatabaseFileStructureException("Can't load: '" + file.getParent() + "' is file");
            }
        }
    }

    protected void loadWork() throws LoadOrSaveException, DatabaseFileStructureException {
        File path = getSaveFilePath();
        Exception error = null;
        String exMessage = null;
        try (FileInputStream inputStream = new FileInputStream(path)) {
            try (DataInputStream reader = new DataInputStream(inputStream)) {
                ArrayList<String> keys = new ArrayList<>();
                ArrayList<Integer> begins = new ArrayList<>();
                int index = 0;
                do {
                    ArrayList<Byte> keyString = new ArrayList<>();
                    int inputByte;
                    do {
                        inputByte = reader.read();
                        if (inputByte > 0) {
                            keyString.add((byte) inputByte);
                        }
                        ++index;
                    } while (inputByte > 0);
                    if (inputByte < 0) {
                        if (index == 1) {
                            return;
                        }
                        throw new LoadOrSaveException(UNEXPECTED_END_OF_FILE);
                    }
                    keys.add(getString(keyString));
                    begins.add(reader.readInt());
                    index += 4;
                } while (index < begins.get(0));
                for (int i = 0; i < begins.size(); ++i) {
                    int size = (i + 1 < begins.size() ? begins.get(i + 1) - begins.get(i) : inputStream.available());
                    if (size < 0) {
                        throw new LoadOrSaveException("Error in loading: file is corrupted.");
                    }
                    byte[] buffer = new byte[size];
                    if (reader.read(buffer) != size) {
                        throw new LoadOrSaveException(UNEXPECTED_END_OF_FILE);
                    } else {
                        data.put(keys.get(i), new String(buffer, CHARSET));
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            try (FileOutputStream writer = new FileOutputStream(path)) {
                writer.flush();
            } catch (Exception cantCreateFileEx) {
                exMessage = "Can't create file with database: " + cantCreateFileEx.getMessage();
                error = cantCreateFileEx;
            }
        } catch (SecurityException ex) {
            exMessage = "Security error in loading: " + ex.getMessage();
            error = ex;
        } catch (EOFException ex) {
            exMessage = UNEXPECTED_END_OF_FILE;
            error = ex;
        } catch (IOException ex) {
            exMessage = "Input/output error on loading data: " + ex.getMessage();
            error = ex;
        } catch (LoadOrSaveException ex) {
            exMessage = "Error in loading : " + ex.getMessage();
            error = ex;
        }
        if (error != null) {
            throw new LoadOrSaveException("Can't load. " + exMessage, error);
        }
    }

    public void load() throws LoadOrSaveException, DatabaseFileStructureException {
        if (!isLoaded) {
            loadChecks();
            isLoaded = true;
        }
    }

    private String getString(ArrayList<Byte> line) {
        byte[] lineBytes = new byte[line.size()];
        for (int i = 0; i < lineBytes.length; ++i) {
            lineBytes[i] = line.get(i);
        }
        return new String(lineBytes, CHARSET);
    }

    public void save() throws LoadOrSaveException, DatabaseFileStructureException {
        File path = getSaveFilePath();
        if (empty()) {
            try {
                if (path.exists()) {
                    if (!path.delete()) {
                        throw new LoadOrSaveException("Can't delete file.");
                    }
                }
                File parent = new File(path.getParent());
                String[] list = parent.list();
                if (parent.exists() && (list == null || list.length == 0)) {
                    if (!parent.delete()) {
                        throw new LoadOrSaveException("Can't delete folder.");
                    }
                }
            } catch (SecurityException ex) {
                throw new LoadOrSaveException("Access denied on save.", ex);
            }
        } else {
            try {
                File directory = path.getParentFile();
                if (!directory.exists()) {
                    Files.createDirectory(directory.toPath());
                }
                saveWork();
            } catch (SecurityException ex) {
                throw new LoadOrSaveException("Access denied on creating folder", ex);
            } catch (FileAlreadyExistsException ex) {
                throw new LoadOrSaveException("Something strange in creating folder", ex);
            } catch (IOException ex) {
                throw new LoadOrSaveException("Error creating folder: " + ex.getMessage(), ex);
            }
        }
    }

    public File getSaveFilePath() throws DatabaseFileStructureException {
        return getFolderPath().resolve(fileNum + ".dat").toFile();
    }

    private Path getFolderPath() throws DatabaseFileStructureException {
        return table.getDirectory().resolve(folderNum + ".dir");
    }

    public int count() {
        return data.size();
    }

    public boolean empty() {
        return count() == 0;
    }

    public void drop() throws LoadOrSaveException, DatabaseFileStructureException {
        data.clear();
        save();
    }

    public void saveWork() throws LoadOrSaveException, DatabaseFileStructureException {
        File path = getSaveFilePath();
        Exception error = null;
        String exMessage = null;
        try (FileOutputStream stream = new FileOutputStream(path)) {
            try (DataOutputStream output = new DataOutputStream(stream)) {
                List<String> keys = list();
                ArrayList<byte[]> keysUtf8 = new ArrayList<>(keys.size());
                int keysDataSize = 0;
                ArrayList<byte[]> values = new ArrayList<>(keys.size());
                for (String key : keys) {
                    byte[] buffer = key.getBytes(CHARSET);
                    keysUtf8.add(buffer);
                    keysDataSize += buffer.length + 1 + Integer.BYTES;
                    values.add(data.get(key).getBytes(CHARSET));
                }
                byte[] separator = new byte[]{0};
                for (int i = 0; i < values.size(); i++) {
                    output.write(keysUtf8.get(i));
                    output.write(separator);
                    output.writeInt(keysDataSize);
                    keysDataSize += values.get(i).length;
                }
                for (int i = 0; i < keys.size(); i++) {
                    output.write(values.get(i));
                }
            }
        } catch (FileNotFoundException ex) {
            exMessage = "File not found on save: " + ex.getMessage();
            error = ex;
        } catch (SecurityException ex) {
            exMessage = "Security error: " + ex.getMessage();
            error = ex;
        } catch (IOException ex) {
            exMessage = "Error in save: " + ex.getMessage();
            error = ex;
        }
        if (error != null) {
            throw new LoadOrSaveException("Can't save. " + exMessage, error);
        }
    }

    public String put(String key, String value) {
        return data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public boolean remove(String key) {
        return (data.remove(key) != null);
    }

    public List<String> list() {
        ArrayList<String> result = new ArrayList<>(data.size());
        for (String key : data.keySet()) {
            result.add(key);
        }
        return result;
    }
}
