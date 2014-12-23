package storeableparallel.util;

import java.io.File;

import storeableparallel.structured.*;

public class MyTableProviderFactory implements TableProviderFactory{
    public MyTableProviderFactory() {
    }

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Property doesn't given");
        }
        File file = new File(dir);
        file.mkdir();
        
        return new MyTableProvider(file);
    }
}
