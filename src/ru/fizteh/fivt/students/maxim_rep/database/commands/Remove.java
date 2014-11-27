package ru.fizteh.fivt.students.maxim_rep.database.commands;

import ru.fizteh.fivt.students.maxim_rep.database.HexConverter;
import ru.fizteh.fivt.students.maxim_rep.database.IoLib;
import ru.fizteh.fivt.students.maxim_rep.database.StringConverter;

public class Remove implements DBCommand {

    String filePath;
    String keyName;

    public Remove(String filePath, String keyName) {
        this.filePath = filePath;
        this.keyName = keyName;
    }

    public static boolean removeFromDB(String filePath, String keyName) {
        String[] keysUnformed = IoLib.getKeyList(filePath, true, false);

        if (keysUnformed == null) {
            return false;
        }

        String keysAll = "";
        String dataAll = "";
        for (int i = 0; i < keysUnformed.length; i++) {
            String curLine = keysUnformed[i];

            if (keyName.equals(HexConverter.hexToString(curLine.substring(0,
                    curLine.length() - 10)))) {
                int deltaSize = HexConverter.stringToHex(
                        StringConverter.convertToUTF8(Get.getValueByKey(
                                filePath, keyName, false))).length();
                for (int j = i + 1; j < keysUnformed.length; j++) {

                    String curLine2 = keysUnformed[j];

                    int newSize = Integer.parseInt(
                            curLine2.substring(curLine2.length() - 8,
                                    curLine2.length()), 16)
                            - deltaSize;

                    keysAll = keysAll
                            + curLine2.substring(0, curLine2.length() - 8)
                            + StringConverter.convertIntTo8(newSize);

                    dataAll = dataAll
                            + Get.getValueByKey(filePath, curLine2.substring(0,
                                    curLine2.length() - 10), true);
                }
                break;
            } else {
                keysAll = keysAll + keysUnformed[i];
                dataAll = dataAll
                        + Get.getValueByKey(filePath,
                                curLine.substring(0, curLine.length() - 10),
                                true);
            }
        }
        IoLib.writeToDB(
                filePath,
                keysAll
                        + HexConverter.stringToHex(StringConverter
                                .convertToUTF8(dataAll)), false);
        return true;

    }

    @Override
    public boolean execute() {
        boolean result = removeFromDB(filePath, keyName);
        if (result) {
            System.out.println("REMOVED");
            return true;
        } else {
            System.out.println("NOT FOUND");
            return false;
        }
    }
}
