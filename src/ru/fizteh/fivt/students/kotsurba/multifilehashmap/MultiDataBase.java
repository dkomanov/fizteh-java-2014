package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBaseException;
import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBaseFile;
import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBaseWrongFileFormat;

import java.io.File;
import java.io.IOException;

public final class MultiDataBase {
    private String dataBaseDirectory;
    private DataBaseFile[] files;

    public static final int DIRECTORY_COUNT = 16;
    public static final int FILES_COUNT = 16;

    public final class DirFile {
        private int nDir;
        private int nFile;

        public DirFile(int key) {
            key += 256;
            nDir = key % 16;
            nFile = (key / 16) % 16;
        }

        private String getNDirectory() {
            return Integer.toString(nDir) + ".dir";
        }

        private String getNFile() {
            return Integer.toString(nFile) + ".dat";
        }

        private int getHash() {
            return nDir * 16 + nFile;
        }
    }

    public MultiDataBase(final String dbDirectory) throws IOException {
        dataBaseDirectory = dbDirectory;
        isCorrect();
        files = new DataBaseFile[256];
        loadFiles();
    }

    private void checkNames(final String[] dirs, final String secondName) {
        for (String dir : dirs) {
            String[] name = dir.split("\\.");
            if (name.length != 2 || !name[1].equals(secondName)) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file in path " + dir);
            }

            int firstName;
            try {
                firstName = Integer.parseInt(name[0]);
            } catch (NumberFormatException e) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file first name " + dir);
            }

            if ((firstName < 0) || firstName > 15) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file first name " + dir);
            }
        }
    }

    private void isCorrectDirectory(String dirName) {
        File file = new File(dirName);
        if (file.isFile()) {
            throw new MultiDataBaseException(dirName + " isn't a directory!");
        }
        String[] dirs = file.list();
        checkNames(dirs, "dat");
        for (String dir : dirs) {
            if (new File(dirName + File.separator + dir).isDirectory()) {
                throw new MultiDataBaseException(dirName + File.separator + dir + " isn't a file!");
            }
        }
    }

    private void isCorrect() {
        File file = new File(dataBaseDirectory);
        if (file.isFile()) {
            throw new MultiDataBaseException(dataBaseDirectory + " isn't directory!");
        }

        String[] dirs = file.list();
        checkNames(dirs, "dir");
        for (String dir : dirs) {
            isCorrectDirectory(dataBaseDirectory + File.separator + dir);
        }
    }

    private void tryAddDirectory(final String name) {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new DataBaseException("Cannot create a directory!");
            }
        }
    }


    private void tryDeleteDirectory(final String name) {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (file.exists()) {
            if (file.list().length == 0) {
                if (!file.delete()) {
                    throw new DataBaseException("Cannot delete a directory!");
                }
            }
        }
    }

    private String getFullName(final DirFile node) {
        return dataBaseDirectory + File.separator + node.getNDirectory() + File.separator + node.getNFile();
    }

    public void loadFiles() {
        try {
            for (int i = 0; i < DIRECTORY_COUNT; ++i) {
                for (int j = 0; j < FILES_COUNT; ++j) {
                    DirFile node = new DirFile(i + j * FILES_COUNT);
                    MultiDataBaseFile file = new MultiDataBaseFile(getFullName(node), node.nDir, node.nFile);
                    files[node.getHash()] = file;
                }
            }
        } catch (DataBaseWrongFileFormat e) {
            save();
            throw e;
        }
    }

    public String put(final String keyStr, final String valueStr) {
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getHash()];
        return file.put(keyStr, valueStr);
    }

    public String get(final String keyStr) {
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getHash()];
        return file.get(keyStr);
    }

    public boolean remove(final String keyStr) {
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getHash()];
        return file.remove(keyStr);
    }

    public void drop() {
        for (byte i = 0; i < DIRECTORY_COUNT; ++i) {
            for (byte j = 0; j < FILES_COUNT; ++j) {
                File file = new File(getFullName(new DirFile(i + j * 16)));
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new DataBaseException("Cannot delete a file!");
                    }
                }
            }
            tryDeleteDirectory(Integer.toString(i) + ".dir");
        }
    }


    public String list() {
        StringBuilder str = new StringBuilder();
        for (DataBaseFile dbf : files) {
            if (!dbf.getKeyList().isEmpty()) {
                str.append(dbf.getKeyList()).append(", ");
            }
        }
        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
            str.deleteCharAt(str.length() - 1);
        }
        return str.toString();
    }

    public String getCount() {
        Integer count = 0;
        for (DataBaseFile dbf : files) {
            count += dbf.getKeyCount();
        }
        return count.toString();
    }

    public void save() {
        for (int i = 0; i < DIRECTORY_COUNT; ++i) {
            tryAddDirectory(Integer.toString(i) + ".dir");
            for (int j = 0; j < FILES_COUNT; ++j) {
                if (files[new DirFile((i + j * FILES_COUNT)).getHash()] != null) {
                    files[new DirFile((i + j * FILES_COUNT)).getHash()].save();
                }
            }
            tryDeleteDirectory(Integer.toString(i) + ".dir");
        }
    }

}
