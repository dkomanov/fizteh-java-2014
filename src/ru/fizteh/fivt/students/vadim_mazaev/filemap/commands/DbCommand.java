package ru.fizteh.fivt.students.vadim_mazaev.filemap.commands;

import ru.fizteh.fivt.students.vadim_mazaev.filemap.DbConnector;

public abstract class DbCommand implements Command<DbConnector> {

	@Override
	public abstract String getName();

	@Override
	public abstract boolean checkArgs() throws Exception;

	@Override
	public abstract void execute(DbConnector link,
			String[] cmdWithArgs) throws Exception;

}
