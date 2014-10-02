import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner; 
import java.util.regex.*;
import java.io.*;
import java.lang.*;
import java.nio.file.*;

public class Shell {
	public static void main(String[] args) {
				Scanner in = new Scanner(System.in);
				String regime = in.nextLine();
				String currentdir = "C:";
				String[] parserstring = regime.split(" ");
				Parser parser = new Parser();
				MethodsDirectory objectdir = new MethodsDirectory();
	        	MethodsFiles objectfile = new MethodsFiles();
	        	//for package
				if (parserstring.length > 2) {
					packageparser(parserstring,in);
					in.close();
					return;
				}
				System.out.println("$");
				//input comand of Shell;.
		        String comand;
		        while (true){
		        	File path = new File(currentdir);
		        	comand = in.nextLine();
		        	String[] comands = comand.split(";");
		        	int j = 0;
		        	while (j < comands.length) {
		        		String[] ParsedComand = comands[j].split(" ");
						if (ParsedComand[0].equals("exit")) {
		        					break;
		        				}
		        			if (ParsedComand[0].equals("cd")) {
		        					currentdir = parser.cd(ParsedComand, currentdir);
		        				}
		        			if (ParsedComand[0].equals("pwd")) {
		        					objectdir.pwd(path);
		        				}
		        			if (ParsedComand[0].equals("mkdir")) {
		        					objectdir.mkdir(ParsedComand[1], path);
		        				}
		        			if (ParsedComand[0].equals("rm")) {
		        					parser.rm(path, ParsedComand);
		        				}
		        			if (ParsedComand[0].equals("cp")) {
		        					parser.cp(path, ParsedComand);
		        				}
		        			if (ParsedComand[0].equals("ls")) {
		        					objectdir.ls(path, ParsedComand);
		        				}
		        			if (ParsedComand[0].equals("mv")) {
		        					parser.mv(path, ParsedComand);
		        				}
		        			if (ParsedComand[0].equals("cat")) {
		        					parser.cat(path, ParsedComand);
		        				}
		        			j = j + 1;
		        		}
		        		System.out.println("$");
		        	}
			}
	
	static void packageparser(String[] parserstring, Scanner in) {
		String currentdir = "C:";
		Parser parser = new Parser();
		MethodsDirectory objectdir = new MethodsDirectory();
    	MethodsFiles objectfile =  new MethodsFiles();
		int i = 2;
		while (i < parserstring.length) {
			File path = new File(currentdir);
				if (parserstring[i].equals("exit")) {
        				break;
        			}
        		if (parserstring[i].equals("cd")) {
        				String argstring = parserstring[i] + " " + parserstring[i+1];
        				String[] arg = argstring.split(" ");
        				currentdir = parser.cd(arg, currentdir);
        				i = i + 2;
        				continue;
        			}
        		if (parserstring[i].equals("pwd")) {
        				objectdir.pwd(path);
        				i = i + 1;
        				continue;
        			}
        		if (parserstring[i].equals("mkdir")) {
        				objectdir.mkdir(parserstring[i+1], path);
        				i = i + 2;
        				continue;
        			}
        		if (parserstring[i].equals("rm")) {
        				String argstring;
        				if (parserstring[i+1].equals("-r")) {	
        						argstring = parserstring[i] + " " + parserstring[i+1] + " " + parserstring[i+2];
        						i = i + 3;
        					} else {
        						argstring = parserstring[i] + " " + parserstring[i+1];
        						i = i + 2;
        					}
        				String[] arg = argstring.split(" ");
        				parser.rm(path,	arg);
        				continue;
        			}
        		if (parserstring[i].equals("cp")) {
        				String argstring;
        				if (parserstring[i+1].equals("-r")) {	
        						argstring = parserstring[i] + " " + parserstring[i+1] + " " + parserstring[i+2] + " " + parserstring[i+3];
        						i=i+4;
        					} else {
        						argstring = parserstring[i] + " " + parserstring[i+1] + " " + parserstring[i+2];
        						i = i + 3;
        					}
        				String[] arg = argstring.split(" ");
        				parser.rm(path,	arg);
        				continue;
        			}
        		if (parserstring[i].equals("ls")) {
        				String argstring=parserstring[i]+" "+parserstring[i+1];
        				String[] arg=argstring.split(" ");
        				objectdir.ls(path, arg);
        				i=i+2;
        				continue;
        			}
        		if (parserstring[i].equals("mv")) {
        				String argstring = parserstring[i] + " " + parserstring[i+1] + " " + parserstring[i+2];
        				String[] arg = argstring.split(" ");
        				objectdir.ls(path, arg);
        				i = i + 3;
        				continue;
        			}
        		if (parserstring[i].equals("cat")) {
        				String argstring = parserstring[i] + " " + parserstring[i+1];
        				String[] arg = argstring.split(" ");
        				objectdir.ls(path, arg);
        				i = i + 2;
        				continue;
        			}
        		System.out.println("$");
		}
	}
}


