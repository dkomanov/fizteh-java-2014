package ru.fizteh.fivt.students.torunova.filemap;

import java.util.Set;

/**
 * Created by nastya on 04.10.14.
 */
public interface Database {
	boolean put(final String key, final String value);
	String get(final String key);
	boolean remove(final String key);
	Set<String> list();
	void close();
}
