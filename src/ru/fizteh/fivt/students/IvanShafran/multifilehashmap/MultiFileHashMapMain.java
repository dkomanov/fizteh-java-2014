package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;


public class MultiFileHashMapMain {
    public static void main(String[] args) {
        MultiFileHashMap multiFileHashMap = new MultiFileHashMap();

        if (args.length == 0) {
            multiFileHashMap.startInteractiveMode();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : args) {
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }

            multiFileHashMap.startPacketMode(stringBuilder.toString());
        }
    }
}
