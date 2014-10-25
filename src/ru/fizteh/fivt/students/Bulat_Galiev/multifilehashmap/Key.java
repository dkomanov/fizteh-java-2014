package ru.fizteh.fivt.students.Bulat_Galiev.multifilehashmap;

import java.io.UnsupportedEncodingException;

public class Key {

    private final int x;
    private final int y;

    public Key(final String key) throws UnsupportedEncodingException {
        int nbytes = key.getBytes("UTF-8")[0];
        int ndirectory = Math.abs(nbytes % 16);
        int nfile = Math.abs((nbytes / 16) % 16);
        this.x = ndirectory;
        this.y = nfile;
    }

    public Key(final int ndirectory, final int nfile) {
        this.x = ndirectory;
        this.y = nfile;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Key)) {
            return false;
        }
        Key key = (Key) o;
        return (x == key.x) && (y == key.y);
    }

    @Override
    public final int hashCode() {
        return 31 * x + y;
    }

}
