package ru.fizteh.fivt.students.standy66.shell;

import java.io.File;

/**
 * Created by astepanov on 21.09.14.
 */
public final class FileUtils {
	public static File fromPath(String path) {
		if (path.startsWith(File.separator)) {
			return new File(path);
		} else {
			return new File(System.getProperty("user.dir"), path);
		}
	}
}
