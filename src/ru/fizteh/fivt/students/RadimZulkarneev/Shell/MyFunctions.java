package ru.fizteh.fivt.students.RadimZulkarneev.Shell;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
public final class MyFunctions {

    public static String myPwd() {
        return System.getProperty("user.dir");
    } 
    public static void myLs(final String currentDir) {
        try {
            File path = new File(currentDir);
            String[] files = path.list();
        
            for (int i = 0; i < files.length; ++i) {
                System.out.println(files[i]);
            }
        } catch (SecurityException ex) {
            System.out.println("ls: Access denied");
        }
    }
    public static boolean myCat(final String direct, final String qFile) {
        String sFile = direct + File.separator + qFile;
        try {
            FileReader myFile = new FileReader(sFile);
            @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(myFile);
            String s;
            try {
                while ((s = br.readLine()) != null) {
                    System.out.println(s);
                }
            } catch (IOException e) {
                return false;
            }
        } catch (FileNotFoundException e) {
            System.out.println("cat: " + qFile + ": No such file in directory");
            return false;
        }
        return true;
    }
    public static String myCd(final String currentDir, 
            final String newDir, final boolean isParse) throws FileNotFoundException {
        
        if (newDir.equals(".")) {
            return currentDir;
        }
        if (newDir.equals("..")) {
            String[] p;
            p  = currentDir.split("\\" + File.separator);
            if (p.length > 1) {
                String nDir = "";
                int i = 0;
                for (i = 0; i < p.length - 2; ++i) {
                        nDir += (p[i] + File.separator);
                }
                nDir += p[p.length - 2];
                if (p.length - 1 == 1) {
                    nDir += File.separator;
                }
                return nDir;
            } else {
                return currentDir;
            }
        } 

        File cFile = new File(currentDir + File.separator + newDir);
        if (!cFile.isDirectory()) {
            System.out.println("cd: '" + newDir + "': Not a directory");
            throw new FileNotFoundException();
        }
        if (cFile.isDirectory()) {
            Path q = cFile.toPath().normalize();
            return q.toString();
        }
        System.out.println("cd: '" + newDir + "': No such file or directory");
        if (isParse) {
            System.exit(1);
        }
        return currentDir;
    }
    
