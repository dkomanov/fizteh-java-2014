package ru.fizteh.fivt.students.kalandarovshakarim.shell;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.List;

/**
 *
 * @author shakarim
 */

public class Shell {

    public static void printStrArray(String[] list) {
        for (String output : list) {
            System.out.println(output);
        }
    }
    
    public static void readAndWrite(InputStream in, OutputStream out) {
        int symbolRead;
        byte[] buffer = new byte[256];
        try {
            while ((symbolRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, symbolRead);
            }
            out.flush();
        } catch (Exception e) {}
    }
    
    public static String[] ls() {
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir);
        
        return file.list();
    }
    
    public static String pwd() {
        return System.getProperty("user.dir");
    }
    
    public static boolean mkdir(String dirName) {
        File dir;
        boolean isCreated = false;
        
        try {
            dir = new File(pwd() + "/" + dirName);
            isCreated = dir.mkdir();
        } catch (Exception e) {
            System.out.print("Is dir created:" + isCreated);
        }
        
        return isCreated;
    }
    
    public static boolean cd(String destination) {
        File dirToGo = new File(destination);;
        
        if (dirToGo.exists() == true && dirToGo.isDirectory() == true) {
            String newPath;
            try {
                newPath = dirToGo.getCanonicalPath();
                System.setProperty("user.dir", newPath);
            } catch (Exception e) {}
            
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean cp(String source, String destination) {
        File srcFile = new File(source);
        File destFile = new File(destination);
        
        try {
            if (srcFile.exists() == true && destFile.createNewFile() == true) {
                InputStream in = 
                        new BufferedInputStream(new FileInputStream(srcFile));
                OutputStream out = 
                        new BufferedOutputStream(
                                new FileOutputStream(destFile));
                readAndWrite(in, out);
                out.close();
                in.close();
                return true;
            }
            
        } catch (Exception e) {}
        return false;
    }
    
    public static boolean mv(String source, String destination) {
        if (cp(source,destination) == true) {
            File srcFile = new File(source);
            srcFile.delete();
            return true;
        } else {
            return false;
        }
        
    }
    
    public static boolean cat(String source) {
        File srcFile = new File(source);
        
        if (srcFile.exists() == true) {
            try {
                InputStream in = 
                        new BufferedInputStream(new FileInputStream(srcFile));
                
                readAndWrite(in, System.out);
                
                in.close();
                return true;
            } catch (Exception e) {}
        }
        return false;
    }
    
    public static void main(String[] args) {
        /*
        printStrArray(ls());
        cp("nbbuild.xml","mani2.mf");
        cat("nbbuild.xml");
        System.out.println(pwd());
        System.out.println(cd("../sjkadj/../askdjakdj/.."));
        System.out.println(pwd());
         * 
         */
    }
}