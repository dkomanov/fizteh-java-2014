package ru.fizteh.fivt.students.torunova.shell;
/**
 * Created by nastya on 13.09.14.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * ru.fizteh.fivt.students.torunova.shell.Shell Class.
 */
final class Shell {
    private static final boolean FUNCTION_ERROR = false;
    private static final boolean FUNCTION_SUCCESS = true;

    /**
     * just stupid constructor.
     */
    private Shell() {
    }
    /**
     * main method.
     *
     * @param args - arguments for program.
     */
    public static void main(final String[] args) {
        if (args.length > 0) {
            int status = 0;
            String[] funcs = parseCommandsFromArray(args);
            for (int i = 0; i < funcs.length; i++) {
                if (!run(funcs[i])) {
                    status = 1;
                    System.exit(status);
                }
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            String newcommands = new String();
            String[] newfuncs;
            while (true) {
                System.out.print("$ ");
                try {
                    newcommands = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                }
                newfuncs = newcommands.split(";");
                for (int i = 0; i < newfuncs.length; i++) {
                    run(newfuncs[i]);
                }
            }
        }
    }
    /**
     * parses commands,given in array.
     *
     * @param commands - array of commands and their arguments, which are split by whitespace.
     * @return - array of commands with their arguments, which are now correctly split by semicolon.
     */
    private static String[] parseCommandsFromArray(final String[] commands) {
        StringBuilder b = new StringBuilder();
        int length = commands.length;
        for (int i = 0; i < length; i++) {
            b.append(commands[i]).append(" ");
        }
        String functions = b.toString();
        String[] funcs = functions.split(";");
        return funcs;
    }
    /**
     * method,which runs all functions.
     *
     * @param func
     */
    private static boolean run(String func) {
        func = func.trim();
        if (Pattern.matches("cd\\s+.*", func)) {
            String dir = func.substring(2);
            dir = dir.trim();
			String[]args = dir.split("\\s+");
			if (args.length > 1) {
				System.err.println("cd: too many arguments.");
				return FUNCTION_ERROR;
			}
            return cd(dir);
        }  else if (func.equals("cd")) {
			System.err.println("cd: too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("ls\\s+.*", func)) {
			System.err.println("ls: too many arguments.");
			return FUNCTION_ERROR;
		} else if (func.equals("ls")) {
            return ls();
        } else if (Pattern.matches("mkdir\\s+.*", func)) {
            String dir = func.substring(5);
            dir = dir.trim();
			String[] args = dir.split("\\s+");
			if (args.length > 1) {
				System.err.println("mkdir: too many arguments.");
				return FUNCTION_ERROR;
			}
            return mkdir(dir);
        } else if (func.equals("mkdir")) {
			System.err.println("mkdir: too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("pwd\\s+.*", func)) {
			System.err.println("pwd: too many arguments.");
			return FUNCTION_ERROR;
		} else if (func.equals("pwd")) {
            return pwd();
        } else if (Pattern.matches("cp\\s+.*", func) && !Pattern.matches("cp\\s+-r\\s+.*", func)) {
            String args = func.substring(2);
            args = args.trim();
            String[] arrgs = args.split("\\s+");
			if (arrgs.length > 2) {
				System.err.println("cp: too many arguments.");
				return FUNCTION_ERROR;
			} else if (arrgs.length < 2) {
				System.err.println("cp: too few arguments.");
				return FUNCTION_ERROR;
			}
            return copy(arrgs[0], arrgs[1]);
        } else if (func.equals("cp")) {
			System.err.println("cp: too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("cp\\s+-r\\s+.*", func)) {
            String args = func.substring(2);
            args = args.trim();
            args = args.substring(2);
            args = args.trim();
            String[] arrgs = args.split("\\s+");
			if (arrgs.length > 2) {
				System.err.println("cp -r: too many arguments.");
				return FUNCTION_ERROR;
			} else if (arrgs.length < 2) {
				System.err.println("cp -r: too few arguments.");
				return FUNCTION_ERROR;
			}
            return copyRecursive(arrgs[0], arrgs[1]);
        } else if (Pattern.matches("cp\\s+-r", func)) {
			System.err.println("cp -r: too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("rm\\s+.*", func) && !Pattern.matches("rm\\s+-r\\s+.*", func)) {
            String arg = func.substring(2);
            arg = arg.trim();
			String[] args = arg.split("\\s+");
			if (args.length > 1) {
				System.err.println("rm: too many arguments.");
				return FUNCTION_ERROR;
			}
            return remove(arg);
        } else if (Pattern.matches("rm\\s+-r\\s+.*", func)) {
            String arg = func.substring(2);
            arg = arg.trim();
            arg = arg.substring(2);
            arg = arg.trim();
			String[] args = arg.split("\\s+");
			if (args.length > 1) {
				System.err.println("rm -r: too many arguments.");
				return FUNCTION_ERROR;
			}
            return removeRecursive(arg);
        } else if (func.equals("rm")) {
			System.err.println("rm : too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("rm\\s+-r", func)) {
			System.err.println("rm -r: too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("cat\\s+.*", func)) {
            String file = func.substring(3);
            file = file.trim();
			String[] args = file.split("\\s+");
			if (args.length > 1) {
				System.err.println("cat: too many arguments.");
				return FUNCTION_ERROR;
			}
            return cat(file);
        } else if (func.equals("cat")) {
			System.err.println("cat: too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("mv\\s+.*", func)) {
            String arg = func.substring(2);
            arg = arg.trim();
            String[] args = arg.split("\\s+");
			if (args.length > 2) {
				System.err.println("mv: too many arguments.");
				return FUNCTION_ERROR;
			} else if (args.length < 2) {
				System.err.println("mv: too few arguments.");
				return FUNCTION_ERROR;
			}
            return move(args[0], args[1]);
        } else if (func.equals("mv")) {
			System.err.println("mv: too few arguments.");
			return FUNCTION_ERROR;
		} else if (Pattern.matches("exit.+", func)) {
			System.err.println("exit: too many arguments.");
			return FUNCTION_ERROR;
		} else if (func.equals("exit")) {
            System.exit(0);
        } else if (!func.equals("")) {
            System.err.println(func + ":command not found");
            return FUNCTION_ERROR;
        }
        return FUNCTION_SUCCESS;
    }
    /**
     * copy for directory.
     * @param sourceDir -source directory.
     * @param destDir   - destination directory.
     */
    private static boolean copyRecursive(final String sourceDir, final String destDir) {
        File source = new File(sourceDir).getAbsoluteFile();
        File dest = new File(destDir).getAbsoluteFile();
        if (!source.isDirectory() || !dest.isDirectory()) {
            System.err.println("cp -r: source or destination is not a directory.");
            return FUNCTION_ERROR;
        }
        File[] listOfFiles = source.listFiles();
        dest = new File(dest.getAbsolutePath(), source.getName()).getAbsoluteFile();
        if (!dest.mkdirs()) {
			System.out.println("cp -r: directory " + sourceDir
					+ "cannot be copied");
			return FUNCTION_ERROR;
		}
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                File newDestDir = new File(dest.getAbsolutePath(), listOfFiles[i].getName());
                if (!newDestDir.mkdirs()) {
					System.out.println("cp -r: directory " + newDestDir.getName()
							+ "cannot be copied");
					return FUNCTION_ERROR;
				}
                if (!copyRecursive(listOfFiles[i].getAbsolutePath(), newDestDir.getAbsolutePath())) {
					return FUNCTION_ERROR;
				}
            } else {
                if (!copy(listOfFiles[i].getAbsolutePath(), destDir)) {
					return FUNCTION_ERROR;
				}
            }
        }
        return FUNCTION_SUCCESS;
    }
    /**
     * move file or directory.
     *
     * @param source - source.
     * @param dest   - destination.
     */
    private static boolean move(final String source, final String dest) {
        File src = new File(source).getAbsoluteFile();
        File dst = new File(dest).getAbsoluteFile();
        if (dst.isDirectory()) {
            if (src.isFile()) {
                copy(src.getAbsolutePath(), dst.getAbsolutePath());
                remove(src.getAbsolutePath());
            } else if (src.isDirectory()) {
                copyRecursive(src.getAbsolutePath(), dst.getAbsolutePath());
                removeRecursive(src.getAbsolutePath());
            }
        } else if (dst.isFile()) {
            if (src.isDirectory()) {
                System.err.println("mv: "
                        + source + "is directory and" + dest + " is regular file.");
                return FUNCTION_ERROR;
            } else if (src.isFile()) {
                if (src.getParent().equals(dst.getParent())) {
                    if (!src.renameTo(dst)) {
						if (dst.exists()) {
							System.err.println("mv: cannot rename "
									+ source + "to"
									+ dest + ",file already exists.");
						} else {
							System.err.println("mv: cannot rename "
									+ source + "to"
									+ dest + " for some unknown reason.");
						}
                        return FUNCTION_ERROR;
                    }
                } else {
                    copy(source, dst.getParent());
                    remove(source);
                    File newCopy = new File(dst.getParent(), source);
                    if (!newCopy.renameTo(dst)) {
						if (dst.exists()) {
							System.err.println("mv: cannot rename "
									+ source + "to"
									+ dest + ",file already exists.");
						} else {
							System.err.println("mv: cannot rename "
									+ source + "to"
									+ dest + " for some unknown reason.");
						}
                        return FUNCTION_ERROR;
                    }
                }
            }
        } else if (!dst.exists()) {
            try {
                dst.createNewFile();
            } catch (IOException e) {
                System.err.println("mv: caught IOException " + e.getMessage());
                return FUNCTION_ERROR;
            }
            if (src.getParent().equals(dst.getParent())) {
                if (!src.renameTo(dst)) {
					if (dst.exists()) {
                    System.err.println("mv: cannot rename "
                            + source + "to" + dest + ",file already exists.");
					} else {
						System.err.println("mv: cannot rename "
								+ source + "to" + dest + " for some unknown reason.");
					}
                    return FUNCTION_ERROR;
                }
            } else {
                copy(source, dst.getParent());
                remove(source);
                File newCopy = new File(dst.getParent(), source);
                if (!newCopy.renameTo(dst)) {
					if (dst.exists()) {
                    System.err.println("mv: cannot rename "
                            + source + "to" + dest + ",file already exists.");
					} else {
						System.err.println("mv: cannot rename "
								+ source + "to" + dest + " for some unknown reason.");
					}
                    return FUNCTION_ERROR;
                }
            }
        }
        return FUNCTION_SUCCESS;
    }
    /**
     * print file content.
     *
     * @param file - file.
     */
    private static boolean cat(final String file) {
        File f = new File(file).getAbsoluteFile();
        if (f.exists()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(f);
            } catch (FileNotFoundException e) {
                System.err.println("cat: caught FileNotFoundException " + e.getMessage());
                return FUNCTION_ERROR;
            }
            String nextString = new String();
            while (scanner.hasNext()) {
                nextString = scanner.nextLine();
                System.out.println(nextString);
            }
        } else {
            System.err.println("cat: " + f.getName() + ": No such file or directory.");
            return FUNCTION_ERROR;
        }
        return FUNCTION_SUCCESS;
    }
    /**
     * remove for regular file.
     *
     * @param file - file.
     */
    private static boolean remove(final String file) {
        File f = new File(file).getAbsoluteFile();
        if (f.isFile()) {
           if (!f.delete()) {
			   System.err.println("rm: file " + f.getName()
					   + " cannot be removed for some unknown reason.");
			   return FUNCTION_ERROR;
		   }
        } else if (f.isDirectory()) {
            System.out.println("rm: " + f.getName() + " : is a directory.");
            return FUNCTION_ERROR;
        } else if (!f.exists()) {
			System.out.println("rm: cannot remove " + f.getName() + " : No such file or directory");
			return FUNCTION_ERROR;
		}
        return FUNCTION_SUCCESS;
    }
    /**
     * remove for directory.
     *
     * @param dir - directory.
     */
    private static boolean removeRecursive(final String dir) {
        File dIr = new File(dir).getAbsoluteFile();
        if (dIr.isDirectory()) {
            if (dir.equals(System.getProperty("user.dir"))) {
                System.setProperty("user.dir", dIr.getParent());
            }
            File[] content = dIr.listFiles();
            for (int i = 0; i < content.length; i++) {
                if (content[i].isDirectory()) {
                    if (!removeRecursive(content[i].getAbsolutePath())) {
						return FUNCTION_ERROR;
					}
                } else {
                    if (!remove(content[i].getAbsolutePath())) {
						return FUNCTION_ERROR;
					}
                }
            }
           if (!dIr.delete()) {
			  System.err.println("rm -r: directory " + dIr.getName()
					  + "cannot be deleted for some unknown reason.");
			   return FUNCTION_ERROR;
		   }
        } else {
            System.out.println("rm -r: " + dir + " is not a directory");
            return FUNCTION_ERROR;
        }
        return FUNCTION_SUCCESS;
    }
    /**
     * copy for regular file.
     *
     * @param file - file.
     * @param dir  - directory.
     */
    private static boolean copy(final String file, final String dir) {
        File oldFile;
        File newFile;
        File dIr = new File(dir).getAbsoluteFile();
        if (dIr.isDirectory()) {
            String[] folders = file.split(File.separator);
            oldFile = new File(file).getAbsoluteFile();
            newFile = new File(dir, folders[folders.length - 1]).getAbsoluteFile();
        } else {
            oldFile = new File(file).getAbsoluteFile();
            newFile = new File(dir).getAbsoluteFile();
        }
		if (oldFile.getAbsolutePath().equals(newFile.getAbsolutePath())) {
			System.out.println("cp:"
					+ oldFile.getName()
					+ " and " + newFile.getName() + " are the same file.");
			return FUNCTION_ERROR;
		}
		if (oldFile.isDirectory()) {
			System.err.println("cp: " + oldFile.getAbsolutePath() + " is a directory (not copied).");
		}
        try {
            Files.copy(oldFile.toPath(), newFile.toPath(),
					StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
			System.err.println("cp: Caught IOException " + e.getMessage());
			return FUNCTION_ERROR;
		}
        return FUNCTION_SUCCESS;
    }
    /**
     * print working directory.
     */
    private static boolean pwd() {
        String currentdir = System.getProperty("user.dir");
        System.out.println(currentdir);
        return FUNCTION_SUCCESS;
    }
    /**
     * make directory.
     *
     * @param dir - directory.
     */
    private static boolean mkdir(final String dir) {
        File newDir = new File(dir).getAbsoluteFile();
        if (!newDir.mkdirs()) {
			if (newDir.isDirectory()) {
            	System.err.println("mkdir: directory " + newDir.getAbsolutePath() + " already exists.");
			} else if (newDir.isFile()) {
				System.err.println("mkdir: file " + newDir.getAbsolutePath() + " already exists.");
			} else {
				System.err.println("mkdir: directory " + newDir.getAbsolutePath()
						+ " cannot be created for some unknown reason.");
			}
            return FUNCTION_ERROR;
        }
        return FUNCTION_SUCCESS;
    }
    /**
     * list contents of current directory.
     */
    private static boolean ls() {
        File currentDir = new File(System.getProperty("user.dir"));
        File[] contents = currentDir.listFiles();
        for (int i = 0; i < contents.length; i++) {
            System.out.println(contents[i].getName());
        }
        return FUNCTION_SUCCESS;
    }
    /**
     * change directory.
     *
     * @param dir - new directory.
     */
    private static boolean cd(final String dir) {
        File newWorkingDir = new File(dir);
        if (newWorkingDir.getAbsoluteFile().isDirectory()) {
            String canonicalPath = new String();
            try {
                canonicalPath = newWorkingDir.getCanonicalPath();
            } catch (IOException e) {
                System.err.println("cp: Caught IOException: " + e.getMessage());
                return FUNCTION_ERROR;
            }
            System.setProperty("user.dir", canonicalPath);
        } else {
            System.err.println("cd: " + newWorkingDir.getAbsolutePath() + ": No such file or directory.");
            return FUNCTION_ERROR;
        }
        return FUNCTION_SUCCESS;
    }
}
