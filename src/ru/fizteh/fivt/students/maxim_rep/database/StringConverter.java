package ru.fizteh.fivt.students.maxim_rep.database;

public class StringConverter {

	public static String convertFromUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
			return null;
		}
		return out;
	}

	public static String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
			return null;
		}
		return out;

	}

	public static String convertIntTo6(int integer) {
		String converted = "" + integer;
		while (converted.length() < 6) {
			converted = "0" + converted;
		}
		return converted;

	}

}