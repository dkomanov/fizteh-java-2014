package ru.fizteh.fivt.students.AndrewTimokhin.FileMap;

import java.io.*;
import java.util.*;

public class MainClass {

    public static void main(String[] args) {
        Reader rd = new Reader(System.getProperty("db.file").toString());
        HashMap<String, String> coll = new HashMap<String, String>();
        coll = rd.read();
        Set<String> show = coll.keySet();
        ModeWork mw = new ModeWork(coll);
        if (args.length == 0) {
            mw.usermode();
        } else {
            mw.consol(args);
        }
        Functional fn = new Functional(coll);
        fn.list();
        Collection<String> value = coll.values();
        Write wr = new Write(args[0]);
        wr.write(coll);
    }
}
