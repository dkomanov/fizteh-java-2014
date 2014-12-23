package ru.fizteh.fivt.students.YaronskayaLiubov.StructuredDataTables;

/**
 * Created by luba_yaronskaya on 18.11.14.
 */
public class Main {
    public static void main(String[] args) {
        boolean errorOccurred;
        errorOccurred = !new MultiFileHashMap().exec(args);

        if (errorOccurred) {
            System.exit(1);
        } else {
            System.exit(0);
        }

    }
}
