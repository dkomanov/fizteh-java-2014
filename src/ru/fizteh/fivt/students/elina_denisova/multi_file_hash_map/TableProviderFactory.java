package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TableProviderFactory {

    private HashMap<String, TableProvider> tables;
    String using;
    File parentDirectory;


    public TableProviderFactory(String path) {
        parentDirectory = new File(path);
        using = null;
        tables = new HashMap<String, TableProvider>();
        try {
            if (path == null) {
                throw new NullPointerException("TableProviderFactory: Wrong path");
            }
            if (!Files.exists(parentDirectory.toPath()) && !parentDirectory.mkdir()) {
                throw new UnsupportedOperationException("TableProviderFactory: Cannot create working directory");
            }
            if (!parentDirectory.isDirectory()) {
                throw new FileNotFoundException("TableProviderFactory:" + parentDirectory.toString()
                        + " is not a directory");
            }
            for (String childName : parentDirectory.list()) {
                File childDirectory = new File(parentDirectory, childName);
                if (childDirectory.isDirectory()) {
                    tables.put(childName, new TableProvider(childDirectory));
                } else {
                    throw new Exception(childName + " from databases directory is not a directory");
                }
            }
        } catch (UnsupportedOperationException e) {
            HandlerException.handler(e);
        } catch (NullPointerException e) {
            HandlerException.handler(e);
        } catch (FileNotFoundException e) {
            HandlerException.handler(e);
        } catch (Exception e) {
            HandlerException.handler("TableProviderFactory: Unknown error", e);
        }

    }

    public TableProvider getUsing() {
        return tables.get(using);
    }

    public TableProvider get(String key) {
        return tables.get(key);
    }

    public Set< Map.Entry<String, TableProvider> > entrySet() {
        return tables.entrySet();
    }

    public void put(String key, TableProvider value ) {
        tables.put(key, value);
    }

    public void remove(String key) {
        tables.remove(key);
    }

    public boolean containsKey(String key) {
        return tables.containsKey(key);
    }

}