class Parser {
	
	String cd(String[] comand, String currentdir) {
		MethodsDirectory objectdir = new MethodsDirectory();
		boolean flag = false;
		flag = objectdir.cd(comand);
		if (flag) {
			String dirname = new String();
			for (int i = 1 ;i<comand.length ;++i) {
				dirname = dirname + comand[i] +' ';
			}
			currentdir = dirname;
			return currentdir;
		}
		return currentdir;
	}
	
	void cp(File path,String[] comand) {
		MethodsDirectory objectdir = new MethodsDirectory();
		MethodsFiles objectfile = new MethodsFiles();
		if (comand[1].equals("-r")) {
			String arg2 = new String();
			File obj = new File(path, comand[2]);
			File object = new File(path, comand[3]);
			if (!obj.exists()) {
					System.out.println("there is no file at the root directory of the");
					return;
				}
			if (!object.exists()) {
					System.out.println("there is no file at the root directory of the");
					return;
				}
			if (obj.isDirectory() == true && object.isDirectory()) {
					arg2 = object.getAbsolutePath();
					objectdir.cp(comand[2], arg2, true, path);
				} else if(obj.isDirectory() == true && object.isDirectory() == false) {
					System.out.println("can't copy directory in file");
					return;
				} else {
					try {
						arg2 = object.getAbsolutePath();
						objectfile.cp(comand[2], arg2);
					} catch (IOException e) {
						System.out.println("unable to copy files");
					}
				}
		} else {
			String arg2 = new String();
			File obj = new File(comand[1]);
			File object = new File(comand[2]);
			if (!obj.exists()) {
					System.out.println("there is no file at the root directory of the");
					return;
				}
			if (!object.exists()) {
					System.out.println("there is no file at the root directory of the");
					return;
				}
			if(obj.isDirectory() == true && object.isDirectory() == true) {
					arg2 = object.getAbsolutePath();
					objectdir.cp(comand[1], arg2, false, path);
				} else if(obj.isDirectory() == true && object.isDirectory() == false) {
					System.out.println("cant copy directory to file");
				} else {
					try {
						arg2 = object.getAbsolutePath();
						objectfile.cp(comand[1], arg2);
					} catch (IOException e) {
						System.out.println("unable to copy files");
					}
				}
		}
	}

	void rm(File path,String[] comand) {
		MethodsDirectory objectdir = new MethodsDirectory();
		MethodsFiles objectfile = new MethodsFiles();
		if (comand[1].equals("-r")) {
			String arg = new String();
			if (comand.length<2)
				System.out.println("Not enough arguments");
			if (comand.length>3)
				System.out.println("argument is greater than 1");
			File obj = new File(path, comand[2]);
			if (obj.isDirectory()) {
					arg = obj.getAbsolutePath();
					objectdir.rm(arg);
				} else if(obj.isFile()) {
					arg = obj.getAbsolutePath();
					objectfile.rm(arg);
				} else {
					System.out.println("current directory hasn't this file");
					return;
				}
		} else {
			String arg = new String();
			if (comand.length<1)
				System.out.println("Not enough arguments");
			if (comand.length>2)
				System.out.println("argument is greater than 1");
			File removeobject = new File(path, comand[1]);
			if (removeobject.isFile() == true) {
					arg = removeobject.getAbsolutePath();
					objectfile.rm(arg);
				} else if(removeobject.isDirectory()) {
					boolean a = removeobject.delete();
					if (a == false)
					System.out.println("file is directory");
				} else {
					System.out.println("current directory hasn't this file");
					return;
				}
		}
	}

