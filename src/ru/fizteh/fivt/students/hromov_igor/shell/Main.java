package ru.fizteh.fivt.students.hromov_igor.shell;

public class Main {
	public static void main( String[] args) {
		if (args.length == 0) {
			Shell.interactiveMode();
		} else {
			Shell.packageMode(args);
		}
	}
}
