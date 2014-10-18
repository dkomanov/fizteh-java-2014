package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.IoLib;

public class Show implements DBCommand {

	String filePath;

	public Show(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public boolean execute() {
		String[] tableArray = IoLib.getTables();
		if (tableArray == null) {
			return false;
		}
		System.out.println("table_name row_count");
		for (String curLine : tableArray) {
			System.out.println(curLine.split("\n")[1] + " "
					+ curLine.split("\n")[0]);
		}
		return true;
	}

}
