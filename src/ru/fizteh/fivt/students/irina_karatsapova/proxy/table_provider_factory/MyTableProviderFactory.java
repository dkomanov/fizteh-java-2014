package ru.fizteh.fivt.students.irina_karatsapova.proxy.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.DataBaseException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MyTableProviderFactory implements TableProviderFactory {
    private boolean loaded = false;
    Path factoryPath;
    private Map<String, TableProvider> providers = new HashMap<>();

    public MyTableProviderFactory() {
        factoryPath = Utils.makePathAbsolute("");
        loaded = true;
    }

    public MyTableProviderFactory(String factoryDir) {
        factoryPath = Utils.makePathAbsolute(factoryDir);
        loaded = true;
    }

    public TableProvider create(String dir) throws DataBaseException {
        checkLoaded();
        Utils.checkNotNull(dir);
        File dirFile = checkAndCreateProviderFile(dir);
        TableProvider provider;
        if (providers.containsKey(dirFile.toString())) {
            provider = providers.get(dirFile.toString());
        } else {
            provider = new MyTableProvider(dirFile.toString());
            providers.put(dirFile.toString(), provider);
        }
        return provider;
    }

    public void close() {
        for (TableProvider provider: providers.values()) {
            provider.close();
        }
        loaded = false;
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

    private void checkLoaded() throws IllegalStateException {
        if (!loaded) {
            throw new IllegalStateException("ProviderFactory is not loaded");
        }
    }
}
