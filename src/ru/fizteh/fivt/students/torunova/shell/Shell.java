package ru.fizteh.fivt.students.torunova.shell;
/**
 * Created by nastya on 13.09.14.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 * ru.fizteh.fivt.students.torunova.shell.Shell Class.
 */
final class Shell {
	/**
	 * just stupid constructor.
	 */
	private Shell() {
	}
	/**
	 * theVeryBeginningOfThePath.
	 */
	private static final int THE_VERY_BEGINNING_OF_THE_PATH = 3;
	/**
	 * THE_BEGINNING_OF_FILENAME.
	 */
	private static final int THE_BEGINNING_OF_FILENAME = 4;
	/**
	 * another_const_int,because checkstyle thinks that some of numbers are magic.
	 */
	private static final int THE_BEGINNING_OF_ARGS = 6;
	/** main method.
	 * @param args - arguments for program.
	 */
	public static void main(final String[] args) {
		if (args.length > 0) {
			String[] funcs = parseCommandsFromArray(args);
			for (int i = 0; i < funcs.length; i++)
				run(funcs[i]);
		} else {
			Scanner scanner = new Scanner(System.in);
			String newcommands = new String();
			String[] newfuncs;
			while (true) {
				System.out.println("$ ");
				newcommands = scanner.nextLine();
				newfuncs = newcommands.split(";");
				for (int i = 0; i < newfuncs.length; i++)
					run(newfuncs[i]);
			}
		}
	}
	/**
	 * parses commands,given in array.
	 * @param commands - array of commands and their arguments, which are split by whitespace.
	 * @return  - array of commands with their arguments, which are now correctly split by semicolon.
	 */
	private static String[] parseCommandsFromArray(final String[] commands) {
		StringBuilder b = new StringBuilder();
		int length = commands.length;
		for (int i = 0; i < length; i++)
			b.append(commands[i]).append(" ");
		String functions = b.toString();
		String[] funcs = functions.split(";");
		return funcs;
	}
	/**
	 * method,which runs all functions.
	 * @param func
	 */
	private static void run(final String func) {
		if (Pattern.matches("cd .*", func)) {
			String dir = func.substring(THE_VERY_BEGINNING_OF_THE_PATH);
			cd(dir);
		} else if (func.equals("ls"))
			ls();
		else if (Pattern.matches("mkdir .*", func)){
			String dir = func.substring(6);
			mkdir(dir);
		} else if (func.equals("pwd"))
			pwd();
		else if (Pattern.matches("cp .*", func) && !Pattern.matches("cp -r .*", func)) {
			String args = func.substring(THE_VERY_BEGINNING_OF_THE_PATH);
			String[] arrgs = args.split(" ");
			copy(arrgs[0], arrgs[1]);
		} else if (Pattern.matches("cp -r .*", func)) {
			String args = func.substring(THE_BEGINNING_OF_ARGS);
			String[] arrgs = args.split(" ");
			copyRecursive(arrgs[0], arrgs[1]);

		} else if (Pattern.matches("rm .*", func) && !Pattern.matches("rm -r .*", func)){
			String arg = func.substring(THE_BEGINNING_OF_FILENAME);
			remove(arg);
		} else if (Pattern.matches("rm -r .*", func)) {
			String arg = func.substring(THE_BEGINNING_OF_ARGS);
			removeRecursive(arg);
		} else if (Pattern.matches("cat .*", func)) {
			String file = func.substring(THE_BEGINNING_OF_FILENAME);
			cat(file);
		} else if (Pattern.matches("mv .*", func)) {
			String[] args = func.substring(THE_VERY_BEGINNING_OF_THE_PATH).split(" ");
			move(args[0], args[1]);
		} else if (Pattern.matches("exit", func))
			System.exit(0);
		else if (func.equals(""));
		else System.err.println("I don't know this function\n");
	}
	/**
	 *copy for directory.
	 * @param sourceDir -source directory.
	 * @param destDir - destination directory.
	 */
	private static void copyRecursive(final String sourceDir, final String destDir) {
		File source = new File(sourceDir);
		File dest = new File(destDir);
		if (!source.isDirectory() || !dest.isDirectory())
			System.err.println("Source or destination is not a directory");
		File[]listOfFiles = source.listFiles();
		dest = new  File(dest.getAbsolutePath(), source.getName());
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				File newDestDir = new File(dest.getAbsolutePath(), listOfFiles[i].getName());
				newDestDir.mkdirs();
				copyRecursive(listOfFiles[i].getAbsolutePath(), newDestDir.getAbsolutePath());
			} else copy(listOfFiles[i].getAbsolutePath(), destDir);
		}
	}
	/**
	 * move file or directory.
	 * @param source - source.
	 * @param dest - destination.
	 */
	private static void move(final String source, final String dest) {
		File src;
		File dst;
		if (!Pattern.matches(File.separator + ".*", source))
			src = new File(System.getProperty("user.dir"), source);
		else
			src = new File(source);
		if (!Pattern.matches(File.separator + ".*", dest))
			dst = new File(System.getProperty("user.dir"), dest);
		else
			dst = new File(dest);
		if (dst.isDirectory()) {
			if (src.isFile()) {
				copy(src.getAbsolutePath(), dst.getAbsolutePath());
				remove(src.getAbsolutePath());
			} else if(src.isDirectory()) {
				copyRecursive(src.getAbsolutePath(), dst.getAbsolutePath());
				removeRecursive(src.getAbsolutePath());
			}
		} else if (dst.isFile()) {
			if(src.isDirectory())
				System.err.println("mv error: " + source + "is directory and" + dest + " is regular file");
			else if (src.isFile()) {
				if (src.getParent().equals(dst.getParent())) {
					if (!src.renameTo(dst))
						System.err.println("mv error: cannot rename " + source + "to" + dest + ",file already exists");
				} else {
					copy(source, dst.getParent());
					remove(source);
					File newCopy = new File(dst.getParent(), source);
					if (!newCopy.renameTo(dst))
						System.err.println("mv error: cannot rename " + source + "to" + dest + ",file already exists");
				}
			}
		}
	}
	/**
	 * print file content.
	 * @param file - file.
	 */
	private static void cat(final String file) {
		File f;
		if (!Pattern.matches(File.separator + ".*", file))
			f = new File(System.getProperty("user.dir"), file);
		else
			f = new File(file);
		if(f.exists()) {
			Scanner scanner;
			try {
				scanner = new Scanner(f);
			} catch (FileNotFoundException e){
				System.err.println("Caught FileNotFoundException " + e.getMessage());
				System.exit(1);
				return;
			}
			String nextString = new String();
			while (scanner.hasNext()) {
				nextString = scanner.nextLine();
				System.out.println(nextString);
			}
		} else
			System.err.println("No such file");
	}
	/**
	 *remove for regular file.
	 * @param file - file.
	 */
	private static void remove(final String file)  {
		File f;
		if (!Pattern.matches(File.separator + ".*", file))
			f = new File(System.getProperty("user.dir"), file);
		else
			f = new File(file);
		if(f.isFile())
			f.delete();
		else System.out.println("Can't remove " + f.getName() + "No such file or directory");
	}
	/**
	 *remove for directory.
	 * @param dir - directory.
	 */
	private static void removeRecursive(final String dir) {
		File dIr;
		if (!Pattern.matches(File.separator + ".*", dir))
			dIr = new File(System.getProperty("user.dir"), dir);
		else
			dIr = new File(dir);
		if (dIr.isDirectory()) {
			if (dir.equals(System.getProperty("user.dir")))
				System.setProperty("user.dir", dIr.getParent());
			File[] content = dIr.listFiles();
			for (int i = 0; i < content.length; i++) {
				if(content[i].isDirectory())
					removeRecursive(content[i].getAbsolutePath());
				else
					remove(content[i].getAbsolutePath());
			}
			dIr.delete();
		} else System.out.println(dir + " is not a directory");
	}
	/**
	 *copy for regular file.
	 * @param file - file.
	 * @param dir - directory.
	 */
	private static void copy(final String file, final String dir) {
		File oldFile;
		File newFile;
		if (Pattern.matches(File.separator + ".*", file)) {
			String[]folders = file.split(File.separator);
			oldFile = new File(file);
			newFile = new File(dir, folders[folders.length - 1]);
		} else {
			oldFile = new File(System.getProperty("user.dir"), file);
			newFile = new File(dir, file);
		}
		try {
			Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
		} catch(IOException e) {
			System.err.println("Caught IOException " + e.getMessage());
		}
	}
	/**
	 * print working directory.
	 */
	private static void pwd() {
		String currentdir = System.getProperty("user.dir");
		System.out.println(currentdir);
	}
	/**
	 * make directory.
	 * @param dir - directory.
	 */
	private static void mkdir(final String dir) {
		File newDir;
		if (!Pattern.matches(File.separator + ".*", dir))
			newDir = new File(System.getProperty("user.dir"), dir);
		else
			newDir = new File(dir);
		if (!newDir.mkdirs())
			System.err.println("Can't make this directory or it already exists");
	}
	/**
	 * list contents of current directory.
	 */
	private static void ls() {
		File currentDir = new File(System.getProperty("user.dir"));
		File[] contents = currentDir.listFiles();
		for (int i = 0; i < contents.length; i++) {
			System.out.println(contents[i].getName() + "\n");
		}
	}
	/**
	 * change directory.
	 * @param dir - new directory.
	 */
	private static void cd(final String dir)  {
		File newWorkingDir = new File(dir);
		if (Pattern.matches(File.separator + ".*", dir) && newWorkingDir.isDirectory()) {
			String canonicalPath = new String();
			try {
				canonicalPath = newWorkingDir.getCanonicalPath();
			} catch (IOException e){
				System.err.println("Caught IOException: " + e.getMessage());
			}
			System.setProperty("user.dir", canonicalPath);
		} else {
			File newWorkingDir2 = new File(System.getProperty("user.dir"), dir);
			if (newWorkingDir2.isDirectory()) {
				String canonicalPath = new String();
				try {
					canonicalPath = newWorkingDir2.getCanonicalPath();
				} catch (IOException e) {
					System.err.println("Caught IOException: " + e.getMessage());
				}
				System.setProperty("user.dir", canonicalPath);
			} else System.err.println("Directory does not exist");
		}
	}
}
