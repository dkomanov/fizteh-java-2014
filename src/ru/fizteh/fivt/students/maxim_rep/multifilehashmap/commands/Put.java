package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.*;

public class Put implements DBCommand {
    String filePath;
    String keyName;
    String dataText;

    public Put(String filePath, String keyName, String dataText) {
        this.keyName = keyName;
        this.dataText = dataText;
        this.filePath = filePath;
    }

    public static boolean putData(String keyName, String dataText,
            String filePath) {
    	filePath = IoLib.getDataFilePath(keyName)[0];
        String[] keys = IoLib.getKeyList(filePath, true, false);
        String data = IoLib.getData(filePath);

        String allDataInHex = "";
        if (data != null) {
            allDataInHex = data;
        }

        String allKeysInHex = "";
        if (keys != null) {
            for (String tmp : keys) {
                allKeysInHex = allKeysInHex + tmp;
            }
        }
    	
        String newDataInHex = HexConverter.stringToHex(StringConverter
                .convertToUTF8(dataText));
        String newKeyInHex = HexConverter.stringToHex(StringConverter
                .convertToUTF8(keyName))
                + "00"
                + StringConverter.convertIntTo8(allDataInHex.length());

        return IoLib.writeToDB(filePath, allKeysInHex + newKeyInHex
                + allDataInHex + newDataInHex, false, keyName);
    	

    }

    @Override
    public boolean execute() {
		if (DbMain.currentTable == null) {
			System.out.println("no table");
			return false;
		}
		
		if (!IoLib.tableExists(DbMain.currentTable)) {
			System.out.println(DbMain.currentTable +" not exists");
			return false;
		}
		
        String result = Get.getValueByKey(filePath, keyName, false);
        if (result != null) {
            System.out.println("OVERWRITE");
            System.out.println(result);
            Remove.removeFromDB(filePath, keyName);
        } else {
            System.out.println("NEW");
        }

        return putData(keyName, dataText, filePath);

    }
}
