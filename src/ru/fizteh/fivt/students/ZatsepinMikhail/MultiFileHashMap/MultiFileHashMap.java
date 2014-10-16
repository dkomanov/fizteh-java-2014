package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;

/**
 * Created by mikhail on 16.10.14.
 */
public class MultiFileHashMap {
    private String dataBaseDirectory;
    private ArrayList<FileMap> tables;
    public MultiFileHashMap(String newDirectory) {
        dataBaseDirectory = newDirectory;
        tables = new ArrayList<>();
    }

    public String getDataBaseDirectory() {
        return dataBaseDirectory;
    }

    public int getNumberOfTables() {
        return tables.size();
    }

    public boolean init() {
        String[] listOfFiles = new File(dataBaseDirectory).list();
        for (String oneFile: listOfFiles) {
            Path oneTablePath = Paths.get(dataBaseDirectory, oneFile);
            if (Files.isDirectory(oneTablePath)) {
                tables.add(new FileMap(oneTablePath.toString()));
            }
        }
        boolean allRight = true;
        for (FileMap oneFileMap: tables) {
            if  (!oneFileMap.init()) {
                allRight = false;
            }
        }
        return allRight;
    }
}
