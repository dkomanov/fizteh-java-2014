package ru.fizteh.fivt.students.torunova.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 04.11.14.
 */
public class DatabaseFactory implements TableProviderFactory {
    public DatabaseFactory(){}
    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        TableProvider tp = null;
        if (dir == null || dir.equals("..") || dir.equals(".")) {
            throw new IllegalArgumentException("Illegal name of database.");
        }
        try {
            tp = new Database(dir);
        } catch (IncorrectDbNameException e) {
            System.err.println("Caught IncorrectDbNameException: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        } catch (TableNotCreatedException e) {
            System.err.println("Caught TableNotCreatedException: " + e.getMessage());
        } catch (IncorrectFileException e) {
            System.err.println("Caught IncorrectFileException: " + e.getMessage());
        }
        return tp;

    }
}
