package ru.fizteh.fivt.students.deserg.telnet;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by deserg on 26.11.14.
 */
public class DbTableProviderFactory implements TableProviderFactory, AutoCloseable {

    private boolean closed = false;

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param dir Директория с файлами базы данных.
     * @return Объект для работы с базой данных.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     */
    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {

        checkClosed();

        if (dir == null) {
            throw new IllegalArgumentException("Database \"" + dir + "\": null path");
        }

        if (dir.contains("\000")) {
            throw new IllegalArgumentException("Database \"" + dir + "\": unacceptable path");
        }

        Path path = Paths.get(System.getProperty("user.dir")).resolve(dir);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Database \"" + dir + "\": invalid path");
            }
        } else if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Database \"" + dir + "\": invalid path");
        }

        return new DbTableProvider(path);
    }

    @Override
    public void close() {

        checkClosed();
        closed = true;

    }



    private void checkClosed() {
        if (closed) {
            throw new IllegalStateException("Factory is closed");
        }
    }

}
