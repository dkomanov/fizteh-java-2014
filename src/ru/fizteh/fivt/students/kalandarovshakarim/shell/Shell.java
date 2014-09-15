package ru.fizteh.fivt.students.kalandarovshakarim.shell;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.Vector;

/**
 *
 * @author shakarim
 */
class Path {
    Vector<String> path;
    
}

public class Shell {

    /**
     * @param args the command line argumentsr
     */
    public static void cd() {
        
    }
    
    public static void pwd() {
        
    }
    
    public static void mkdir(String dirName) {
        File dir;
        boolean isCreated = false;
        
        try {
            dir = new File(dirName);
            isCreated = dir.mkdir();
        } catch (Exception e) {
            System.out.print("Is dir created:" + isCreated);
        }
    }
    
    public static void cp() {
        
    }
    
    public static void mv() {
        
    }
    
    
    public static void ls() {
        String curDir = System.getProperty("user.dir");
        String[] paths;
        File file = new File(curDir);
        
        paths = file.list();
        
        for (String path : paths) {
            System.out.println(path);
        }
    }
    
    public static void cat() {
        
    }
    
    public static void parseCommand() {
        
    }
    
    //public static void 
    
    public static void main(String[] args) {
        // TODO code application logic here
        for (int i = 0; i < args.length; ++i) {
            System.out.println(args[i]); 
        }
      
        String curDir = System.getProperty("user.dir"); 
        System.out.println(curDir);
        
        
            File f = new File("/");
        
        
        try {
        //f.mkdir();
            f.createNewFile();
        } catch(Exception e) {
            
        }
        
        //System.setProperty("user.dir","");
        ls();

    }
}