package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TablePart {
    public static final String DB_FILE_PARAMETER_NAME = "db.file";
    protected static final Charset CHARSET = Charset.forName("UTF-8");
    protected static final String UNEXPECTED_END_OF_FILE = "Unexpected end of file.";
    protected TreeMap<String, String> data = new TreeMap<>();
    private boolean isLoaded = false;

    public boolean isLoaded() {
        return isLoaded;
    }

    public String put(String key, String value) {
        String result = data.put(key, value);
        if (result == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(result);
        }
        return result;
    }

    public String get(String key) {
        String result = data.get(key);
        if (result == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(result);
        }
        return result;
    }

    public boolean remove(String key) {
        String result = data.remove(key);
        if (result == null) {
            System.out.println("not found");
            return false;
        } else {
            System.out.println("removed");
            return true;
        }
    }

    public List<String> list() {
        ArrayList<String> result = new ArrayList<>(data.size());
        for (String key : data.keySet()) {
            result.add(key);
        }
        return result;
    }

    public void load() throws LoadOrSaveError {
        if (!isLoaded) {
            loadWork();
            isLoaded = true;
        }
    }

    protected void loadWork() throws LoadOrSaveError {
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
                        throw new LoadOrSaveError(UNEXPECTED_END_OF_FILE);
                    }
                    keys.add(getString(keyString));
                    begins.add(reader.readInt());
                    index += 4;
                } while (index < begins.get(0));
                for (int i = 0; i < begins.size(); ++i) {
                    int size = (i + 1 < begins.size() ? begins.get(i + 1) - begins.get(i) : inputStream.available());
                    byte[] buffer = new byte[size];
                    if (reader.read(buffer) != size) {
                        throw new LoadOrSaveError(UNEXPECTED_END_OF_FILE);
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
            exMessage = "Security error: " + ex.getMessage();
            error = ex;
        } catch (EOFException ex) {
            exMessage = UNEXPECTED_END_OF_FILE;
            error = ex;
        } catch (IOException ex) {
            exMessage = "Input/output error on loading data: " + ex.getMessage();
            error = ex;
        } catch (LoadOrSaveError ex) {
            exMessage = "Error in loading : " + ex.getMessage();
            error = ex;
        }
        if (error != null) {
            throw new LoadOrSaveError("Can't load. " + exMessage, error);
        }
    }

    public void save() throws LoadOrSaveError {
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
            throw new LoadOrSaveError("Can't save. " + exMessage, error);
        }
    }

    private String getString(ArrayList<Byte> line) {
        byte[] lineBytes = new byte[line.size()];
        for (int i = 0; i < lineBytes.length; ++i) {
            lineBytes[i] = line.get(i);
        }
        return new String(lineBytes, CHARSET);
    }

    public File getSaveFilePath() throws LoadOrSaveError {
        return new File(System.getProperty(DB_FILE_PARAMETER_NAME));
    }
}

