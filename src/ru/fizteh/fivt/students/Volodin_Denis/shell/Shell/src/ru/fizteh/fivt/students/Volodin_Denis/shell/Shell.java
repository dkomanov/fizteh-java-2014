package ru.fizteh.fivt.students.Volodin_Denis.shell;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Shell
{
	static final int SUCCESS = 0;
	static final int ERROR = 1;
	
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			//interactive mode
			
			Scanner scanner = new Scanner(System.in);
			
			do
			{			
				try
				{
					System.out.print("$ ");
					String[] shell_in = scanner.nextLine().split(";");
					for (int i = 0; i < shell_in.length; ++i)
					{
						if (shell_in[i].length() > 0)
						{
							String[] buffer = shell_in[i].trim().split("\\s+");
							try
							{
								shell_parser(buffer);
							}
							catch (Exception except)
							{
								System.err.println("$ " + except.getMessage());
							}
						}
					}
				}
				catch (Exception except)
				{	
					System.err.println("$ " + except.getMessage());
				}
			} while (true);
			
		}
		else
		{
			try
			{
				String[] shell_in = args.toString().split(";");
				for (int i = 0; i < shell_in.length; ++i)
				{
					if (shell_in[i].length() > 0)
					{
						String[] buffer = shell_in[i].trim().split("\\s+");
						try
						{
							shell_parser(buffer);
						}
						catch (Exception except)
						{
							System.err.println("$ " + except.getMessage());
						}
					}
				}
			}
			catch (Exception except)
			{
				System.err.println("$ " + except.getMessage());
			}
		}
    }
	
	//
	// basic functions
	//
	// begin
	//
	
	private static void shell_cd(String[] args) throws Exception
	{
		if (args.length != 2)
		{
			shell_wrongQuantity("cd");
		}
		if (args[1].isEmpty() == true)
		{
			shell_noName("cd");
		}
		
		try
		{
			Path pathToFile = Paths.get(args[1]).normalize();
			if (pathToFile.isAbsolute() == false)
			{
				pathToFile = Paths.get(System.getProperty("user.dir"), pathToFile.toString()).normalize();
			}
			
			if (pathToFile.toFile().exists() == true)
			{
				if (pathToFile.toFile().isDirectory() == true)
				{
					System.setProperty("user.dir", pathToFile.toString());
				}
				else
				{
					shell_notDirectory("cd", args[1]);
				}
			}
			else
			{
				shell_notExist("cd", args[1]);
			}
		}
		catch (InvalidPathException invExcept)
		{
			shell_invalidName("cd", args[1]);
		}
		catch (SecurityException secExcept)
		{
			shell_security("cd", args[1]);
		}
	}
	
	private static void shell_mkdir(String[] args) throws Exception
	{
		if (args.length != 2)
		{
			shell_wrongQuantity("mkdir");
		}
		if (args[1].isEmpty() == true)
		{
			shell_noName("mkdir");
		}
		
		try
		{
			Path pathToFile = Paths.get(args[1]).normalize();
			if (pathToFile.isAbsolute() == false)
			{
				pathToFile = Paths.get(System.getProperty("user.dir"), pathToFile.toString()).normalize();
			}
			
			if (pathToFile.toFile().exists() == true)
			{
				shell_alreadyExist("mkdir", args[1]);
			}
			else
			{
				if (pathToFile.toFile().mkdir() == false)
				{
					shell_notMkdir("mkdir", args[1]);
				}
			}
		}
		catch (InvalidPathException invExcept)
		{
			shell_invalidName("mkdir", args[1]);
		}
		catch (SecurityException secExcept)
		{
			shell_security("mkdir", args[1]); 
		}
	}

	private static void shell_pwd(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			shell_wrongQuantity("pwd");
		}
		
		try
		{
			Path pathToFile = Paths.get(System.getProperty("user.dir")).normalize();
			System.out.println(pathToFile.toString());
		}
		catch (SecurityException secExcept)
		{
			shell_security("pwd", "current directory");
		}
	}
	
	private static void shell_rm(String[] args) throws Exception
	{
		if ((args.length != 2) && (args.length != 3))
		{
			shell_wrongQuantity("rm");
		}
		if (args[args.length - 1].isEmpty() == true)
		{
			shell_noName("rm");
		}
		
		if (args.length == 3)
		{
			if (args[1].equals("-r") == false)
			{
				shell_wrongKey("rm", "-r");
			}
			try
			{
				Path pathToFile = Paths.get(args[2]).normalize();
				if (pathToFile.isAbsolute() == false)
				{
					pathToFile = Paths.get(System.getProperty("user.dir"), args[2]).normalize();
				}
				if (pathToFile.toFile().exists() == false)
				{
					shell_notExist("rm", args[2]);
				}
				if (pathToFile.toFile().isDirectory() == false)
				{
					shell_notDirectory("rm", args[2]);
				}
				
				String[] names = pathToFile.toFile().list();
				
				if (names.length != 0)
				{
					for (int i = 0; i < names.length; ++i)
					{
						if (Paths.get(pathToFile.toString(), names[i]).normalize().toFile().isDirectory() == true)
						{
							System.setProperty("user.dir", pathToFile.toString());
							String[] helpArray = new String[] {"rm", "-r", names[i]};
							shell_rm(helpArray);
							System.setProperty("user.dir", pathToFile.getParent().toString());
						}
						if (Paths.get(pathToFile.toString(), names[i]).normalize().toFile().isFile() == true)
						{
							String[] helpArray = new String[] {"rm", Paths.get(pathToFile.toString(), names[i]).normalize().toString()};
							shell_rm(helpArray);
						}
					}
				}
				if (pathToFile.toFile().delete() == false)
				{
					shell_smthWrong("rm");
				}
				
			}
			catch (InvalidPathException invExcept)
			{
				shell_invalidName("rm", args[2]);
			}
			catch (SecurityException secExcept)
			{
				shell_security("rm", args[2]);
			}
		}
		else
		{
			try
			{
				Path pathToFile = Paths.get(args[1]).normalize();

				if (pathToFile.isAbsolute() == false)
				{				
					pathToFile = Paths.get(System.getProperty("user.dir"), args[1]).normalize();
				}				
				if (pathToFile.toFile().exists() == false)
				{
					shell_notExist("rm", args[1]);
				}
				if (pathToFile.toFile().isFile() == false)
				{
					shell_notFile("rm", args[1]);
				}
				if (pathToFile.toFile().delete() == false)
				{
					shell_smthWrong("rm");
				}
			}
			catch (InvalidPathException invExcept)
			{
				shell_invalidName("rm", args[1]);
			}
			catch (SecurityException secExcept)
			{
				shell_security("rm", args[1]);
			}
		}
	}
