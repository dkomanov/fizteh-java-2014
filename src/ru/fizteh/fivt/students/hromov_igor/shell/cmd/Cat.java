package ru.fizteh.fivt.students.hromov_igor.shell.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Cat {
	public static void run(String[] args) throws Exception{
		if(args.length != 2){
			throw new Exception("cat : wrong number of arguments");
		}
		File file;
		if (args[1].charAt(0) == File.separatorChar){
			 file = new File(args[1]);
		 } else{
			 file = new File(System.getProperty("user.dir") + File.separator + args[1]); 
		 }
		if(file.isFile()){
			InputStream in = null;
			try{
				in = new FileInputStream(file);
				rw(in, System.out);
				in.close();
			} catch(Exception e){
				throw new Exception("cat : cannot read file");
			}
		} else if(file.isDirectory()) {
			throw new Exception("cat : can't open directory");
		} else{
			throw new Exception("cat : no such file or directory");
		}
	}
	
	public static void rw(InputStream in, OutputStream out) throws Exception {
		int lengthRead;
		byte[] buffer = new byte[4096];
		while((lengthRead = in.read(buffer)) != -1){
			out.write(buffer, 0, lengthRead);
		}
	}
}
