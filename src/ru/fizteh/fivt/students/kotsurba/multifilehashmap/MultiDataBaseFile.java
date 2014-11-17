package ru.fizteh.fivt.students.kotsurba.multifilehashmap;

import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBaseFile;
import ru.fizteh.fivt.students.kotsurba.filemap.database.DataBaseWrongFileFormat;

import java.util.Map;

public class MultiDataBaseFile extends DataBaseFile {

    private int fileNumber;
    private int direcotryNumber;

    public MultiDataBaseFile(final String fileName, final int newDirectoryNumber, final int newFileNumber) {
        super(fileName);
        fileNumber = newFileNumber;
        direcotryNumber = newDirectoryNumber;
        check();
    }

    public boolean check() {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!((getZeroByte(entry) % 16 == direcotryNumber) && ((getZeroByte(entry) / 16) % 16 == fileNumber))) {
                throw new DataBaseWrongFileFormat("Wrong file format key[0] =  " + String.valueOf(getZeroByte(entry))
                        + " in file " + fileName);
            }
        }
        return true;
    }

    private byte getZeroByte(Map.Entry<String, String> entry) {
        byte[] bytes = entry.getKey().getBytes();
        return bytes[0];
    }

    public void close() {
        data.clear();
    }
}
