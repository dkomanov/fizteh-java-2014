package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class DbMain {
    public static void main(String[] args) {
        FileMap myFileMap = new FileMap(System.getProperty("db.file"));
        myFileMap.addCommand(new Put());
        myFileMap.addCommand(new Get());
        myFileMap.addCommand(new List());
        myFileMap.addCommand(new Remove());
        boolean errorOcuried = false;
        errorOcuried = !myFileMap.init();
        if (args.length > 0) {
            errorOcuried = !myFileMap.packetMode(args);
        } else {
            errorOcuried = !myFileMap.interactiveMode();
        }
        if (errorOcuried) {
            System.exit(0);
        } else {
            System.exit(3);
        }
    }
}