	void mv(File path,String[] comand) {
		MethodsDirectory object1 = new MethodsDirectory();
		MethodsFiles object2 = new MethodsFiles();
		boolean arg1path = false;
		boolean arg2path = false;
		File arg1 = new File(comand[1]);
		if (arg1.exists()) {
			arg1path = true;//дан полный путь до comand[1]
		}
		File arg2 = new File(comand[2]);
		if (arg2.exists()) {
			arg2path = true;//дан полный путь до comand[2]
		}
		if (arg1path == true && arg2path == true) {
			if ((arg1.isDirectory() && arg2.isDirectory()) || (arg1.isDirectory() == false && arg2.isDirectory() == true)) {
				object1.mv(arg1, arg2);
				return;
			}
			if (arg1.isFile() && arg2.isFile()) {
				object2.mv(arg1, arg2);
				return;
			}
			System.out.println("no unable mv directory to file");
			return;
		}
		File path1 = new File(path, comand[1]);
		File path2 = new File(path, comand[2]);
		if (arg1path == true && arg2path == false && path2.exists()) {
			if ((arg1.isDirectory() && path2.isDirectory()) || (arg1.isDirectory() == false && path2.isDirectory() == true)) {
				object1.mv(arg1, path2);
				return;
			}
			if (arg1.isFile() && path2.isFile()) {
				object2.mv(arg1, path2);
				return;
			}
			System.out.println("no unable mv directory to file");
			return;
		}
		if (arg1path == false && arg2path == true && path1.exists()) {
			if ((path1.isDirectory() && arg2.isDirectory()) || (path1.isDirectory() == false && arg2.isDirectory() == true)) {
				object1.mv(path1, arg2);
				return;
			}
			if (path1.isFile() && arg2.isFile()) {
				object2.mv(path1, arg2);
				return;
			}
			System.out.println("no unable mv directory to file");
			return;
		}
		if (arg1path == false && arg2path == false && path1.exists() && path2.exists()) {
			if ((path1.isDirectory() && path2.isDirectory()) || (path1.isDirectory() == false && path2.isDirectory() == true)) {
				object1.mv(path1, path2);
				return;
			}
			if (path1.isFile() && path2.isFile()) {
				object2.mv(path1, path2);
				return;
			}
			System.out.println("no unable mv directory to file");
			return;
		}
		System.out.println("can't find " + comand[1] + " and " +comand[2]);
	}

	void cat(File path,String[] comand) {
		File mainfile = new File(path,comand[1]);
		try {
			BufferedReader a = new BufferedReader(new FileReader(mainfile));
			String s;
			StringBuilder sb = new StringBuilder();
		    try {
				while ((s = a.readLine()) != null) { 
					sb.append(s + "\n");
					System.out.println(s);
				}
			} catch (IOException e) {
				System.out.println("can't cat file");
			}
		    try {
				a.close();
			} catch (IOException e) {
				System.out.println("can't cat file");
			}
		} catch (FileNotFoundException e) {
			System.out.println("can't read file");
		}
	}
}


class MethodsDirectory {
	
	boolean cd(String[] comand) {
		String dirname = new String();
		for(int i = 1; i < comand.length; ++i) {
			dirname = dirname + comand[i] + ' ';
		}
		File file = new File(dirname);
		// check exist file
		if (!file.exists()) {
			System.out.println("cd:" + dirname + ": No such file or directory");
			return false;
		}
		if (!file.isDirectory()) {
			System.out.println("cd:" + dirname + "this is not directory");
			return false;
		}
		return true;
	}
	
	void pwd(File path) {
			System.out.println(path.getAbsolutePath());
		}
	
	boolean mkdir(String dirname,File path) {
			if (path.isDirectory()) {
				File newdir = new File(path,dirname);
				if (newdir.exists()) {
						System.out.println("directory already exists");
						return false;
					}
				newdir.mkdir();
				return true;
			}
				return false;
		}
	
	void rm(String file) {
		File removefile = new File(file);
		if (!removefile.exists()) {
				System.out.println("current directory hasn't this file");
				return;
			}
		MethodsDirectory object = new MethodsDirectory();
		String[] list;
		list = removefile.list();
		if (list.length == 0) {
				removefile.delete();
				return;
			}
		for (int i = 0; i < list.length; ++i)
		{
			File obj = new File(removefile.getAbsolutePath(), list[i]);
			if (obj.isDirectory() == true) {
					String arg = new String();
					arg = file + "\\" + list[i];
					object.rm(arg);
				} else if(obj.isFile()) {
					obj.delete();
				}
		}
		removefile.delete();
	}

