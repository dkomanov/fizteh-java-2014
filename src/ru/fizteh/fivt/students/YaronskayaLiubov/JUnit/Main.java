package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

public class Main {

    public static void main(String[] args) {
        boolean errorOccurred;
        try {
            errorOccurred = !new MultiFileHashMap().exec(args);
        } catch (Exception e) {
            System.err.println(e.toString());
            errorOccurred = true;
        }
        if (errorOccurred) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
