package ru.fizteh.fivt.students.torunova.filemap;

/**
 * Created by nastya on 08.10.14.
 */
public abstract class Action {
	void tooManyArguments() {
		System.err.println(getName() + ": too many arguments.");
	}
	void tooFewArguments() {
		System.err.println(getName() + ": too few arguments.");
	}

    abstract boolean run(String[] args, Database db);

    abstract String getName();
}