    public static boolean myMkdir(final String currentDir,
            final String newDir) {
        File sFile = new File(currentDir + File.separator + newDir);
        if (sFile.exists()) {
            System.out.println("Such file already exist");
            return false;
        }
        try {
            Files.createDirectories(sFile.toPath());
            return true;
        } catch (IOException e) {
            System.out.println("mkdir: Some errors");
            return false;
        }
        
    }
    public static boolean myRmDir(final String currentDir, final String ctDir) {
        File cFile = new File(currentDir + File.separator + ctDir);
        if (cFile.isFile()) {
            System.out.println("can't remove " + ctDir);
            return false;
        }
        // RECURSION
        if (cFile.isDirectory()) {
            MyFunctions.removeDir(currentDir + File.separator + ctDir);
            return true;
        } else {
            System.out.println("rm: cannot remove '" +  ctDir 
                    + "': No such file or directory");
            return false;
        }
    }
    public static boolean myRmFile(final String currentDir,
            final String ctDir) {
        File cFile = new File(currentDir + File.separator + ctDir);
        if (cFile.isDirectory()) {
            System.out.println("rm: " + ctDir + ": is a directory");
            return false;
        }
        if (!cFile.isFile()) {
            System.out.println("rm: cannot remove '" + ctDir 
                    + "': No such file or directory");
            return false;
        }
        cFile.delete();
        return true;
    }
    public static int myCpDir(final String currentDir, final String ctDir, 
            final String dest, final String command) {
        
        File ctFile = new File(currentDir + File.separator + ctDir);
        if (!ctFile.exists()) {
            ctFile = new File(ctDir);
            if (!ctFile.exists()) {
                System.out.println(command + ": '" + ctDir 
                        + "': No such file or directory");
                return -1;
            }
        } else {
            if (ctFile.isFile()) {
                if (MyFunctions.myCpFile(currentDir, ctDir, dest, command)) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        File destFile = new File(dest + File.separator + ctDir);
        if (!destFile.isAbsolute()) {
            destFile = new File(currentDir + File.separator 
                    + dest + File.separator + ctDir);
        }
        String ct = Paths.get(destFile.getParent()).relativize(
                ctFile.toPath()).toString();
       
        if (ct.equals("") || ct.matches("[\\/\\.]+") 
                || destFile.toString().equals(ctFile.toString())) {
            System.out.println("Can't copy '" + ctDir + "' into itself");
            return -1;
        }


        try {
            Files.createDirectories(destFile.toPath());
        } catch (IOException e) {
            System.out.println(command + ": Some errors");
        }
        myCpDirRealize(ctFile, destFile, command);
        return 1;
    }

    private static void myCpDirRealize(final File ctDir, final File dest, 
            final String command) {
        String[] ctFileList = ctDir.list();
        for (int i = 0; i < ctFileList.length; ++i) {
            File current = new File(ctDir.getAbsoluteFile() + File.separator 
                    + ctFileList[i]);
            if (current.isFile()) {
                myCpFile(ctDir.getAbsolutePath(), ctFileList[i], 
                        dest.getAbsolutePath(), command);
            } else {
                MyFunctions.myMkdir(dest.getAbsolutePath(), ctFileList[i]);
                File push = new File(dest.getAbsolutePath() + File.separator 
                        +  ctFileList[i]);
                File ctpush = new File(ctDir.getAbsolutePath() + File.separator 
                        +  ctFileList[i]);
                MyFunctions.myCpDirRealize(ctpush, push, command);
            }
        }
    }
    public static boolean myCpFile(final String currentDir, final String ctDir, 
            final String dest, final String command) {
        File ctFile = new File(currentDir + File.separator + ctDir);
        if (!ctFile.exists()) {
            System.out.println(command + ": '" 
        + ctDir + "': No such file or directory");
            return false;
        }
        if (ctFile.isDirectory()) {
            System.out.println(command + ": " + ctDir +  " is a directory");
            return false;
        }
        File qFile = new File(dest);
        
        if (!qFile.isAbsolute()) {
            qFile = new File(currentDir + File.separator + dest);
        }
        
        if (!qFile.exists()) {
            File newFile = new File(currentDir + File.separator + dest);
            try {
                Files.copy(ctFile.toPath(), newFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                return true;    
            } catch (IOException e) {
               System.out.println("cp: Some errors");
               return false;
            }
            
            
        }
        File assertFile = new File(qFile.getPath() + File.separator + ctDir);
        if (assertFile.exists() && !command.equals("mv")) {
            System.out.println(command + ": File already exist");
            return false;
        }
        try {
            File newFile = new File(qFile.getPath() + File.separator + ctDir);
            if (newFile.exists()) {
                Files.delete(newFile.toPath());
            }
            Files.copy(ctFile.toPath(), newFile.toPath());
         
            return true;
        } catch (IOException e) {     
            System.out.println(command + ": Some errors  " + e.getMessage());
            return false;
        }
    }

    public static boolean myMv(final String currentDir, final String ctDir, 
            final String dest) {
        File ctFile = new File(currentDir + File.separator + ctDir);
        if (!ctFile.exists()) {
            System.out.println("mv: '" +  ctDir 
                    + "': No such file or directory");
            return false;
        }
        File destFile = new File(dest);
        if (!destFile.isAbsolute()) {
            destFile = new File(currentDir + File.separator + dest);
        }
            
        String ct = Paths.get(destFile.getParent()).relativize(
                ctFile.toPath()).toString();
       
        if (ct.equals("") || ct.matches("[\\/\\.]+") 
                || destFile.toString().equals(ctFile.toString())) {
            System.out.println("Can't move '" + ctDir + "' into itself");
            return false;
        }
        if (ctFile.isFile()) {
            if (MyFunctions.myCpFile(currentDir, ctDir, dest, "mv")) {
                return MyFunctions.myRmFile(currentDir, ctDir);
            } else {
                return false;
            }
        } else {
            String[] ctFiles = destFile.list();
            if (ctFiles.length > 0) {
                System.out.println("mv: cannot move '" + ctFile + "' to '" 
            + dest + "': Diretory is not empty");
                return false;
            }
            if (MyFunctions.myCpDir(currentDir, ctDir, destFile.getPath(),
                    "mv") == 1) {
                return MyFunctions.myRmDir(currentDir, ctDir);
            } else {
                return false;
            }
        }
        
    }
    private static void removeDir(final String currentDir) {
        File path = new File(currentDir);
        String[] files = path.list();
        for (int i = 0; i < files.length; ++i) {
            File curFile = new File(currentDir + File.separator + files[i]);
            if (curFile.isDirectory()) {
                MyFunctions.removeDir(curFile.getPath());                
            }
            curFile.delete();
        }
        path.delete();
    }
    private MyFunctions() {
    }
}   
