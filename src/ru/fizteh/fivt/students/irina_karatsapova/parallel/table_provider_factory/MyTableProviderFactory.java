package ru.fizteh.fivt.students.irina_karatsapova.parallel.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.exceptions.DataBaseException;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.utils.Utils;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class MyTableProviderFactory implements TableProviderFactory {
    Path factoryPath;

    public MyTableProviderFactory() {
        factoryPath = Utils.makePathAbsolute("");
    }

    public MyTableProviderFactory(String factoryDir) {
        factoryPath = Utils.makePathAbsolute(factoryDir);
    }

    public TableProvider create(String dir) throws DataBaseException {
        Utils.checkNotNull(dir);
        File dirFile = checkAndCreateProviderFile(dir);
        return new MyTableProvider(dirFile.toString());
    }

    private File checkAndCreateProviderFile(String dir) throws DataBaseException {
        File file;
        try {
            file = Utils.makePathAbsolute(factoryPath, dir).toFile();
        } catch (InvalidPathException e) {
            throw new DataBaseException("init: Incorrect name");
        }
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new DataBaseException("init: Incorrect name: Can't create main directory");
            }
        }
        if (!file.isDirectory()) {
            throw new DataBaseException("init: Incorrect name: Main directory is a file");
        }
        return file;
    }
}
