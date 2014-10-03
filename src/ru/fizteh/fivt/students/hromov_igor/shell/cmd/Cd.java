package ru.fizteh.fivt.students.hromov_igor.shell.cmd;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Cd {
	public static void run(String[] args) throws Exception{
		if(args.length != 2){
			throw new Exception("cd : wrong number of arguments");
		}
		try{
			File dir = Paths.get(args[1]).normalize().toFile();
			if(!dir.isAbsolute()){
				dir = Paths.get(System.getProperty("user.dir"), dir.getAbsolutePath()).normalize().toFile();
			}
			if(dir.exists()){
				if(dir.isDirectory()){
					System.setProperty("user.dir", dir.getPath());
				} else {
					throw new Exception("cd : " + args[1] + " is not a folder");
				}
			}
			else{
				throw new Exception("cd : No such file or directory");
			}
		} catch(SecurityException e){
			throw new Exception("mkdir : cannot create folder : access deneid");
		}
		catch(InvalidPathException e){
			throw new Exception("mkdir : wrong name of folder");
		}
	}
}
