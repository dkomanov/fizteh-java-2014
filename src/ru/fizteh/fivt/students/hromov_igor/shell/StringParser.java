package ru.fizteh.fivt.students.hromov_igor.shell;

import ru.fizteh.fivt.students.hromov_igor.shell.cmd.*;


public class StringParser {
	
	public static void parse(String[] str_p, boolean fl){
		try{
			switch(str_p[0]){
			case "exit":
				System.exit(0);
			case "ls":
				Ls.run(str_p);
				break;
			case "pwd" :
				Pwd.run(str_p);
				break;
			case "cat" :
				Cat.run(str_p);
				break;
			case "cp" :
				Cp.run(str_p);
				break;
			case "rm" :
				Rm.run(str_p);
				break;
			case "mkdir" :
				Mkdir.run(str_p);
				break;
			case "cd" :
				Cd.run(str_p);
				break;
			case "mv" :
				Mv.run(str_p);
				break;
			default:
				System.err.println(str_p[0] + " : no such command");
				if(fl){
					System.exit(1);
				}
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			if(fl){
				System.exit(1);
			}
		}
	}
	
}
