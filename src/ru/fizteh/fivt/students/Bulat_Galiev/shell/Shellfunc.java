package ru.fizteh.fivt.students.Bulat_Galiev.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;

public final class Shellfunc {
    private Shellfunc() {
        // not called
    }

    public static void ls() {
        File dir = new File(System.getProperty("user.dir"));
        File[] files = dir.listFiles();
        boolean isHidden = false;
        for (File file : files) {
            isHidden = file.isHidden(); // check whether file if hidden
            if (!isHidden) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                } else if (file.isDirectory()) {

                    System.out.println(file.getName());

                }

            }
        }

    }

    public static void cp(final String arg1, final String arg2,
            final String arg3) throws IOException {
        if (arg1.equals("-r")) {
            File fsrc = new File(arg2);
            File fdst = new File(arg3);
            if (fsrc == null || fdst == null) {
                System.out.println("wrong arguments");
                return;
            }

            reccopy(fsrc, fdst);
        } else {
            File fsrc = new File(arg1);
            File fdst = new File(arg2);
            if (fsrc == null || fdst == null) {
                System.out.println("wrong arguments");
                return;
            }
            if (fsrc.isDirectory()) {
                System.out.println(arg1 + ("is a directory (not copied)"));
            }
            copy(fsrc, fdst);
        }

    }

    public static void reccopy(final File fsrc, final File fdstarg)
            throws IOException {
        try {
            File fdst = fdstarg;
            if (fsrc.isDirectory()) {
                fdst = new File(fdst.getAbsolutePath() + File.separatorChar
                        + fsrc);
                if (!fdst.exists()) {
                    fdst.mkdirs();
                }
                String[] fList = fsrc.list();

                for (String subdirent : fList) {
                    File fdest = new File(fdst, subdirent);
                    File fsource = new File(fsrc, subdirent);

                    reccopy(fsource, fdest);
                }
            } else {
                copy(fsrc, fdst);
            }
        } catch (IOException e) {
            System.out.println("File not found ");
            return;
        }
    }

    public static void copy(final File src, final File dst) throws IOException {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out;
            if (dst.isDirectory()) {
                out = new FileOutputStream(dst.getAbsolutePath()
                        + File.separatorChar + src);
            } else {
                out = new FileOutputStream(dst);
            }

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.println("File not found ");
            return;
        }

    }

    public static void cat(final String fname) throws IOException {
        if (fname == null) {
            System.out.println("wrong arguments");
            return;
        }
        try {

            BufferedReader in2 = new BufferedReader(
                    new FileReader(System.getProperty("user.dir")
                            + File.separatorChar + fname)); // separator \ on
            // windows and / on
            // linux
            String line = "";
            while ((line = in2.readLine()) != null) {
                System.out.print(line);
            }
            System.out.print("\n");
            in2.close();
        } catch (IOException e) {
            System.out.println(fname + ("No such file or directory"));
            return;
        }
    }

    public static void rm(final String arg1, final String arg2)
            throws IOException {
        if (arg1.equals("-r")) {
            File fdfile = new File(arg2);
            if (fdfile.exists()) {
                if (!recremove(fdfile)) {
                    System.out.println("Failed to delete");
                }
            } else {
                System.out.println("No such file or directory");
            }

        } else {
            File fdfile = new File(arg1);
            if (!fdfile.isAbsolute()) {
                fdfile = new File(fdfile.getAbsolutePath());
            }
            if (fdfile.exists()) {
                if (!remove(fdfile)) {
                    System.out.println("Failed to delete");
                }
            } else {
                System.out.println("No such file or directory");
            }
        }

    }

    public static boolean recremove(final File dfile) throws IOException {
        if (dfile.isDirectory()) {
            String[] fList = dfile.list();
            for (String subdirent : fList) {
                boolean success = recremove(new File(dfile, subdirent));
                if (!success) {
                    return false;
                }
            }
            return remove(dfile);
        } else {
            return remove(dfile);
        }

    }

    public static boolean remove(final File dfile) throws IOException {
        if (dfile.isDirectory() && dfile.list().length > 0) {
            System.out.println("The directory is not empty!");
            return false;
        } else {
            return dfile.delete();
        }
    }

    public static void mkdir(final String src) {
        boolean success = (new File(System.getProperty("user.dir")
                + File.separatorChar + src).mkdir());
        if (!success) {
            System.out.println("Failed to create the directory");
        }
    }

    public static void cd(final String dirName) {

        if (dirName == null || dirName.equals("")) {
            return;
        }
        File newDir = new File(dirName);
        String origPath = null;
        if (!newDir.isAbsolute()) {
            origPath = newDir.getPath();
            newDir = new File(newDir.getAbsolutePath());
        }
        if (newDir.exists()) {

            if (!newDir.isDirectory()) {
                if (origPath == null) {
                    System.out.println(newDir.getPath()
                            + (" is not a Directory"));
                } else {
                    System.out.println(origPath + (" is not a Directory"));
                }
                return;
            }
            try {
                System.setProperty("user.dir", newDir.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println((dirName) + (" Directory does not exist"));
            System.out.println(dirName);
        }

    }

    public static void mv(final String src, final String des) {
        File file = new File(src);
        File dir = new File(des);
        Path srcpath = file.toPath();
        Path despath = new File(dir, file.getName()).toPath();
        try {
            Files.move(srcpath, despath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void pwd() {
        System.out.println(System.getProperty("user.dir"));
    }
}
