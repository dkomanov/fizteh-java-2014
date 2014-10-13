package ru.fizteh.fivt.students.AndrewTimokhin.FileMap;

import java.io.*;
import java.util.*;

public class MainClass {

    public static void main(String[] args) {
        Reader rd = new Reader(System.getProperty("db.file").toString());
        Map<String, String> coll = new HashMap<String, String>();
        coll = rd.read();
        Set<String> show = coll.keySet();
        ModeWork mw = new ModeWork(coll);
        if (args.length == 0) {
            mw.usermode();
        } else {
            mw.interactive(args);
        }
        Functional fn = new Functional(coll);
        Write wr = new Write(System.getProperty("db.file").toString());
        wr.write(coll);
    }
}
