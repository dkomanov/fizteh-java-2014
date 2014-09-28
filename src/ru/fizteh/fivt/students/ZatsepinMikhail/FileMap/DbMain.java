package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

/**
 * FileMap
 * Version from 28.09.2014
 * Created by Mikhail Zatsepin, 395
 */

public class DbMain {
    public static void main(String[] args) {
        FileMap myFileMap = new FileMap(System.getProperty("db.file"));
        myFileMap.addCommand(new Put());
        myFileMap.addCommand(new Get());
        myFileMap.addCommand(new List());
        myFileMap.addCommand(new Remove());
        boolean errorOcuried = false;
        if (!myFileMap.init()) {
            errorOcuried = true;
        }
        if (args.length > 0) {
            if (!myFileMap.packetMode(args)) {
                errorOcuried = true;
            }
        } else {
            if (!myFileMap.interactiveMode()) {
                errorOcuried = true;
            }
        }
        if (errorOcuried) {
            System.exit(2);
        } else {
            System.exit(0);
        }
    }
}
