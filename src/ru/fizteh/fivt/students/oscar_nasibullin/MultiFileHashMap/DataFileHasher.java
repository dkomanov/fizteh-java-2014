package ru.fizteh.fivt.students.oscar_nasibullin.MultiFileHashMap;

import java.io.UnsupportedEncodingException;

class DataFileHasher implements Comparable<DataFileHasher> {
    private final int ndir;
    private final int nfile;

    public DataFileHasher(String key) throws IllegalArgumentException {
        try {
            ndir = Math.abs(key.getBytes("UTF-8")[0] % 16);
            nfile = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("bad key in DataFileHasher constructor");
        }
    }

    public DataFileHasher(int dirNum, int fileNum) {
        ndir = dirNum;
        nfile = fileNum;
    }

    public Integer getDirNum() {
        return new Integer(ndir);
    }

    public Integer getFileNum() {
        return new Integer(nfile);
    }


    public boolean contains(Byte key) {
        int keyNdir = Math.abs(Byte.valueOf(key) % 16);
        int keyNfile = Math.abs((Byte.valueOf(key) / 16) % 16);
        return ndir == keyNdir && nfile == keyNfile;
    }

    @Override
    public int compareTo(DataFileHasher o) {
      if (o.getDirNum() > getDirNum()) {
          return -1;
      } else if (o.getDirNum() < getDirNum()) {
          return 1;
      } else if (o.getFileNum() > getFileNum()) {
          return -1;
      } else if (o.getFileNum() < getFileNum()) {
          return 1;
      }
        return 0;
    }
}
