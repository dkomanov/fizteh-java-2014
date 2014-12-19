package ru.fizteh.fivt.students.torunova.storeable.database;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.database.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 04.11.14.
 */
public class DatabaseFactory implements TableProviderFactory {
    public DatabaseFactory(){}
    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        TableProvider tp;
        try {
            tp = new Database(dir);
        } catch (IncorrectDbNameException | IOException | TableNotCreatedException
                | IncorrectFileException | IncorrectDbException e) {
            throw new RuntimeException(e);
        }
        return tp;

    }
}
