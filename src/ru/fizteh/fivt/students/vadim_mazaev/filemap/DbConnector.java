package ru.fizteh.fivt.students.vadim_mazaev.filemap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

public final class DbConnector {
	public DbConnector() {
		dataBase = new TreeMap<String, String>();
		try {
			dbFile = new RandomAccessFile(System.getProperty("db.file"), "r");
			readDbFromFile();
			dbFile.close();
		} catch (Exception e) {
			System.err.println("Cannot reading DataBase");
			System.exit(1);
		}
	}
	
	private void readDbFromFile() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		List<Integer> offsets = new ArrayList<Integer>();
		List<String> keys = new ArrayList<String>();
		byte b; int bytesCounter = 0;

		while ((b = dbFile.readByte()) != 0) {
			bytesCounter++;
			out.write(b);
		}
		bytesCounter++;
		int firstOffset = dbFile.readInt();
		bytesCounter += 4;
		keys.add(out.toString("UTF-8"));
		out.reset();
		while (bytesCounter < firstOffset) {
			while ((b = dbFile.readByte()) != 0) {
				bytesCounter++;
				out.write(b);
			}
			bytesCounter++;
			offsets.add(dbFile.readInt());
			keys.add(out.toString("UTF-8"));
			out.reset();
			bytesCounter += 4;
		}
		
		Iterator<String> keyIter = keys.iterator();
		Iterator<Integer> offIter = offsets.iterator();
		while (offIter.hasNext()) {
			int nextOffset = offIter.next();
			while (bytesCounter < nextOffset) {
				out.write(dbFile.readByte());
				bytesCounter++;
			}
			dataBase.put(keyIter.next(), out.toString("UTF-8"));
			out.reset();
		}
		while (bytesCounter < dbFile.length()) {
			out.write(dbFile.readByte());
			bytesCounter++;
		}
		dataBase.put(keyIter.next(), out.toString("UTF-8"));
		out.close();
	}
	
	private RandomAccessFile dbFile;
	private Map<String, String> dataBase;
	//state, link to file, etc.
}
