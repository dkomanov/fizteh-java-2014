package com.example.new_shele;

import java.io.File;
import java.io.IOException;

public class new_shele {
    public static void main (String args[]) {
        try {
          Func_shele f = new Func_shele();
            f.currentDirectory = System.getProperty("user.dir");
            if (args.length > 0) f.parsLine(args);
            else f.interactive();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1); }
        //Func_shele f = new Func_shele();
        //f.currentDirectory = System.getProperty("user.dir");

        //File f1 = new File(f.currentDirectory+File.separator+"TEST");
        //File f2 = new File(f.currentDirectory+File.separator+"TO");
        //try {
          //f.recursiveCopy(f1,f2);
        //} catch (IOException e) {
          // e.printStackTrace();
        //}

    }
    }



