package ru.fizteh.fivt.students.anastasia_ermolaeva.shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.InvalidPathException;

public final class Commands {
    private Commands() {
        //
    }
    static void main(final String[] args) {
    }

    public static void cd(final String[] args) throws Exception {
        String command = "cd";
        if (args.length == 1) {
            throw new Exception(command + ": missing operand");
        } else {
            if (args.length > 2) {
                throw new Exception(command
                        + ": too much arguments");
            }
        }
        try {
            String newPath = args[1];
            if (newPath.equals(".")) {
                return;
            } else {
                if (newPath.equals("..")) {
                    String path = System.getProperty("user.dir");
                    File oldCurrent = new File(path);
                    System.setProperty("user.dir", oldCurrent.getParent());
                } else {
                    File directory = new File(newPath);
                    if (!directory.isAbsolute()) {
                        String current = System.getProperty("user.dir");
                        directory = new File(current
                                + File.separator + directory);
                    }
                    if (directory.exists()) {
                        if (directory.isDirectory()) {
                            String absolutePath = directory.getAbsolutePath();
                            System.setProperty("user.dir", absolutePath);
                        } else {
                            throw new Exception(command + ": '"
                                    + args[1] + "': is not a directory");
                        }
                    } else {
                        throw new Exception(command + ": '"
                                + args[1] + "': No such file or directory");
                    }
                }
            }
        } catch (InvalidPathException e) {
            throw new Exception(command
                   + ": cannot change directory to '" 
                   + args[1] + "': illegal character in path");
        } catch (SecurityException e) {
            throw new Exception(command
                    + ": cannot change directory: access denied");
        }
    }
    public static void mkdir(final String[] args) throws Exception {
        String command = "mkdir";
        if (args.length == 1) {
            throw new Exception(command + ": missing operand");
        } else {
            if (args.length > 2) {
                throw new Exception(command
                        + ": too much arguments");
            }
        }
        try {
            String dirName = args[1];
            String path = System.getProperty("user.dir");
            File newDirectory = new File(path + File.separator + dirName);
            if (args[1].isEmpty()) {
            throw new Exception(command + ": cannot create '" 
            + "': no such file or directory");
            }
            if (newDirectory.exists()) {
                throw new Exception(command + ": cannot create directory '" 
                + args[1] + "': File exists");
            } else {
                if (!newDirectory.mkdir()) {
                    throw new Exception(command + 
                    ": cannot create directory '" 
                    + args[1]);
                }
            }
        } catch (InvalidPathException e) {
            throw new Exception(command 
            + ": cannot create directory '" 
            + args[1] + "': illegal character in name");
        } catch (SecurityException e) {
            throw new Exception(command 
            + ": cannot create directory '" 
            + args[1] + "': access denied");
        }
    }
    public static void pwd(final String[] args) throws Exception {
        String command = "pwd";
        if (args.length != 1) {
            throw new Exception(command 
            + ": too much arguments");
        }
        try {
            String path = System.getProperty("user.dir");
            System.out.println(path);
        } catch (SecurityException e) {
            throw new Exception(command 
            + ": cannot read current working directory: access denied");
        }
    }

    private static void rmutil(final File name)throws Exception {
        String command = "rm";
        if (name.isDirectory()) {
            for (File c: name.listFiles()) {
                rmutil(c);
            }
        }
        if (!name.delete()) {
            throw new Exception(command 
            + ": cannot remove a file '" 
            + name.getName() + "'");
        }
    }
    public static void rm(final String[] args) throws Exception {
        String command = "rm";
        int index;
        boolean recursive;
        String file;
        String [] idealCmd = {"rm", "-r", "file.txt"};
        int suitableAmount = idealCmd.length;
        if (args.length > suitableAmount) {
            throw new Exception(command + ": too much arguments");
        } else
            if (args.length == suitableAmount) {
                if (args[1].equals("-r")) {
                    recursive = true;
                    file = args[2];
                    index = 2;
                } else {
                    throw new Exception(command + ": too much arguments");
                }
            } else {
                if (args.length != 2) {
                    throw new Exception(command + ": missing operand");
                } else {
                    recursive = false;
                    file = args[1];
                    index = 1;
                }
            }
        try {
            File object = new File(file);
            if (!object.isAbsolute()) {
                String current = System.getProperty("user.dir");
                object = new File(current + File.separator + file);
            }
            if (args[index].isEmpty()) {
				throw new Exception(command 
				+ ": cannot remove '" 
				+ args[index] 
				+ "': No such file or directory");
			} else {
                if (object.exists()) {
                    if (object.isFile()) {
                        if (!object.delete()) {
                            throw new Exception(command 
                            + ": cannot remove a file '" + file + "'");
                        }
                    } else {
                        if (object.isDirectory()) {
                            if (recursive) {
                                rmutil(object);
                            } else {
                                throw new Exception(command + ": " 
                                + file + ": is a directory");
                            }
                        }
                    }
                } else {
                    throw new Exception(command 
                    + ": cannot remove '" 
                    + file 
                    + "': No such file or directory");
                }
			}
        } catch (SecurityException e) {
            throw new Exception(command 
            + ": cannot remove file '" 
            + args[index] 
            + "': access denied");
        } catch (InvalidPathException e) {
			throw new Exception(command 
			+ ": cannot remove file '" 
			+ args[index] 
			+ "': illegal character in name");
	} 
    }

