package ru.fizteh.fivt.students.SergeyAksenov.JUnit;

public class JUnitTableProviderFactory {

    public JUnitTableProviderFactory() {
    }

    public JUnitTableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("create");
        }
        return new JUnitTableProvider(dir);
    }

}
