package ru.fizteh.fivt.students.vadim_mazaev.filemap;

public final class DbMain {

	public static void main(final String[] args) {
		DbConnector link = new DbConnector();
		if (args.length == 0) {
			CommandParser.interactiveMode(link);
		} else {
			CommandParser.packageMode(link, args);
		}
	}
	
}