    private static void cputil(final File source, final File destination)
            throws Exception {
        String command = "cp";
        if (source.isDirectory()) {
            String absolutePath = destination.getAbsolutePath();
            String sourceName = source.getName();
            String filePath = absolutePath + File.separator + sourceName;
            File current = new File(filePath);
            if (current.exists()) {
                throw new Exception(command + ": " + current.getName() + " : directory already exists");
            } else {
                if (!current.mkdir()) {
                    throw new Exception(command 
                    + ": cannot create directory '" 
                    + current.getName());
                }
            }
            for (File c : source.listFiles()) {
                cputil(c, current);
            }
        } else {
            String absolutePath = destination.getAbsolutePath();
            String filePath = absolutePath + File.separator + source.getName();
            File newFile = new File(filePath);
            if (newFile.exists()) {
                throw new Exception(command + ": file already exists");
            } else {
                if (!newFile.createNewFile()) {
                    throw new Exception(command + ": cannot create a file");
                } else {
                    FileReader f1 = new FileReader(source);
                    FileWriter f2 = new FileWriter(newFile);
                    BufferedReader reader = new BufferedReader(f1);
                    BufferedWriter writer = new BufferedWriter(f2);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    reader.close();
                    writer.close();
                }
            }
        }
    }
    public static void cp(final String[] args) throws Exception {
        String command = "cp";
        boolean recursive;
        int index;
        String source;
        String destination;
        String [] idealCmd = {"cp", "-r", "source", "destination"};
        int suitAmount = idealCmd.length;
        if (args.length > suitAmount) {
            throw new Exception(command + ": too much arguments");
        } else {
            if (args.length == suitAmount) {
                if (args[1].equals("-r")) {
                    recursive = true;
                    source = args[2];
                    index = 2;
                    destination = args[index + 1];
                } else {
                    throw new Exception(command + ": too much arguments");
                }
            } else {
                if (args.length != suitAmount - 1) {
                    throw new Exception(command + ": missing operand");
                } else {
                    recursive = false;
                    source = args[1];
                    index = 1;
                    destination = args[index + 1];
                }
            }
        }
        try {
            File fileSource = new File(source);
            File fileDestination = new File(destination);
            if (!fileDestination.isAbsolute()) {
                String current = System.getProperty("user.dir");
                fileDestination = new File(current
                        + File.separator + destination);
            }
            if (!fileSource.isAbsolute()) {
                String current = System.getProperty("user.dir");
                fileSource = new File(current
                        + File.separator + source);
            }
            if (fileSource.isDirectory() && !recursive) {
                System.out.print("cp:" + source);
                System.out.println(" is a directory(not copied).");
                return;
            }
            if (!fileSource.exists()) {
                throw new Exception(command + ": " 
                + fileSource.getName()
                + " : No such file or directory");
            } else {
                if (!fileDestination.exists()) {
                    throw new Exception(command + ": " 
                    + fileDestination.getName() 
                    + ": No such file or directory");
                } else {
                    if (!fileDestination.isDirectory()) {
                        throw new Exception(command + ": " 
                        + fileDestination.getName() 
                        + " :is not a directory");
                    } else {
                        String srcAbsolute = fileSource.getAbsolutePath();
                        String destAbsolute = fileDestination.getAbsolutePath();
                        boolean isSubstr = destAbsolute.contains(srcAbsolute);
                        boolean srcDirectory = fileSource.isDirectory();
                        if (srcDirectory && isSubstr) {
                            System.out.print("cp: " + destination);
                            System.out.print(" is a inner directory of ");
                            System.out.println(source + "(not copied).");
                        } else {
                            cputil(fileSource, fileDestination);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception(command 
            + ": cannot read or write files");
        } catch (SecurityException e) {
            throw new Exception(command 
            + ": cannot copy file '" 
            + args[index] + "' to '" 
            + args[index  + 1] 
            + "': access denied");
        } catch (InvalidPathException e) {
			throw new Exception(command 
			+ ": cannot copy file '" 
			+ args[index] + "' to '" 
			+ args[index + 1] 
			+ "': illegal character in name");
		}
    }

    public static void mv(final String[] args) throws Exception {
        String command = "mv";
        String source;
        String destination;
        String [] idealCmd = {"mv", "source", "destination"};
        int suitAmount = idealCmd.length;
        if (args.length > suitAmount) {
            throw new Exception(command + ": too much arguments");
        } else {
            if (args.length == suitAmount) {
                source = args[1];
                destination = args[2];
            } else {
                throw new Exception(command + ": missing operand");
            }
        }
        try {
            File fileSource = new File(source);
            File fileDestination = new File(destination);
            if (!fileDestination.isAbsolute()) {
                String current = System.getProperty("user.dir");
                String filePath = current + File.separator + destination;
                fileDestination = new File(filePath);
            }
            if (!fileSource.isAbsolute()) {
                String current = System.getProperty("user.dir");
                String filePath = current + File.separator + source;
                fileSource = new File(filePath);
            }
            String srcParent = fileSource.getParent();
            String destParent = fileDestination.getParent();
            boolean parents = srcParent.equals(destParent);
            if (parents && !fileDestination.exists()) {
                fileSource.renameTo(fileDestination);
                return;
            }
            String sourceAbsolute = fileSource.getAbsolutePath();
            String destAbsolute = fileDestination.getAbsolutePath();
            String[] args1 = {"cp", sourceAbsolute, destAbsolute};
            String[] args2 = {"rm", sourceAbsolute};
            cp(args1);
            rm(args2);
        } catch (IOException e) {
            throw new Exception(command 
            + ": cannot read or write files");
        } catch (SecurityException e) {
            throw new Exception(command 
            + ": cannot move file '" 
            + args[1] + "' to '" 
            + args[2] + "': access denied");
        } catch (InvalidPathException e) {
			throw new Exception(command 
			+ ": cannot move file '" 
			+ args[1] + "' to '" 
			+ args[2] + "': illegal character in name");
		}
    }
    public static void ls(final String[] args) throws Exception {
        String command = "ls";
        if (args.length != 1) {
            throw new Exception(command + ": too much arguments");
        }
        try {
            File currentDirectory = new File(System.getProperty("user.dir"));
            for (String i : currentDirectory.list()) {
                System.out.println(i);
            }
        } catch (SecurityException e) {
            throw new Exception(command 
            + ": cannot get the list of files: access denied");
        }
    }

    public static void cat(final String[] args) throws Exception {
        String command = "cat";
        if (args.length != 2) {
            throw new Exception(command + ": too much arguments");
        }
        try {
            String file = args[1];
            File sourceFile = new File(file);
            if (!sourceFile.isAbsolute()) {
                String current = System.getProperty("user.dir");
                sourceFile = new File(current + File.separator + file);
            }
            if (!sourceFile.exists()) {
                throw new Exception(command + ": " 
                + file + ": No such file or directory");
            }
            if (sourceFile.isDirectory()) {
                System.out.println(sourceFile.getName());
                return;
            } else {
                try {
                    FileReader f = new FileReader(sourceFile);
                    BufferedReader reader = new BufferedReader(f);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    throw new Exception(command + ": '" 
                    + sourceFile.getName() + "': file not found");
                }
            }
        } catch (SecurityException e) {
            throw new Exception(command 
            + ": cannot open file '" 
            + args[1] + "': access denied");
        } catch (InvalidPathException e) {
            throw new Exception(command + ": cannot open file'" 
            + args[1] + "': illegal character in name");
        }
    }

    public static void exit(final String[] args) throws Exception {
        String command = "exit";
        if (args.length != 1) {
            throw new Exception(command + ": too much arguments");
        }
        System.exit(0);
    }
}
