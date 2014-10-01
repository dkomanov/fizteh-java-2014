package ru.fizteh.fivt.students.titov.shell;

import java.io.*;
import java.util.Scanner;

public class Shell {

    static File currentDir = new File(System.getProperty("user.dir"));

    static boolean cd(String directory) {
        try {
            File newDir = new File(directory);

            if (!newDir.isAbsolute()) {
                newDir = new File(currentDir.getPath() + File.separator + directory);
            }

            if (!newDir.isDirectory()) {
                System.out.println("cd: \'" + directory + "\': No such file or directory");
                return false;
            }

            currentDir = newDir;
            return true;
        } catch (Exception e) {
            System.err.println("cd: couldn't change current directory to \'" + directory + "\'.");
            return false;
        }
    }

    static boolean mkdir(String directory) {
        try {
            File newDir = new File(directory);

            if (!newDir.isAbsolute()) {
                newDir = new File(currentDir.getPath() + File.separator + directory);
            }

            if (newDir.exists()) {
                System.err.println("mkdir: \'" + directory + "\' is already exists!");
                return false;
            }

            if (!newDir.mkdir()) {
                throw new Exception();
            }

            return true;
        } catch (Exception e) {
            System.err.println("mkdir: couldn't create the directory \'" + directory + "\'.");
            return false;
        }
    }

    static boolean pwd() {
        try {
            System.out.println(currentDir.getCanonicalPath());
            return true;
        } catch (Exception e) {
            System.err.println("pwd: couldn't print the current work directory.");
            return false;
        }

    }

    static boolean remove(String filename) {
        try {
            File subject = new File(filename);
            if (!subject.isAbsolute()) {
                subject = new File(currentDir.getPath() + File.separator + filename);
            }

            if (!subject.exists()) {
                System.err.print("rm: cannot remove \'" + filename + "\': ");
                System.err.println("No such file or directory");
                return false;
            }

            if (subject.isDirectory()) {
                String[] entries = subject.list();
                if (entries == null) {
                    if (!subject.delete()) {
                        throw new Exception();
                    }
                    return true;
                }

                for (String entry : entries) {
                    File file = new File(entry);
                    remove(subject.getCanonicalPath() + File.separator + file.getName());
                }
            }

            if (!subject.delete()) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            System.err.print("rm: couldn't remove \'" + filename + "\'.");
            return false;
        }

    }

