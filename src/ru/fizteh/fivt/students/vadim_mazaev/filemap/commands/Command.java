package ru.fizteh.fivt.students.vadim_mazaev.filemap.commands;

public interface Command<T> {
	String getName();
	boolean checkArgs() throws Exception;
	void execute(T link, String[] cmdWithArgs) throws Exception;
}
