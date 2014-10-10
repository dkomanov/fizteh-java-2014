package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

public class MultiFileHashMapMain {
    public static void main(String[] arg) {
        try {            
            if (arg.length == 0) {
                Intercative.conv();
            } else {
                InputParse.parse(arg);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