    static boolean copy(String file, String location) {
        try {
            File source = new File(file);
            if (!source.isAbsolute()) {
                source = new File(currentDir.getPath() + File.separator + file);
            }

            if (!source.exists()) {
                System.err.println("Cannot copy \'" + file + "\': No such file or directory");
                return false;
            }

            File destination = new File(location);
            if (!destination.isAbsolute()) {
                destination = new File(currentDir.getPath() + File.separator + location);
            }

            if (source.equals(destination)) {
                System.err.println("Error: cannot copy file to itself.");
                return false;
            }

            if (source.isDirectory()) {
                File newDestination = new File(destination + File.separator + source.getName());

                if (!destination.isDirectory()) {
                    if (!destination.mkdir()) {
                        System.err.println("Error: couldn't make a new directory.");
                        return false;
                    }
                    newDestination = destination;
                } else if (!newDestination.exists()) {
                    if (!newDestination.mkdir()) {
                        System.err.println("Error: couldn't make a new directory.");
                        return false;
                    }
                }

                String[] entries = source.list();
                for (String entry : entries) {
                    File newSource = new File(source + File.separator + entry);
                    if (!copy(newSource.toString(), newDestination.toString())) {
                        return false;
                    }
                }
            } else {
                FileInputStream input = null;
                FileOutputStream output = null;
                try {
                    input = new FileInputStream(source);

                    if (destination.isDirectory()) {
                        output = new FileOutputStream(destination + File.separator + source.getName());
                    } else {
                        output = new FileOutputStream(destination);
                    }

                    int count;
                    byte[] buf = new byte[16];
                    while (true) {
                        count = input.read(buf);
                        if (count < 0) {
                            break;
                        }
                        output.write(buf, 0, count);
                    }

                } catch (Exception e) {
                    System.err.println("A problem with reading from source file or writing to destination file");
                    return false;
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (Throwable e) {
                            System.err.println("Couldn't close the source file after reading!");
                            return false;
                        }
                    }
                    if (output != null) {
                        try {
                            output.close();
                        } catch (Throwable e) {
                            System.err.println("Couldn't close the destination file after writing!");
                            return false;
                        }
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("Couldn't copy the file.");
            return false;
        }

        return true;
    }

    static boolean move(String file, String location) {
        if (!copy(file, location) || !remove(file)) {
            return false;
        }

        return true;
    }

    static boolean ls() {
        try {
            String[] entries = currentDir.list();

            for (String entry : entries) {
                System.out.println(entry);
            }
        } catch (Exception e) {
            System.err.println("ls: couldn't execute the command.");
        }
        return true;
    }
    
    static boolean cat(String path) {
		try {
			File file = new File(currentDir.getPath() + File.separator + path); 
			Scanner sc = new Scanner(file); 
			
			while (sc.hasNextLine()) {
				System.out.println(sc.nextLine());
		    }
			sc.close();
		} catch(Exception e) {
			System.err.println("File \"" + path + "\" not found");
	        return false;
		}
		return true;   	
    }

    static boolean executeCommand(String command) {
        String[] tokens = command.split("[\\s]+");

        if (tokens == null || tokens.length == 0) {
            return true;
        }
        if (tokens.length == 1 && tokens[0].equals("")) {
            return true;
        }

        int i = tokens[0].equals("") ? 1 : 0;

        if (tokens[i].equals("cd")) {
            if (tokens.length == i + 2) {
                return cd(tokens[i + 1]);
            } else {
                System.err.println("Error: cd: wrong count of arguments");
                return false;
            }
        } else if (tokens[i].equals("pwd")) {
            if (tokens.length == i + 1) {
                return pwd();
            } else {
                System.err.println("Error: pwd: wrong count of arguments");
                return false;
            }
        } else if (tokens[i].equals("cp")) {
            if (tokens.length == i + 3) {
                return copy(tokens[i + 1], tokens[i + 2]);
            } else {
                System.err.println("Error: cp: wrong count of arguments");
                return false;
            }
        } else if (tokens[i].equals("mv")) {
            if (tokens.length == i + 3) {
                return move(tokens[i + 1], tokens[i + 2]);
            } else {
                System.err.println("Error: mv: wrong count of arguments");
                return false;
            }
        } else if (tokens[i].equals("rm")) {
            if (tokens.length == i + 2) {
                return remove(tokens[i + 1]);
            } else {
                System.err.println("Error: rm: wrong count of arguments");
                return false;
            }
        } else if (tokens[i].equals("ls")) {
            if (tokens.length == i + 1) {
                return ls();
            } else {
                System.err.println("Error: ls: wrong count of arguments");
                return false;
            }
        } else if (tokens[i].equals("cat")) {
            if (tokens.length == i + 2) {
                return cat(tokens[i + 1]);
            } else {
                System.err.println("Error: cat: wrong count of arguments");
                return false;
            }
        }else if (tokens[i].equals("mkdir")) {
            if (tokens.length == i + 2) {
                return mkdir(tokens[i + 1]);
            } else {
                System.err.println("Error: mkdir: wrong count of arguments");
                return false;
            }
        } else if (tokens[i].equals("exit")) {
            if (tokens.length == i + 1) {
                System.exit(0);
            } else {
                System.err.println("Error: exit: wrong count of arguments");
                return false;
            }
        } else {
            System.err.println("Unknown command!");
            return false;
        }

        return true;
    }

    static void interactive() {
        while (true) {
            try {
                System.out.print("$ ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String string = reader.readLine();

                String[] commands = string.split(";");
                if (commands == null) {
                    continue;
                }

                for (String command: commands) {
                    if (command.equals("")) {
                        continue;
                    }
                    if (!executeCommand(command)) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Fatal error. Program has been interrupted.");
                System.exit(1);
            }
        }
    }


    public static void main(String[] args) {

        if (args.length == 0) {
            interactive();
        } else {
            StringBuilder builder = new StringBuilder();
            for (String s : args) {
                builder.append(s).append(" ");
            }

            String string = new String(builder);
            String[] commands = string.split(";");
            for (String command : commands) {
                if (!executeCommand(command)) {
                    System.exit(1);
                }
            }
        }
    }

}
