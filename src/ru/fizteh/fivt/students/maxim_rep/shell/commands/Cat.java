package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.Parser;
import java.io.*;

public class Cat implements ShellCommand {

	String currentPath;
	String FileName;

	public Cat(String currentPath, String FileName) {
		this.FileName = Parser.pathConverter(FileName, currentPath);
		this.currentPath = currentPath;
	}

	@Override
	public boolean execute() {
		File f = new File(FileName);

		if (!f.exists() || !f.isFile()) {
			System.out.println("cat: " + FileName + ": No such file");
			return false;
		} else {
			FileReader in;
			try {
				in = new FileReader(f);
			} catch (FileNotFoundException ex) {
				System.out.println("cat: " + FileName + ": No such file");
				return false;
			}

			char[] buffer = new char[4096];
			int len;
			try {
				while ((len = in.read(buffer)) != -1) {
					String s = new String(buffer, 0, len);
					System.out.println(s);
				}
			} catch (IOException ex) {
				System.out.println("cat: Error: " + ex.getMessage() + "\"");
				return false;
			}
			try {
				in.close();
			} catch (IOException ex) {
				System.out.println("cat: Error: " + ex.getMessage() + "\"");
				return false;
			}

		}
		return true;
	}
}
