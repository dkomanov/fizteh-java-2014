package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;
//package Shell;

import java.io.IOException;

abstract class Command {
	String name;
	int maxNumberOfArguements;

	@Override
	public String toString() {
		return name;
	}

	abstract boolean execute(String[] args) throws IOException;
}
