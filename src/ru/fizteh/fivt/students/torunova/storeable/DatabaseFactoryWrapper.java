package ru.fizteh.fivt.students.torunova.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;

/**
 * Created by nastya on 24.11.14.
 */
public class DatabaseFactoryWrapper implements TableProviderFactory{
	private DatabaseFactory factory;
	public DatabaseFactoryWrapper() {
		factory = new DatabaseFactory();
	}
	@Override
	public TableProvider create(String path) throws IOException {
		return new DatabaseWrapper(factory.create(path));
	}
}
