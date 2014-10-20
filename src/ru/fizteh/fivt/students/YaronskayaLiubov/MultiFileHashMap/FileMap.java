package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luba_yaronskaya on 06.10.14.
 */
public class FileMap {
    protected File curDB;
    public String dbPath;
    protected HashMap<String, String> data;

    public FileMap(String path) {
        curDB = new File(path);
        dbPath = new String(path);
        data = new HashMap<String, String>();
        loadDBData();
    }

    @Override
    protected void finalize() throws Throwable {
        save();
        super.finalize();
    }

    public void loadDBData() {
        File[] tableDirs = curDB.listFiles();
        for (File dir : tableDirs) {
            if (dir.getName().equals(".DS_Store")) {
                continue;
            }
            File[] tableFiles = dir.listFiles();
            if (tableFiles.length == 0) {
                continue;
            }
            for (File tableFile : tableFiles) {
                if (tableFile.getName().equals(".DS_Store")) {
                    continue;
                }
                FileChannel channel = null;
                try {
                    channel = new FileInputStream(tableFile.getCanonicalPath()).getChannel();

                    ByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

                    while (byteBuffer.remaining() > 0) {
                        int keyLength = byteBuffer.getInt();
                        byte[] key = new byte[keyLength];
                        byteBuffer.get(key, 0, keyLength);
                        int valueLength = byteBuffer.getInt();
                        byte[] value = new byte[valueLength];

                        byteBuffer.get(value, 0, valueLength);
                        data.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                    }
                } catch (IOException e) {
                    System.err.println("error reading file" + e.toString()
                    );
                }
            }
        }
    }

    public void save() {
        for (int i = 0; i < 16; ++i) {
            try {
                Path dirName = Paths.get(curDB.getCanonicalPath()).resolve(i + ".dir/");
                if (!Files.exists(dirName)) {
                    Files.createDirectory(dirName);
                }
                for (int j = 0; j < 16; ++j) {
                    Path fileName = dirName.resolve(j + ".dat");
                    if (!Files.exists(fileName)) {
                        Files.createFile(fileName);
                    }
                }
            } catch (IOException e) {
                System.err.println("error creating directory");
            }
        }

        FileOutputStream[][] fos = new FileOutputStream[16][16];
        boolean[] usedDirs = new boolean[16];
        boolean[][] usedFiles = new boolean[16][16];
        try {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                int hashcode = Math.abs(key.hashCode());
                int ndirectory = hashcode % 16;
                int nfile = hashcode / 16 % 16;
                if (!usedFiles[ndirectory][nfile]) {
                    if (!usedDirs[ndirectory]) {
                        usedDirs[ndirectory] = true;
                    }
                    usedFiles[ndirectory][nfile] = true;
                    fos[ndirectory][nfile] = new FileOutputStream(dbPath + File.separator + ndirectory + ".dir" + File.separator + nfile + ".dat");
                }
                byte[] keyInBytes = new byte[0];
                byte[] valueInBytes = new byte[0];
                keyInBytes = key.getBytes("UTF-8");
                valueInBytes = value.getBytes("UTF-8");
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
                String fileName = dbPath + File.separator + i + ".dir" + File.separator + j + ".dat";
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
}
