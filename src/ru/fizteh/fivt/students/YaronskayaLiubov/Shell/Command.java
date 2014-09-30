package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.IOException;

abstract class Command {
	String name;

	@Override
	public String toString() {
		return name;
	}

	abstract void execute(String[] args) throws IOException;
}
