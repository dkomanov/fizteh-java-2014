package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by mikhail on 26.09.14.
 */
public class DdMain {
    public static void main(String[] args) {
        try (FileOutputStream outStream =
                     new FileOutputStream(System.getProperty("db.file"))) {
            FileMap myFileMap = new FileMap();
            myFileMap.init(outStream);
            if (args.length > 0) {
                myFileMap.packetMode(outStream, args);
            } else {
                myFileMap.interactiveMode(outStream);
            }
        }
    }
}
