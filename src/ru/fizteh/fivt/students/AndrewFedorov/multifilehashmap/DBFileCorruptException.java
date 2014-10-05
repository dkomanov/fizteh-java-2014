package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

/**
 * This exception describes situation when database file is corrupt and cannot
 * be properly read.
 * 
 * @author phoenix
 * 
 */
public class DBFileCorruptException extends Exception {

    private static final long serialVersionUID = 2107102137382933269L;

    public DBFileCorruptException(String reason) {
	super("DB file is corrupt: " + reason);
    }
}
