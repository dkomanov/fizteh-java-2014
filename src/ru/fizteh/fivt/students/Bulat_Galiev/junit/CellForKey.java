package ru.fizteh.fivt.students.Bulat_Galiev.junit;

import java.io.UnsupportedEncodingException;

public class CellForKey {

    private static final int KEY_NUMBER = 16;
    private static final int HASH_NUMBER = 16;
    private final int x;
    private final int y;

    public CellForKey(final String key) throws UnsupportedEncodingException {
        int nbytes = key.getBytes("UTF-8")[0];
        int ndirectory = Math.abs(nbytes % KEY_NUMBER);
        int nfile = Math.abs((nbytes / KEY_NUMBER) % KEY_NUMBER);
        this.x = ndirectory;
        this.y = nfile;
    }

    public CellForKey(final int ndirectory, final int nfile) {
        this.x = ndirectory;
        this.y = nfile;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CellForKey)) {
            return false;
        }
        CellForKey key = (CellForKey) o;
        return (x == key.x) && (y == key.y);
    }

    @Override
    public final int hashCode() {
        return HASH_NUMBER * x + y;
    }

}
