package ru.fizteh.fivt.students.maxim_rep.database.commands;

import java.io.*;

import ru.fizteh.fivt.students.maxim_rep.database.StringConverter;

public class Put implements DBCommand {
	String filePath;
	String keyName;
	String dataText;

	public Put(String keyName, String dataText, String filePath) {
		this.keyName = keyName;
		this.dataText = dataText;
		this.filePath = filePath;
	}

	@Override
	public boolean execute() {
		File f = new File(filePath);

		if (!f.exists() || !f.isFile()) {
			System.out.println("Put: " + filePath + ": File not found!");
			return false;
		} else {
			FileWriter writer;
			try {
				writer = new FileWriter(f, true);
			} catch (IOException ex) {
				System.out.println("Put: " + filePath + ": " + ex.getMessage());
				return false;
			}

			try {
				writer.write(keyName);
				writer.write("00000000");
				writer.write(dataText);
				writer.write(StringConverter.convertIntTo6(dataText.length() / 2));
				writer.flush();
			} catch (IOException ex) {
				System.out.println("Put: Error: " + ex.getMessage() + "\"");
			}
			try {
				writer.close();
			} catch (IOException ex) {
				System.out.println("Put: Error: " + ex.getMessage() + "\"");
				return false;
			}

		}
		return true;
	}
}
