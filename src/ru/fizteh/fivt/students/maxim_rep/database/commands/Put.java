package ru.fizteh.fivt.students.maxim_rep.database.commands;

import ru.fizteh.fivt.students.maxim_rep.database.*;

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
                + allDataInHex + newDataInHex, false);

    }

    @Override
    public boolean execute() {

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
