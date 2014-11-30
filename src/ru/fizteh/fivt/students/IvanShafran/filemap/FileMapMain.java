package ru.fizteh.fivt.students.IvanShafran.filemap;


public class FileMapMain {
    public static void main(String[] args) {
        FileMap fileMap = new FileMap();

        if (args.length == 0) {
            fileMap.startInteractiveMode();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : args) {
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }

            fileMap.startPacketMode(stringBuilder.toString());
        }
    }
}