	void cp(String dir, String place, boolean flag, File current) {
		File placefile = new File(place);
		if (!placefile.exists()) {
			System.out.println("unable to copy directory to file");
			return;
		}
		if (placefile.isFile()) {
			System.out.println("unable to copy directory to file");
			return;
		}
		if (flag == true) {
			File directory = new File(current, dir);
			File newfile = new File(placefile, dir);
			if (newfile.exists()) {
				System.out.println("dir exists");
				return;
			}
			if (directory.isFile()) {
				System.out.println("unable to copy directiry");
				return;
			}
			if (directory.isDirectory())
				newfile.mkdir();
			String[] list;
			list=directory.list();
			if (list.length>0) {
				for (int i = 0; i<list.length; ++i) {
					File newarg = new File(directory, list[i]);
					if (newarg.isFile()) {
						MethodsFiles object = new MethodsFiles();
						try {
							String arg2 = new String();
							arg2 = place + "\\" + dir;
							object.cp(list[i], arg2);
						} catch (IOException e) {
							System.out.println("unable copy directory");
						}
							break;
					}
					String arg2 = new String();
					arg2 = place + "\\" + dir;
					cp(list[i], arg2, true, placefile);
				}
			}
		} else {
			File copyfile = new File(current, dir);
			File newfile = new File(placefile, dir);
			if (copyfile.exists()) {
				System.out.println("dir exists");
				return;
			}
			if (copyfile.isFile()) {
				System.out.println("unable to copy directiry");
				return;
			}
			if (copyfile.isDirectory()) {
				newfile.mkdir();
				return;
			}
			System.out.println("could not create file");
		}
	}

	void ls(File path, String[] comand) {
		File newfile = new File(path, comand[1]);
		if (!newfile.exists()) {
			System.out.println("no hasn't file " + comand[1]);
			return;
		}
		String[] list;
		list = newfile.list();
		for (int i = 0 ;i<list.length ;++i) {
			System.out.println(list[i]);
		}
	}

	void mv(File arg1, File arg2) {
		String arg1parent = arg1.getParent();
		String arg2parent = arg2.getParent();
		if (arg1parent.equals(arg2parent)) {
			arg1.renameTo(arg2);
		}
		specialcp(arg1, arg2);
	}
	
	void specialcp(File arg1, File arg2)
	{
		//always with -r
		if (arg1.isFile()) {
			File newfile = new File(arg2 ,arg1.getName());
			try {
				newfile.createNewFile();
			} catch (IOException e) {
				System.out.println("error createNewFile");
			}
			return;
		}
		File newfile = new File(arg2 ,arg1.getName());
		if (arg1.isDirectory()) {
			newfile.mkdir();
		}
		String[] list = arg1.list();
		if (list.length>0) {
			for (int i = 0; i < list.length; ++i) {
				File newarg = new File(arg1, list[i]);
				if (newarg.isFile()) {
						File newf = new File(newfile, list[i]);
						try {
							newf.createNewFile();
						} catch (IOException e) {
							System.out.println("error createNewFile");
						}
						continue;
				}
				File newf = new File(newfile, list[i]);
				specialcp(newfile, newf);
			}
		}
	}
}


class MethodsFiles {
	
	void rm(String file) {
		File removefile = new File(file);
		removefile.delete();
	}

	void cp(String file, String place) throws  IOException {
		File placefile = new File(place);
		if (placefile.isFile()) {
			File copyfile = new File(file);
			Files.copy(copyfile.toPath(), placefile.toPath());
			return;
		}
		// create file in directory place (if place is directory)
		String path = new String();
		path = placefile.getAbsolutePath();
		path = path + "\\" + file;
		File newfile = new File(path);
		if (newfile.exists()) {
			System.out.println("file exists");
			return;
		}
		if (newfile.createNewFile()) {
			return;
		}
		System.out.println("could not create file");
	}

	void mv(File arg1,File arg2) {
		arg1.renameTo(arg2);//переименование первого аргумента
	}
}




