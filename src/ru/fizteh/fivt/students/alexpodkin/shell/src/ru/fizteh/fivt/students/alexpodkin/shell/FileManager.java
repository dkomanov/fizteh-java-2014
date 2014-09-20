package ru.fizteh.fivt.students.alexpodkin.shell;

import java.io.*;

public class FileManager {

    private File currentDirectory = new File("").getAbsoluteFile();
    private PrintStream printStream = System.out;


    public File getCurrentPath() {
        return currentDirectory;
    }

    public boolean changeCurrentPath(File newPath) {
        try {
            currentDirectory = newPath.getCanonicalFile();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public File getFileByPath(String path) {
        File resultFile = new File(path);
        if (resultFile.exists() && resultFile.isAbsolute()) {
            try {
                return resultFile.getCanonicalFile();
            } catch (IOException e) {
                return null;
            }
        }
        resultFile = new File(currentDirectory.getAbsolutePath());
        String[] files = path.split("\\" + File.separator);
        for (String file : files) {
            if (!file.equals(".")) {
                if (file.equals("..")) {
                    if (resultFile.getParent() != null) {
                        resultFile = resultFile.getParentFile();
                    }
                } else {
                    resultFile = new File(resultFile.getAbsolutePath() + File.separator + file);
                }
            }
        }
        return resultFile;
    }

    public boolean recursiveRemove(File toRemove, String command) {
        if (toRemove.isDirectory()) {
            File[] files = toRemove.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!recursiveRemove(file, command)) {
                        return false;
                    }
                }
            }
        }
        try {
            if (!toRemove.delete()) {
                printStream.println(command + ": " + toRemove.getAbsolutePath() + ": couldn't remove file");
                return false;
            }
            return true;
        } catch (SecurityException e) {
            printStream.println(command + ": " + toRemove.getAbsolutePath() + ": couldn't remove file");
        }
        return false;
    }

    public boolean simpleRemove(File toRemove, String command) {
        if (toRemove.isDirectory()) {
            File[] files = toRemove.listFiles();
            if (files != null && files.length > 0) {
                printStream.println(command + ": " + toRemove.getAbsolutePath() + ": directory is not empty");
                return false;
            }
        }
        try {
            if (!toRemove.delete()) {
                printStream.println(command + ": " + toRemove.getAbsolutePath() + ": couldn't remove file");
                return false;
            }
            return true;
        } catch (SecurityException e) {
            printStream.println(command + ": " + toRemove.getAbsolutePath() + ": couldn't remove file");
        }
        return false;
    }

    public boolean copy(File sourceFile, File finishFile, String command) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(finishFile);
            byte[] inputFileBuffer = new byte[4096];
            while (true) {
                int bufferLength = inputStream.read(inputFileBuffer);
                if (bufferLength < 0) {
                    break;
                }
                outputStream.write(inputFileBuffer, 0, bufferLength);
            }
            return true;
        } catch (IOException e) {
            printStream.println(command + ": " + finishFile.getAbsolutePath() + " : this file already exists");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    printStream.println(command + ": " + sourceFile.getAbsolutePath() + " : can't close this file");
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    printStream.println(command + ": " + finishFile.getAbsolutePath() + " : can't close this file");
                }
            }
        }
        return false;
    }

    public boolean recursiveCopy(File sourceFile, File finishFile, String command) {
        if (sourceFile.isDirectory()) {
            try {
                if (!finishFile.mkdir()) {
                    printStream.println(command + ": " +
                            finishFile.getAbsolutePath() + " : can't create this directory");
                    return false;
                }
            } catch (SecurityException e) {
                printStream.println(command + ": " +
                        finishFile.getAbsolutePath() + " : you haven't rights to create this directory");
            }
            File[] files = sourceFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    File newFile = new File(finishFile.getAbsolutePath() + File.separator + file.getName());
                    if (!recursiveCopy(file, newFile, command)) {
                        return false;
                    }
                }
            }
        } else {
            try {
                if (!finishFile.createNewFile()) {
                    printStream.println(command + ": " +
                            finishFile.getAbsolutePath() + " : can't create this file");
                    return false;
                }
                if (!sourceFile.canWrite() || !finishFile.canRead()) {
                    printStream.println(command + ":" +
                            finishFile.getAbsolutePath() + " : you haven't rights to create this file");
                    return false;
                }
                return copy(sourceFile, finishFile, command);
            } catch (IOException e) {
                printStream.println(command + ": " +
                        finishFile.getAbsolutePath() + " : can't create this file");
                return false;
            } catch (SecurityException e) {
                printStream.println(command + ":" +
                        finishFile.getAbsolutePath() + " : you haven't rights to create this file");
                return false;
            }
        }
        return true;
    }

    public boolean recursiveCopyWithErrorMessages(File sourceFile, File finishFile, String command) {
        if (sourceFile == null || !sourceFile.exists()) {
            printStream.println(command + ": invalid input file");
            return false;
        }
        if (finishFile == null || !finishFile.getParentFile().exists()) {
            printStream.println(command + ": invalid output directory");
            return false;
        }
        if (sourceFile.isDirectory() && finishFile.isFile()) {
            printStream.println(command + ": invalid input arguments");
            return false;
        }
        return recursiveCopy(sourceFile, finishFile, command);
    }
}