//+-	
	private static void shell_cp(String[] args) throws Exception
	{
		if ((args.length != 3) && (args.length != 4))
		{
			shell_wrongQuantity("cp");
		}
		if ((args[args.length - 1].isEmpty() == true) || (args[args.length - 2].isEmpty() == true))
		{
			shell_noName("cp");
		}
		
		if (args.length == 3)
		{
			try
			{
				Path pathToFile = Paths.get(args[1]).normalize();
				if (pathToFile.isAbsolute() == false)
				{
					pathToFile = Paths.get(System.getProperty("user.dir").toString(), pathToFile.toString()).normalize();
				}
				if (pathToFile.toFile().exists() == false)
				{
					shell_notExist("cp", args[1]);
				}
				if (pathToFile.toFile().isFile() == false)
				{
					shell_notFile("cp", args[1]);
				}
				Path pathToNewFile = Paths.get(args[2]).normalize();
				if (pathToNewFile.isAbsolute() == false)
				{
					pathToNewFile = Paths.get(System.getProperty("user.dir").toString(), pathToNewFile.toString()).normalize();
				}
				if (pathToNewFile.getParent().toFile().exists() == false)
				{
					shell_notExist("cp", pathToNewFile.getParent().toString());
				}
				if (pathToNewFile.toString().equals(pathToFile.toString()) == true)
				{
					shell_equalNames("cp");
				}
				if (pathToNewFile.toFile().isDirectory() == true)
				{
					pathToNewFile = Paths.get(pathToNewFile.toString(), pathToFile.getFileName().toString()).normalize();
				}
				Files.copy(pathToFile, pathToNewFile, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
			}
			catch (InvalidPathException invExcept)
			{
				shell_invalidName("cp", args[1]);
			}
			catch (SecurityException secExcept)
			{
				shell_security("cp", args[1]);
			}
			catch (IOException ioExcept)
			{
				shell_wrongInput("cp");
			}
		}
		else
		{
			if (args[1].equals("-r") == false)
			{
				shell_wrongKey("cp", "-r");
			}
			try
			{
				Path pathToFile = Paths.get(args[2]).normalize();
				if (pathToFile.isAbsolute() == false)
				{
					pathToFile = Paths.get(System.getProperty("user.dir").toString(), pathToFile.toString()).normalize();
				}
				if (pathToFile.toFile().exists() == false)
				{
					shell_notExist("cp", args[2]);
				}
				if (pathToFile.toFile().isDirectory() == false)
				{
					shell_notDirectory("cp", args[2]);
				}
				Path pathToNewFile = Paths.get(args[3]).normalize();
				if (pathToNewFile.isAbsolute() == false)
				{
					pathToNewFile = Paths.get(System.getProperty("user.dir").toString(), pathToNewFile.toString()).normalize();
				}
				if (pathToNewFile.toFile().isDirectory() == false)
				{
					shell_notDirectory("cp", args[3]);
				}
				if (pathToNewFile.toString().equals(pathToFile.toString()) == true)
				{
					shell_equalNames("cp");
				}
				if (pathToNewFile.toString().startsWith(pathToFile.toString()) == true)
				{
					shell_intoSelf("cp");
				}
				
				pathToNewFile = Paths.get(pathToNewFile.toString(), pathToFile.getFileName().toString()).normalize();
				if (pathToNewFile.toFile().exists() == false)
				{
					if (pathToNewFile.toFile().mkdir() == false)
					{
						shell_notMkdir("mkdir", args[1]);
					}
				}
	
				String[] names = pathToFile.toFile().list();
				
				if (names.length != 0)
				{
					for (int i = 0; i < names.length; ++i)
					{
						if (Paths.get(pathToFile.toString(), names[i]).normalize().toFile().isDirectory() == true)
						{
							System.setProperty("user.dir", pathToFile.toString());
							String[] helpArray = new String[] {"cp", "-r", names[i], pathToNewFile.toString()};
							shell_cp(helpArray);
							System.setProperty("user.dir", pathToFile.getParent().toString());
						}
						if (Paths.get(pathToFile.toString(), names[i]).normalize().toFile().isFile() == true)
						{
							String[] helpArray = new String[] {"cp", Paths.get(pathToFile.toString(), names[i]).normalize().toString(), pathToNewFile.toString()};
							shell_cp(helpArray);
						}
					}
				}
			}
			catch (InvalidPathException invExcept)
			{
				shell_invalidName("cp", args[2]);
			}
			catch (SecurityException secExcept)
			{
				shell_security("cp", args[2]);
			}
			catch (IOException ioExcept)
			{
				shell_wrongInput("cp");
			}
		}	
	}
//+-	
	private static void shell_mv(String[] args) throws Exception
	{
		if (args.length != 3)
		{
			shell_wrongQuantity("mv");
		}
		if ((args[args.length - 1].isEmpty() == true) || (args[args.length - 2].isEmpty() == true))
		{
			shell_noName("mv");
		}
		
		try
		{
			Path pathToFile = Paths.get(args[1]).normalize();
			if (pathToFile.isAbsolute() == false)
			{
				pathToFile = Paths.get(System.getProperty("user.dir").toString(), pathToFile.toString()).normalize();
			}
			if (pathToFile.toFile().exists() == false)
			{
				shell_notExist("mv", args[1]);
			}
			if (pathToFile.toFile().isFile() == true)
			{
				Path pathToNewFile = Paths.get(args[2]).normalize();
				if (pathToNewFile.isAbsolute() == false)
				{
					pathToNewFile = Paths.get(System.getProperty("user.dir").toString(), pathToNewFile.toString()).normalize();
				}
				if (pathToNewFile.getParent().toFile().exists() == false)
				{
					shell_notExist("mv", pathToNewFile.getParent().toString());
				}
				if (pathToNewFile.toString().equals(pathToFile.toString()) == true)
				{
					shell_equalNames("mv");
				}
				if (pathToNewFile.toFile().isDirectory() == true)
				{
					pathToNewFile = Paths.get(pathToNewFile.toString(), pathToFile.getFileName().toString()).normalize();
				}
				Files.move(pathToFile, pathToNewFile, StandardCopyOption.REPLACE_EXISTING);
			}
			if (pathToFile.toFile().isDirectory() == true)
			{
				Path pathToNewFile = Paths.get(args[2]).normalize();
				if (pathToNewFile.isAbsolute() == false)
				{
					pathToNewFile = Paths.get(System.getProperty("user.dir").toString(), pathToNewFile.toString()).normalize();
				}
				if (pathToNewFile.getParent().toFile().exists() == false)
				{
					shell_notExist("mv", pathToNewFile.getParent().toString());
				}
				if (pathToNewFile.toString().equals(pathToFile.toString()) == true)
				{
					shell_equalNames("mv");
				}
				if (pathToNewFile.toString().startsWith(pathToFile.toString()) == true)
				{
					shell_intoSelf("mv");
				}
				
				String[] helpArray1 = new String[] {"cp", "-r", pathToFile.toString(), pathToNewFile.toString()};
				shell_cp(helpArray1);
				String[] helpArray2 = new String[] {"rm", "-r", pathToFile.toString()};
				shell_rm(helpArray2);
			}
		}
		catch (InvalidPathException invExcept)
		{
			shell_invalidName("mv", args[1]);
		}
		catch (SecurityException secExcept)
		{
			shell_security("mv", args[1]);
		}
		catch (IOException ioExcept)
		{
			shell_wrongInput("mv");
		}
	}

	private static void shell_ls(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			shell_wrongQuantity("ls");
		}
		
		try
		{
			String[] names = new File(System.getProperty("user.dir")).list();
			if (names.length != 0)
			{
				for (int i = 0; i < names.length; ++i)
				{
					System.out.println(names[i]);
				}
			}
		}
		catch (SecurityException secExcept)
		{
			shell_accessProhibited("ls", args[1]);
		}
	}
	
	private static void shell_exit(String[] args) throws Exception
	{
		System.exit(SUCCESS);
	}
	
	private static void shell_cat(String[] args) throws Exception
	{
		if (args.length != 2)
		{
			shell_wrongQuantity("cat");
		}
		if (args[1].isEmpty() == true)
		{
			shell_noName("cat");
		}
		
		try
		{
			File fileForCat = Paths.get(System.getProperty("user.dir"), args[1]).normalize().toFile();
			System.out.println(fileForCat.toString());
			if (fileForCat.exists() == false)
			{
				shell_notExist("cat", args[1]);
			}
			if (fileForCat.isFile() == false)
			{
				shell_notFile("cat", args[1]);
			}
			try
			{
				Scanner scanner = new Scanner(fileForCat);
				while (scanner.hasNext() == true)
				{
					System.out.println(scanner.nextLine());
				}
				scanner.close();
			}
			catch (IOException ioExcept)
			{
				shell_wrongInput("cat");
			}
		}
		catch (InvalidPathException invExcept)
		{
			shell_invalidName("cat", args[1]);
		}
		catch (SecurityException secExcept)
		{
			shell_security("cat", args[1]);
		}
	}
	
	//
	// basic functions
	//
	// end
	//
	
	private static void shell_parser(String[] buffer) throws Exception
	{
		switch(buffer[0])
		{
			case "cd":
			{
				shell_cd(buffer);
				break;
			}
			case "mkdir":
			{
				shell_mkdir(buffer);
				break;
			}
			case "pwd":
			{
				shell_pwd(buffer);
				break;
			}
			case "rm":
			{
				shell_rm(buffer);
				break;
			}
			case "cp":
			{
				shell_cp(buffer);
				break;
			}
			case "mv":
			{
				shell_mv(buffer);
				break;
			}
			case "ls":
			{
				shell_ls(buffer);
				break;
			}
			case "exit":
			{
				shell_exit(buffer);
				break;
			}
			case "cat":
			{
				shell_cat(buffer);
				break;
			}
			default:
			{
				System.err.println("Command does not exist: [" + buffer[0] + "]");
			}
		} 
	}

	private static void shell_accessProhibited(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ":  access to list of [" + name + "] is phohibited.");
	}
	
	private static void shell_alreadyExist(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ": the file [" + name + "] already exists.");
	}

	private static void shell_equalNames(String commandName) throws Exception
	{
		throw new Exception(commandName + ": files are same.");
	}
	
	private static void shell_intoSelf(String commandName) throws Exception
	{
		throw new Exception(commandName + ": copy into self.");
	}
	
	private static void shell_invalidName(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ": [" + name + "] is invalid name.");
	}

	private static void shell_notDirectory(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ": [" + name + "] is not a directory.");
	}
	
	private static void shell_notFile(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ": [" + name + "] is not a file.");
	}
		
	private static void shell_notExist(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ": the file [" + name + "] is not exists.");
	}

	private static void shell_notMkdir(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ": failed to create a directory [" + name + "].");
	}
	
	private static void shell_noName(String commandName) throws Exception
	{
		throw new Exception(commandName + ": no file name.");
	}
	
	private static void shell_security(String commandName, String name) throws Exception
	{
		throw new Exception(commandName + ": access to the [" + name + "] is prohibeted.");
	}
	
	private static void shell_smthWrong(String commandName) throws Exception
	{
		throw new Exception(commandName + ": something went wrong.");
	}
	
	private static void shell_wrongInput(String commandName) throws Exception
	{
		throw new Exception(commandName + ": wrong input.");
	}
	
	private static void shell_wrongKey(String commandName, String key) throws Exception
	{
		throw new Exception(commandName + ": key is wrong. Use [" + key + "].");
	}
	
	private static void shell_wrongQuantity(String commandName) throws Exception
	{
		throw new Exception(commandName + ": wrong quantity of arguments.");
	}

}