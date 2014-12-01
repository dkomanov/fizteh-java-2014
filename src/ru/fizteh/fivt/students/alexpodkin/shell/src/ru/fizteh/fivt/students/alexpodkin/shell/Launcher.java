package ru.fizteh.fivt.students.alexpodkin.shell;

import java.io.*;
import java.util.HashSet;

public class Launcher {

    private StringParser stringParser;
    private PrintStream printStream = System.out;
    private HashSet<String> commandsList;
    private boolean exitFlag = false;
    private FileManager fileManager = new FileManager();

    public Launcher(HashSet<String> commands, StringParser parser) {
        stringParser = parser;
        commandsList = commands;
    }

    public boolean launch(String[] arguments) throws IOException {
        if (arguments.length == 0) {
            return exec(System.in, false);
        } else {
            StringBuilder builder = new StringBuilder();

            for (String argument : arguments) {
                builder.append(argument);
                builder.append(" ");
            }

            String request = builder.toString().replaceAll(";", "\n");
            InputStream inputStream = new ByteArrayInputStream(request.getBytes("UTF-8"));

            return exec(inputStream, true);
        }
    }

    private boolean exec(InputStream inputStream, boolean isPackage) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while (true) {
            if (!isPackage) {
                printStream.print("$ ");
            }

            String request = bufferedReader.readLine();
            if (request == null) {
                break;
            }
            String[] commands = request.split(";");

            boolean jobCheck = false;

            for (String command : commands) {
                String[] arguments = stringParser.parse(command);
                if (arguments.length == 0 || arguments[0].equals("")) {
                    continue;
                }
                if (!commandsList.contains(arguments[0])) {
                    printStream.println(arguments[0] + ": command not found");
                } else {
                    switch (arguments[0]) {
                        case "ls":
                            jobCheck = myLs(arguments);
                            break;
                        case "pwd":
                            jobCheck = myPwd(arguments);
                            break;
                        case "cd":
                            jobCheck = myCd(arguments);
                            break;
                        case "mkdir":
                            jobCheck = myMkdir(arguments);
                            break;
                        case "rm":
                            jobCheck = myRm(arguments);
                            break;
                        case "cat":
                            jobCheck = myCat(arguments);
                            break;
                        case "cp":
                            jobCheck = myCp(arguments);
                            break;
                        case "mv":
                            jobCheck = myMv(arguments);
                            break;
                        default:
                            jobCheck = myExit(arguments);
                    }
                }
                if (exitFlag) {
                    return true;
                }
                if (!jobCheck && isPackage) {
                    exitFlag = true;
                    return false;
                }
            }
        }
        exitFlag = true;
        return true;
    }

    private boolean myCd(String[] arguments) {
        if (arguments.length != 2) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        File resultPath = fileManager.getFileByPath(arguments[1]);
        if (resultPath == null || !resultPath.exists()) {
            printStream.println(arguments[0] + ": " + arguments[1] + " : no such file or directory");
            return false;
        } else {
            if (!resultPath.isDirectory()) {
                printStream.println(arguments[0] + ": " + arguments[1] + " : it is a file");
                return false;
            } else {
                return fileManager.changeCurrentPath(resultPath);
            }
        }
    }

    private boolean myMkdir(String[] arguments) {
        if (arguments.length != 2) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        try {
            File newDirectory = new File(fileManager.getCurrentPath().getAbsolutePath()
                    + File.separator + arguments[1]);
            if (newDirectory.exists()) {
                printStream.println(arguments[0] + ": " + arguments[1] + " : this directory already exists");
                return false;
            } else {
                if (!newDirectory.mkdir()) {
                    printStream.println(arguments[0] + ": " + arguments[1] + " : couldn't create directory");
                    return false;
                }
            }
            return true;
        } catch (SecurityException e) {
            printStream.println(arguments[0] + ": " + arguments[1] + " : couldn't create directory");
        }
        return false;
    }

    private boolean myExit(String[] arguments) {
        if (arguments.length != 1) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        exitFlag = true;
        return true;
    }

    private boolean myLs(String[] arguments) {
        if (arguments.length != 1) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        String[] files = fileManager.getCurrentPath().list();
        if (files.length > 0) {
            for (String file : files) {
                printStream.println(file);
            }
        }
        return true;
    }

    private boolean myPwd(String[] arguments) {
        if (arguments.length != 1) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        printStream.println(fileManager.getCurrentPath().getAbsolutePath());
        return true;
    }

    private boolean myRm(String[] arguments) {
        if (arguments.length != 2 && arguments.length != 3) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        if (arguments.length == 2) {
            return fileManager.simpleRemove(fileManager.getFileByPath(arguments[1]), arguments[0]);
        } else {
            if (!arguments[1].equals("-r")) {
                printStream.println(arguments[0] + ": invalid arguments");
                return false;
            }
            return fileManager.recursiveRemove(fileManager.getFileByPath(arguments[2]), arguments[0]);
        }
    }

    private boolean myCat(String[] arguments) {
        if (arguments.length != 2) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        File file = fileManager.getFileByPath(arguments[1]);
        try {
            if (file.isDirectory()) {
                printStream.println((arguments[0] + ": " + arguments[1] + " : it is a directory"));
                return false;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                printStream.println(string);
            }
            return true;
        } catch (FileNotFoundException e) {
            printStream.println(arguments[0] + ": " + arguments[1] + " : no such file or directory");
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean myCp(String[] arguments) {
        if (arguments.length != 3 && arguments.length != 4) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        File sourceFile = fileManager.getFileByPath(arguments[1]);
        File finishFile = fileManager.getFileByPath(arguments[2]);
        if (arguments.length == 3) {
            if (sourceFile.exists() && sourceFile.isDirectory()) {
                if (sourceFile.listFiles() != null && sourceFile.listFiles().length > 0) {
                    printStream.println(arguments[0] + ": "
                            + sourceFile.getAbsolutePath() + " : this directory isn't empty");
                    return false;
                }
            }
            if (finishFile.exists() && finishFile.isDirectory()) {
                if (finishFile.listFiles() != null && finishFile.listFiles().length > 0) {
                    printStream.println(arguments[0] + ": "
                            + finishFile.getAbsolutePath() + " : this directory isn't empty");
                    return false;
                }
            }
            return fileManager.recursiveCopyWithErrorMessages(sourceFile, finishFile, arguments[0]);
        } else {
            if (!arguments[1].equals("-r")) {
                printStream.println(arguments[0] + ": invalid arguments");
                return false;
            }
            return fileManager.recursiveCopyWithErrorMessages(sourceFile, finishFile, arguments[0]);
        }
    }

    private String fileNameGenerator(String oldName, String command) {
        if (oldName.equals("")) {
            printStream.println(command + ": wrong input file or directory");
            return null;
        }
        if (!fileManager.getFileByPath(oldName).exists()) {
            return oldName;
        }
        String[] newFile = oldName.split("\\.");
        int fileNumber = 0;
        while (true) {
            fileNumber++;
            String newFileName = newFile[0] + Integer.toString(fileNumber);
            if (newFile.length > 1) {
                newFileName = newFileName + "." + newFile[1];
            }
            if (!fileManager.getFileByPath(newFileName).exists()) {
                return newFileName;
            }
        }
    }

    private boolean myMv(String[] arguments) {
        if (arguments.length != 3) {
            printStream.println(arguments[0] + ": invalid number of arguments");
            return false;
        }
        String newFileName = fileNameGenerator(arguments[2], arguments[0]);
        if (newFileName == null) {
            return false;
        }
        if (fileManager.getFileByPath(arguments[2]) == null) {
            printStream.println(arguments[0] + ": invalid destination path");
        }
        File sourceFile = fileManager.getFileByPath(arguments[1]);
        File finishFile = new File(fileManager.getFileByPath(arguments[2]).getParentFile()
                + File.separator + newFileName);
        return fileManager.recursiveCopyWithErrorMessages(sourceFile, finishFile, arguments[0])
                && fileManager.recursiveRemove(sourceFile, arguments[0]);
    }
}
