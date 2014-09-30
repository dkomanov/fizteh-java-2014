package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class TablePart {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    protected static final String UNEXPECTED_END_OF_FILE = "Unexpected end of file.";
    protected SortedMap<String, String> data;
    protected boolean isLoaded = false;

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

    public void load() {
        if (!isLoaded) {
            loadWork();
        }
    }

    protected void loadWork() {
        File path = getSaveFilePath();
        Exception error = null;
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
                    keyString.remove(keyString.size() - 1);
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
            } catch (Throwable cantCreateFileEx) {
                System.err.println("Can't create file with database: " + cantCreateFileEx.getMessage());
                ex.addSuppressed(cantCreateFileEx);
            }
            error = ex;
        } catch (SecurityException ex) {
            System.err.println("Security error: " + ex.getMessage());
            error = ex;
        } catch (EOFException ex) {
            System.err.println(UNEXPECTED_END_OF_FILE);
            error = ex;
        } catch (IOException ex) {
            System.err.println("Input/output error on loading data: " + ex.getMessage());
            error = ex;
        } catch (LoadOrSaveError ex) {
            System.err.println("Error in loading : " + ex.getMessage());
            error = ex;
        }
        if (error != null) {
            throw new LoadOrSaveError("Can't load.", error);
        }
    }

    public void Save() {
        File path = getSaveFilePath();
        try (FileOutputStream stream = new FileOutputStream(path)) {
            try (DataOutputStream output = new DataOutputStream(stream)) {

            }
        } catch (FileNotFoundException ex) {

        } catch (SecurityException ex) {

        } catch (IOException ex) {
        
        }
    }

    private String getString(ArrayList<Byte> line) {
        byte[] lineBytes = new byte[line.size()];
        for (int i = 0; i < lineBytes.length; ++i) {
            lineBytes[i] = line.get(i);
        }
        return new String(lineBytes, CHARSET);
    }

    public File getSaveFilePath() {
        return new File(System.getProperty("db.file"));
    }
}

