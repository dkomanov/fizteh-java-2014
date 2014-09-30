package shell_util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CpCommand extends Command {
    /**
     * {@inheritDoc}
     * @see Command#CpCommand(String[])
     */
    CpCommand(final String[] cmd) {
        super(cmd);
    }

    /**
     * {@inheritDoc}
     * @see Command#run()
     */
    public final void run() throws Exception {
        if (args.length < 3 || (args[1] == "-r" && args.length < 4)) {
            throw new Exception("cp: missing file operand");
        }
        File src, dest;

        if (args[1].equals("-r")) {
            src = new File(System.getProperty("user.dir"), args[2]);
            dest = new File(System.getProperty("user.dir"), args[3]);
        } else {
            src = new File(System.getProperty("user.dir"), args[1]);
            dest = new File(System.getProperty("user.dir"), args[2]);
            if (src.isDirectory()) {
                throw new Exception("cp: Is a directory");
            }
        }
        if (!src.exists()) {
            throw new Exception("cp: No such file or directory");
        }
        if (dest.exists()) {
            dest = new File(dest, src.getName());
        }
        try {
            copy(src, dest);
        } catch (Exception e) {
            throw new Exception("cp: cannot copy file");
        }
    }

    /**
     * .
     */
    private void copy(final File src, final File dest) throws Exception {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String[] files = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copy(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }
}
