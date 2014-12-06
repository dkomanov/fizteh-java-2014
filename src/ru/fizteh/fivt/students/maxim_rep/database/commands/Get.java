package ru.fizteh.fivt.students.maxim_rep.database.commands;

import ru.fizteh.fivt.students.maxim_rep.database.HexConverter;
import ru.fizteh.fivt.students.maxim_rep.database.IoLib;

public class Get implements DBCommand {

    String filePath;
    String keyName;

    public Get(String filePath, String keyName) {
        this.filePath = filePath;
        this.keyName = keyName;
    }

    public static String getValueByKey(String filePath, String keyName,
            boolean isHex) {
        if (isHex) {
            keyName = HexConverter.hexToString(keyName);
        }
        String[] keys = IoLib.getKeyList(filePath, false, false);
        String data = IoLib.getData(filePath);

        if (keys == null) {
            return null;
        }

        for (int i = 0; i < keys.length; i++) {
            String curLine = keys[i];
            if (keyName.equals(HexConverter.hexToString(curLine.substring(8)))) {
                int firstAdress = Integer.parseInt(curLine.substring(0, 8), 16);
                int secondAdress;
                if (i == keys.length - 1) {
                    secondAdress = data.length();
                } else {
                    secondAdress = Integer.parseInt(
                            keys[i + 1].substring(0, 8), 16);
                }
                return HexConverter.hexToString(data.substring(firstAdress,
                        secondAdress));
            }
        }
        return null;
    }

    @Override
    public boolean execute() {
        String result = getValueByKey(filePath, keyName, false);
        if (result == null) {
            System.out.println("NOT FOUND");
            return false;
        } else {
            System.out.println("FOUND");
            System.out.println(result);
        }
        return true;
    }
}
