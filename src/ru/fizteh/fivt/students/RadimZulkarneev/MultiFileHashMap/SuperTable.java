package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
//import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SuperTable {
    private Path tablePath;
    private Map<String, Table> src;
    public SuperTable(Path tableDir) throws TableConnectionException, DataBaseCorrupt, MapException {
        checkTableDir(tableDir);
        tablePath = tableDir;
        src = new HashMap<String, Table>();
        initSuperTable();
    }
    
    
    private void checkTableDir(Path tableDir) throws TableConnectionException {
        File ctFile = tableDir.toFile();
        if (!ctFile.exists()) {
            throw new TableConnectionException("can't use table '" + tableDir.getFileName() + "': No such table");
        }
    }
    
    private void initSuperTable() throws DataBaseCorrupt, MapException {
        String[] files = tablePath.toFile().list();
        for (String it : files) {
            if (it.matches("([0-9]|1[0-5])\\.dir")) {
                initTable(tablePath.resolve(it));
            } else {
                throw new DataBaseCorrupt("Can't connect table: illegal name");
            }
        }
    }
    
    private void initTable(Path filePath) throws MapException, DataBaseCorrupt {
        try {
            String[] files = filePath.toFile().list();
            for (String it : files) {
                if (it.matches("([0-9]|1[0-5])\\.dat")) {
                    Table newTable = new Table(filePath.resolve(it).toString());
                    String ct = newTable.retCode();
                    src.put(ct, newTable);
                } else {
                    throw new DataBaseCorrupt("Can't connect table: illegal file-name: " + it);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage() + " " + ex.toString());
        }
    }
    
    public boolean put(String key, String value) throws IOException, DataBaseCorrupt, MapException {
        String dest = destinationOfKey(key);

        if (src.containsKey(dest)) {
            return src.get(dest).put(key, value);
        } else {
            //
        	if (!tablePath.resolve(getDirName(dest)).toFile().exists()) {
        		Files.createDirectory(tablePath.resolve(getDirName(dest)));
        	}
            Table newTable = new Table(tablePath.resolve(getDirName(dest)).resolve(getFileName(dest)).toString());
            src.put(dest, newTable);
            return newTable.put(key, value);
        }
    }
    
    public void showTableList() {
        for (Entry<String, Table> current : src.entrySet()) {
            current.getValue().listCommand();
        }
    }  
    
    public void get(String key) {
        String dest = destinationOfKey(key);
        if (src.containsKey(dest)) {
            src.get(dest).get(key);
        } else {
            System.out.println("not found");
        }
        
    }
    private String getDirName(String dest) {
        if (dest.matches("0[0-9]..")) {
            return new String(dest.substring(1, 2) + ".dir");
        } else {
            return new String(dest.substring(0, 2) + ".dir");
        }
    }
    private String getFileName(String dest) {
        if (dest.matches("..0[0-9]")) {
            return new String(dest.substring(3, 4) + ".dat");
        } else {
            return new String(dest.substring(2, 4) + ".dat");
        }
    }
    public boolean remove(String key) {
        String dest = destinationOfKey(key);
        if (src.containsKey(dest)) {
            return src.get(dest).remove(key);
        } else {
            System.out.println("not found");
            return false;
        }
    }
    private String destinationOfKey(String key) {
        int hashcode = Math.abs(key.hashCode());
        int nDir = hashcode % 16;
        int nFile = hashcode / 16 % 16;
        String retVal = "";
        if (nDir < 10) {
            retVal += ("0" + nDir);
        } else {
            retVal += nDir;
        }
        if (nFile < 10) {
            retVal += ("0" + nFile);
        } else {
            retVal += nFile;
        } 
        return retVal;
    }
    
    public void commit() throws MapException, IOException {
        for (Entry<String, Table> current : src.entrySet()) {
            current.getValue().writeInFile();
            Path it = current.getValue().tablePath().getParent();
            if (it.toFile().list().length == 0) {
            	Files.delete(it);
            }
        }

    }
    
    public String getTableName() {
        return tablePath.getFileName().toString();
    }

}
