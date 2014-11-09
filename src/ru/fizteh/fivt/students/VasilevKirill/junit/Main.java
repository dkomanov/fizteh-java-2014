package ru.fizteh.fivt.students.VasilevKirill.junit;

import java.io.*;

/**
 * Created by Kirill on 09.11.2014.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        MyTable table = new MyTable("C:\\java\\database\\First");
        String retValue = table.put("5", "value5");
        System.out.println(retValue);
        table.commit();
        table.rollback();
        System.out.println(table.get("5"));
    }
}
