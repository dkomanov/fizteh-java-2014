package ru.fizteh.fivt.students.andrey_reshetnikov.shell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class Shell {
	
	private static File currentDirectory;
	private static boolean interactiveMode;
	
	static void runningCommand(String[] parsedCommand) throws IOException, WrongCommand, ExitCommand {
		switch(parsedCommand[0]) {
			case "exit":
				 System.out.print("logout\n");
				 if (parsedCommand.length > 1) {
					 System.out.print("-bash: exit: ");
					 System.out.print(parsedCommand[1]);
					 System.out.println(": numeric argument required");
					 throw new WrongCommand();
				 }
				 System.out.println("\n[Процесс завершен]");
				 throw new ExitCommand();
			case "pwd":
				if (parsedCommand.length > 1) {
                    System.out.println("pwd: must't get parameter");
                    throw new WrongCommand();
                } else {
                	System.out.println(currentDirectory.getCanonicalPath());
                }
				break;
			case "mkdir":
				if (parsedCommand.length != 2) {
					System.out.println("usage: mkdir directory ...");
					throw new WrongCommand();
				}
				else {
					File newDir = WorkingWithFile.ConcatPath(currentDirectory, parsedCommand[1]);
					if (newDir.exists()) {
                       System.out.print("mkdir: " + parsedCommand[1] + ": File exists\n");
                       throw new WrongCommand();
					}
				 else if (!newDir.mkdirs()) {
					 	System.out.println("mkdir: " 
					 			+ newDir.getCanonicalPath() + ": can't create");
					 	throw new WrongCommand();
				 	  }
				}
				break;
			case "cd": 
				if (parsedCommand.length != 2) {
                     System.out.println("usage: cd ...");
                     throw new WrongCommand();
				}
				else {
					File newCurrentDirectory = WorkingWithFile.ConcatPath(currentDirectory,
                            parsedCommand[1]).getCanonicalFile();
					if (!newCurrentDirectory.exists()) {
                        System.out.println("cd: " + newCurrentDirectory 
                        		+ ": No such file or directory");
                        throw new WrongCommand();
					} else if (!newCurrentDirectory.isDirectory()) {
						 System.out.println("cd: " + newCurrentDirectory.getCanonicalPath() 
								 + " is't a directory");
						 throw new WrongCommand();
					} else {
						currentDirectory = newCurrentDirectory;
					}
				}
				break;
			case "rm":
				if (parsedCommand.length != 2) {
                    System.out.println("usage: rm ...");
                    throw new WrongCommand();
                }
				else {
					File elementToDelete = WorkingWithFile.ConcatPath(currentDirectory, parsedCommand[1]);
					if (elementToDelete.exists()) {
						try {
							WorkingWithFile.delete(elementToDelete);
						} catch (FileWasNotDeleted e) {
							System.out.println("rm: can't remove");
                            throw new WrongCommand();
						}
					} else {
                        System.out.println("rm: can't remove " + elementToDelete
                                + ": No such file or directory");
                        throw new WrongCommand();
                    }
				}
				break;
			case "cp":
				if (parsedCommand.length != 3) {
                    System.out.println("cp: need 2 parameters");
                    throw new WrongCommand();
                } 
				else {
					File source = WorkingWithFile.ConcatPath(currentDirectory, parsedCommand[1]);
                    File destination = WorkingWithFile.ConcatPath(currentDirectory, parsedCommand[2]);
                    if (source.equals(destination)) {
                        System.out.println("cp: source and destination are equal");
                        throw new WrongCommand();
                    } 
                    else if (!source.exists()) {
                        System.out.println("cp: source not exists");
                        throw new WrongCommand();
                    } else if (!destination.exists()) {
                    	System.out.println("cp: distanation not exists");
                    	throw new WrongCommand();
                    } else { 
                    	WorkingWithFile.copy(source, destination);
                    	//Files.copy(source.toPath(), Paths.get(destination.toPath().toString() + File.separator + source.getName()), REPLACE_EXISTING);            
                    }
				}
				break;
			case "mv":
				if (parsedCommand.length != 3) {
                    System.out.println("mv: need 2 parameters");
                    throw new WrongCommand();
				} 
				else {
					File source = WorkingWithFile.ConcatPath(currentDirectory, parsedCommand[1]);
                    File destination = WorkingWithFile.ConcatPath(currentDirectory, parsedCommand[2]);
                    if (source.exists()) {
                        if (!destination.exists()) {                    
                                System.out.println("rm: destination doesn't exists");
                                throw new WrongCommand();                            
                        } else {
                            WorkingWithFile.copy(source, destination);
                            try {
                                WorkingWithFile.delete(source);
                            } catch (FileWasNotDeleted e) {
                                System.out.println("rm: cannot remove");
                                throw new WrongCommand();
                            }
                        }
                    }
				}
				break;
			case "ls":
				if (parsedCommand.length > 1) {
					System.out.println("ls hasn't any parameters");
					throw new WrongCommand();
				}
				else {
					File[] listOfElements = currentDirectory.listFiles();
			        if (listOfElements != null) {
			            for (File i : listOfElements) {			               
			            	System.out.println(i.getName());
			            }
			        }
				}
				break;
			case "cat":
				if (parsedCommand.length != 2) {
					System.out.println("cat has 1 parametr");
					throw new WrongCommand();
				}
				else {
					File f = WorkingWithFile.ConcatPath(currentDirectory, parsedCommand[1]);
					BufferedReader fin = new BufferedReader(new FileReader(f));
					String line;
					while ((line = fin.readLine()) != null) {
						System.out.println(line);
					}
				}
				break;
			case "":
				throw new WrongCommand();
		}
	}
	
	public static void main(String[] args) throws IOException, WrongCommand, ExitCommand {
		new Shell().run(args);
	}
	
	public void run(String[] args) throws IOException, WrongCommand, ExitCommand {
		interactiveMode = (args.length == 0);
		currentDirectory = new File(".").getCanonicalFile();
		Input shellInput = null;
		
			if (interactiveMode) {
				shellInput = new InteractiveInput();
			}
			else {
				shellInput = new PackageInput(args);
			}
			while (shellInput.isNext()) {
				try {
					runningCommand(shellInput.nextCommand());
				} catch (WrongCommand e) {
					System.exit(1);
					e.printStackTrace();
				} catch (ExitCommand | CommandsIsEmpty e) {
					System.exit(0);
					e.printStackTrace();
				}
			}
	}
	
	public interface StringParser {
	    String[] parse(String string);
	}
	public class Parser implements StringParser{
		public String[] parse(String string) {
			return string.split("\\s+");
		}
	}	
}
