package ru.fizteh.fivt.students.maxim_rep.shell;

public class OsData {

	public static String backslash = "/";
	public static String root = "/";

	public static void setOsSettings() {
		String os = getOsType();
		if (os.equals("win")) {
			backslash = "\\";
			root = "C:\\";
			Shell.currentDrive = "C";
		} else {
			backslash = "/";
			root = "/";
		}
	}

	public static String getOsType() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0)
			return "win";
		else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0
				|| os.indexOf("aix") > 0)
			return "nix";
		else
			return "nix"; // whynot

	}
}
