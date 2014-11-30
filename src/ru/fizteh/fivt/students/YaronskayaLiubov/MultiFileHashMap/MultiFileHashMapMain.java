package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class MultiFileHashMapMain {
    public static void main(String[] args) {
        boolean errorOccurred;
        try {
            errorOccurred = !MultiFileHashMap.exec(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            errorOccurred = true;
        }
        if (errorOccurred) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
