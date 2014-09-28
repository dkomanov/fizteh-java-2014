package ru.fizteh.fivt.students.maxim_rep.database.commands;

import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class List implements DBCommand {

	String filePath;

	public List(String filePath) {
		this.filePath = filePath;
	}

	public static String[] getKeyList(String filePath) {
		File f = new File(filePath);

		if (!f.exists() || !f.isFile()) {
			System.out.println("List: " + filePath + ": No such file");
			return null;
		}

		FileReader in;
		char[] buffer = new char[4096];
		String data = "";
		String[] separated = null;
		int len;

		try {
			in = new FileReader(f);
		} catch (FileNotFoundException ex) {
			System.out.println("List: " + filePath + ": No such file");
			return null;
		}

		try {
			while ((len = in.read(buffer)) != -1) {
				String s = new String(buffer, 0, len);
				data = data + s;
			}
		} catch (IOException ex) {
			System.out.println("List: Error: " + ex.getMessage() + "\"");
			return null;
		}
		try {
			// @SuppressWarnings("resource")
			// FileOutputStream file = new FileOutputStream(filePath);
			separated = data.split(" 00000000 ");
			for (String curLine : separated) {
				try {
					System.out.println(curLine);
				} catch (Exception ex) {
					break;
				}
			}
			for (int i = 0; i < separated.length - 1; i++) {
				if (i != 0) {
					separated[i] = separated[i + 1].substring(0, 8) + " "
							+ separated[i].substring(8);
				} else {
					separated[i] = separated[i + 1].substring(0, 8) + " "
							+ separated[i];
				}
				System.out.println(separated[i]);
			}
			// separated[separated.length-1] = null;
			// file.write(data.getBytes());
			in.close();
		} catch (IOException ex) {
			System.out.println("List: Error: " + ex.getMessage() + "\"");
			return null;

		}
		return separated;
	}

	@Override
	public boolean execute() {
		String[] keys = getKeyList(filePath);

		if (keys == null) {
			return false;
		}

		for (String curLine : keys) {
			System.out.println(curLine.substring(8));
		}
		return true;
	}
}
