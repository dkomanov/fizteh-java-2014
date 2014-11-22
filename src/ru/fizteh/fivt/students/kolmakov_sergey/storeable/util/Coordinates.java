package ru.fizteh.fivt.students.kolmakov_sergey.storeable.util;

import ru.fizteh.fivt.students.kolmakov_sergey.storeable.data_base_structure.TableManager;

import java.io.UnsupportedEncodingException;

/**
 * Each Table stores subdirectories, that have special name "folders" in this project. Each folder stores DataFiles.
 * Coordinate object holds index of folder and index of file in order to have access to each DataFile in the Table.
 */
public class Coordinates implements Comparable<Coordinates> {
    private final int folderIndex;
    private final int fileIndex;

    public Coordinates(int folderIndex, int fileIndex) {
        this.folderIndex = folderIndex;
        this.fileIndex = fileIndex;
    }

    public Coordinates(String key, int numberOfPartitions) {
        if (key == null) {
            throw new IllegalArgumentException("Error: key == null");
        }
        try {
            folderIndex = Math.abs(key.getBytes(TableManager.CODE_FORMAT)[0] % numberOfPartitions);
            fileIndex = Math.abs((key.getBytes(TableManager.CODE_FORMAT)[0] / numberOfPartitions)
                    % numberOfPartitions);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Bad key is given to constructor of Coordinates");
        }
    }

    public int getFolderIndex() {
        return folderIndex;
    }
    public int getFileIndex() {
        return fileIndex;
    }

    @Override
    public int compareTo(Coordinates coordinates) { // Not used in current version, but can be useful.
        if (folderIndex > coordinates.folderIndex) {
            return 1;
        } else if (folderIndex < coordinates.folderIndex) {
            return  -1;
        } else if (fileIndex > coordinates.fileIndex) {
            return  1;
        } else if (fileIndex < coordinates.fileIndex) {
            return  -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Coordinates)) {
            return  false;
        } else {
            Coordinates coordinates = (Coordinates) o;
            return folderIndex == coordinates.folderIndex && fileIndex == coordinates.fileIndex;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + fileIndex;
        hash = 53 * hash + folderIndex;
        return hash;
    }
}
