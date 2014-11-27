package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.fileMap;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Welcome {

    public static void main(String[] args) {
        FileMap myFileMap = new FileMap(System.getProperty("db.file"));
        boolean flag = true;
        if (!myFileMap.readFromFile()) {
            flag = false;
        }
        if (args.length != 0) {
            if (!myFileMap.pocketMode(args)) {
                flag = false;
            }
        } else {
            if (!myFileMap.interactiveMode()) {
                flag = false;
            }
        }
        if (flag) {
            System.exit(0);
        } else {
            System.exit(-1);
        }
    }
}
