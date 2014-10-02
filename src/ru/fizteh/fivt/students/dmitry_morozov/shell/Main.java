package ru.fizteh.fivt.students.dmitry_morozov.shell;

import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    enum ErrorCode {
        SUCCESS, EXIT, NO_SUCH_COMMAND, ERROR_EXECUTING
    }

    private static String currentPath = "/";
    private static File currentFile = new File(currentPath);
    private static String error = "";

    public static String pathHandler(String path) {
        String[] splitted = path.split("/");
        boolean fromRoot = path.startsWith("/");
        int backSteps = 0; // How much steps must we do in current path
        int finishIndex = 0;
        String[] handledPath = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            if (splitted[i].equals("..")) {
                if (finishIndex > 0) {
                    finishIndex--;
                } else {
                    backSteps++;
                }
            } else if (!splitted[i].equals(".")) {
                handledPath[finishIndex] = splitted[i];
                finishIndex++;
            }
        }
        if (fromRoot) {
            String res = "";
            for (int i = 0; i < finishIndex; i++) {
                if (i != finishIndex - 1) {
                    res += handledPath[i] + "/";
                } else {
                    res += handledPath[i];
                }
            }
            return res;
        }

        String[] prefix = currentPath.split("/");
        String res = "";
        if (backSteps >= prefix.length) {
            for (int i = 0; i < finishIndex; i++) {
                if (i != finishIndex - 1) {
                    res += handledPath[i] + "/";
                } else {
                    res += handledPath[i];
                }
            }
        } else {
            for (int i = 0; i < prefix.length - backSteps; i++) {
                if (i != prefix.length - backSteps - 1) {
                    res += prefix[i] + "/";
                } else {
                    res += prefix[i];
                }
            }
            if (finishIndex != 0) {
                res += "/";
            }
            for (int i = 0; i < finishIndex; i++) {
                if (i != finishIndex - 1) {
                    res += handledPath[i] + "/";
                } else {
                    res += handledPath[i];
                }
            }
        }

        if (!res.startsWith("/")) {
            res = "/" + res;
        }
        return res;
    }

    public static ErrorCode catOne(String path) throws IOException {
        String hPath = pathHandler(path);
        File f = new File(hPath);
        if (!f.exists()) {
            error += "No such file: " + hPath + "\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        if (!f.canRead()) {
            error = error + "Can't read file " + path + "\n";
            return ErrorCode.ERROR_EXECUTING;
        }

        System.out.println(path + ":\n");
        FileReader fr = new FileReader(f);
        int c = fr.read();
        while (c > 0) {
            System.out.print((char) c);
            c = fr.read();
        }
        fr.close();
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode cat(String[] params, int bIndex, int eIndex)
            throws IOException {

        boolean errorOcured = false;
        for (int i = bIndex; i < eIndex; i++) {
            errorOcured = errorOcured
                    || (ErrorCode.ERROR_EXECUTING == catOne(params[i]));
        }

        return errorOcured ? ErrorCode.ERROR_EXECUTING : ErrorCode.SUCCESS;
    }

    public static ErrorCode cD(String path) {
        String candPath = pathHandler(path);
        File dir = new File(candPath);
        if (!dir.exists()) {
            error += "cd: '" + path + "': no such file or directory\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        if (!dir.isDirectory()) {
            error += "Is not a directory: " + path + "\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        currentPath = candPath;
        currentFile = dir;
        return ErrorCode.SUCCESS;

    }

    public static ErrorCode lsOne(String path) { // Handled path should be given
        File f = new File(path);
        if (!f.exists()) {
            error += "Directory doesn't exist: " + path + "\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        if (!f.isDirectory()) {
            error += path + " is not a directory\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        System.out.println(path + ":\n");
        String[] list = f.list();
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i]);
        }
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode ls(String[] params, int bIndex, int eIndex) {
        if (bIndex == eIndex) {
            return lsOne(currentPath);
        }
        boolean errorOcurred = false;
        for (int i = bIndex; i < eIndex; i++) {
            errorOcurred = errorOcurred
                    || (lsOne(pathHandler(params[i])) == ErrorCode.ERROR_EXECUTING);
        }
        return errorOcurred ? ErrorCode.ERROR_EXECUTING : ErrorCode.SUCCESS;
    }

    public static ErrorCode mkDirOne(String path) { // should be handled
        File dir = new File(path);
        if (dir.exists()) {
            error += path + " already exists\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        boolean success = dir.mkdir();
        if (!success) {
            error += "Wrong path: " + path + "\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode mkDir(String[] params, int bIndex, int eIndex) {
        if (bIndex == eIndex) {
            error += "Not enough parametres for mkdir: directory name expected\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        boolean errorOcurred = false;
        for (int i = bIndex; i < eIndex; i++) {
            errorOcurred = errorOcurred
                    || (mkDirOne(pathHandler(params[i])) == ErrorCode.ERROR_EXECUTING);
        }
        return errorOcurred ? ErrorCode.ERROR_EXECUTING : ErrorCode.SUCCESS;
    }

    public static boolean removeDirectory(File dir) { // returns false if
        // something was wrong
        File[] flist = dir.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                if (0 == flist[i].listFiles().length) {
                    if (!flist[i].delete()) {
                        error += "Can't remove " + flist[i].getAbsolutePath()
                                + "\n";
                        return false;
                    }
                } else {
                    if (!removeDirectory(flist[i])) {
                        return false;
                    }
                }
            } else {
                if (!flist[i].delete()) {
                    error += "Can't remove " + flist[i].getAbsolutePath()
                            + "\n";
                    return false;
                }
            }

        }
        return dir.delete();
    }

    public static ErrorCode rMOne(String path, boolean rec) {
        File f = new File(path);
        if (!f.exists()) {
            error += "rm: cannot remove '" + path
                    + "': No such file or directrory\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        if (f.isDirectory()) {
            if (!rec) {
                error += "rm: '" + path + "': is a directory\n";
                return ErrorCode.ERROR_EXECUTING;
            }
            if (!removeDirectory(f)) {
                return ErrorCode.ERROR_EXECUTING;
            }
        } else {
            if (!f.delete()) {
                error += "Can't remove file " + path + "\n";
                return ErrorCode.ERROR_EXECUTING;
            }
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode rM(String[] params, int bIndex, int eIndex) {
        if (bIndex == eIndex) {
            error += "Not enough parametres for rm\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        boolean rec = false;
        int keyIndex = -1;
        for (int i = bIndex; i < eIndex; i++) {
            if (params[i].equals("-r")) {
                rec = true;
                keyIndex = i;
                break;
            }
        }
        boolean errorOcurred = false;
        for (int i = bIndex; i < eIndex; i++) {
            if (i != keyIndex) {
                errorOcurred = errorOcurred
                        || (rMOne(pathHandler(params[i]), rec) == ErrorCode.ERROR_EXECUTING);
            }
        }
        return errorOcurred ? ErrorCode.ERROR_EXECUTING : ErrorCode.SUCCESS;
    }

    public static int fileCopy(File toCopy, String dest) throws IOException {
        /**
         * Return values: 0 - success, -1 - file already exists, -2 - toCopy
         * file is not readable
         */
        if (!toCopy.canRead()) {
            return -2;
        }
        File fDest = new File(dest);
        if (fDest.isDirectory()) {
            fDest = new File(dest + "/" + toCopy.getName());
        }
        if (!fDest.createNewFile()) {
            return -1;
        }
        FileInputStream fin = new FileInputStream(toCopy);
        FileOutputStream fout = new FileOutputStream(fDest);
        int readChar = fin.read();
        while (readChar != -1) {
            fout.write(readChar);
            readChar = fin.read();
        }
        fout.close();
        fin.close();
        return 0;
    }

    public static int directoryCopy(File dir, String dest) throws IOException {
        File cpDir = new File(dest);
        String newDest = dest;
        if (cpDir.exists()) {
            if (!cpDir.isDirectory()) {
                return -1;
            }
            newDest += "/" + dir.getName();
            cpDir = new File(newDest);
            if (!cpDir.mkdir()) {
                return -1;
            }
        } else {
            if (!cpDir.mkdir()) {
                return -1;
            }
        }

        File[] content = dir.listFiles();
        for (int i = 0; i < content.length; i++) {
            if (content[i].isDirectory()) {
                if (directoryCopy(content[i],
                        newDest + "/" + content[i].getName()) < 0) {
                    return -1;
                }
            } else if (fileCopy(content[i], newDest) < 0) {
                return -1;
            }
        }
        return 0;
    }

    public static ErrorCode cP(String[] params, int bIndex, int eIndex)
            throws IOException { // takes exactly to paramtres and an optional
        // key
        if (bIndex + 1 > eIndex) {
            error += "Not enough parametres for cp\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        boolean rec = false;
        if (params[bIndex].equals("-r")) {
            rec = true;
            bIndex++;
        }
        String toCopyPath = pathHandler(params[bIndex]);
        File toCopy = new File(toCopyPath);
        String dest = pathHandler(params[bIndex + 1]);
        if (!toCopy.isDirectory()) {
            int errcode = fileCopy(toCopy, dest);
            switch (errcode) {
            case 0:
                return ErrorCode.SUCCESS;
            case -1:
                error += "File " + params[bIndex] + " already exists\n";
                return ErrorCode.ERROR_EXECUTING;
            case -2:
                error += "Can't copy " + params[bIndex + 1] + "\n";
                return ErrorCode.ERROR_EXECUTING;
            default:
                break;
            }
        }
        if (!rec) {
            error += "cp: '" + params[bIndex]
                    + "' is a directory (not copied).\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        if (dest.startsWith(toCopyPath)) {
            error += "Can't copy directory " + params[bIndex]
                    + " into itself\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        if (directoryCopy(toCopy, dest) < 0) {
            error += "Failed copy " + params[bIndex] + "\n";
            return ErrorCode.ERROR_EXECUTING;
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode mV(String[] params, int bIndex, int eIndex)
            throws IOException {
        if (bIndex + 2 > eIndex) {
            error += "Not eniugh parametres for mv\n";
            return ErrorCode.ERROR_EXECUTING;
        }
        final int forCPParams = 3;
        String[] forCP = new String[forCPParams];
        forCP[0] = "-r";
        forCP[1] = params[bIndex];
        forCP[2] = params[bIndex + 1];
        if (ErrorCode.ERROR_EXECUTING == cP(forCP, 0, forCPParams)) {
            return ErrorCode.ERROR_EXECUTING;
        }
        if (ErrorCode.ERROR_EXECUTING == rMOne(pathHandler(params[bIndex]),
                true)) {
            return ErrorCode.ERROR_EXECUTING;
        }
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode functionHandler(String[] comAndParams, int bIndex,
            int eIndex) throws IOException {
        final int optAm = 9;
        String[] options = new String[optAm]; // Initializing options list
        options[0] = "exit"; //
        options[1] = "cat"; //
        options[2] = "ls"; //
        options[3] = "cd"; //
        options[4] = "rm"; //
        options[5] = "mv";
        options[6] = "pwd"; //
        options[7] = "mkdir"; //
        options[8] = "cp"; //

        int functionNumber;
        for (functionNumber = 0; functionNumber < options.length; functionNumber++) {
            if (options[functionNumber].equals(comAndParams[bIndex])) {
                break;
            }
        }

        switch (functionNumber) {
        case optAm: // Command not found
            error = comAndParams[0];
            return ErrorCode.NO_SUCH_COMMAND;
        case 0: // Exit
            return ErrorCode.EXIT;
        case 3: // CD
            if (bIndex == eIndex) {
                error += "Not enough parametres for cd\n";
                return ErrorCode.ERROR_EXECUTING;
            }
            return cD(comAndParams[bIndex + 1]);
        case 1: // Cat
            return cat(comAndParams, bIndex + 1, eIndex);
        case 6: // pwd
            System.out.println(currentPath);
            return ErrorCode.SUCCESS;
        case 2: // ls
            return ls(comAndParams, bIndex + 1, eIndex);
        case 7: // mkdir
            return mkDir(comAndParams, bIndex + 1, eIndex);
        case 4:
            return rM(comAndParams, bIndex + 1, eIndex);
        case 8: // cp
            return cP(comAndParams, bIndex + 1, eIndex);
        case 5:
            return mV(comAndParams, bIndex + 1, eIndex);
        default:
            return ErrorCode.SUCCESS;
        }
    }

    public static ErrorCode commandSplitting(String command) throws IOException {
        String[] firstSplitted = command.split(" ");
        String[] toGive = new String[firstSplitted.length];
        int j = 0;
        for (int i = 0; i < firstSplitted.length; i++) {
            if (firstSplitted[i].length() > 0) {
                toGive[j] = firstSplitted[i];
                j++;
            }
        }
        if (0 == j) {
            return ErrorCode.SUCCESS;
        }
        return functionHandler(toGive, 0, j);
    }

    public static void packetMode(String[] args) throws IOException {
        String currentLine = "";
        for (int i = 0; i < args.length; i++) {
            currentLine += args[i] + " ";
        }
        String[] commands = currentLine.split(";");
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].length() > 0) {
                ErrorCode exitCode = commandSplitting(commands[i]);
                if (ErrorCode.EXIT == exitCode) {
                    return;
                }
                switch (exitCode) {

                case NO_SUCH_COMMAND:
                    System.err.println("No such command: " + error);
                    return;

                case ERROR_EXECUTING:
                    System.err.println("Errors occured: " + error);
                    return;
                default:
                    break;
                }
                error = "";
                currentPath = currentFile.getAbsolutePath();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        if (args.length != 0) {
            packetMode(args);
            return;
        }
        Scanner in = new Scanner(System.in);
        boolean contFlag = true;
        while (contFlag) {
            System.out.print(currentPath + "$");
            String currentLine = in.nextLine();
            String[] commands = currentLine.split(";");
            for (int i = 0; i < commands.length; i++) {
                if (commands[i].length() > 0) {
                    ErrorCode exitCode = commandSplitting(commands[i]);
                    if (ErrorCode.EXIT == exitCode) {
                        contFlag = false;
                        break;
                    }
                    switch (exitCode) {

                    case NO_SUCH_COMMAND:
                        System.out.print("No such command: " + error);
                        i = commands.length;
                        break;
                    case ERROR_EXECUTING:
                        System.out.print("Errors occured: " + error);
                        break;
                    default:
                        break;
                    }
                    error = "";
                    currentPath = currentFile.getAbsolutePath();
                }
            }
        }

    }

}