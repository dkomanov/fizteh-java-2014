package ru.fizteh.fivt.students.gudkov394.Storable.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapMain {
    public static void main(final String[] args) {
        ArrayList<Class<?>> signatureTmp = readSignature();
        MyMap map = new MyMap(args, signatureTmp);
    }

    private static ArrayList<Class<?>> readSignature() {
        ArrayList<Class<?>> signature = new ArrayList<Class<?>>();
        File inputSignature = new File(System.getProperty("db.file") + File.separator + "signature.tsv");
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputSignature);
        } catch (FileNotFoundException e) {
            System.err.println("Incorrect file with signature");
            System.exit(10);
        }
        String input = scanner.nextLine();
        input = input.trim();
        String[] listSignature = input.split("//s+");
        for (String s : listSignature) {
            switch (s) {
                case "int":
                    signature.add(Integer.class);
                    break;
                case "long":
                    signature.add(Long.class);
                    break;
                case "byte":
                    signature.add(Byte.class);
                    break;
                case "double":
                    signature.add(Double.class);
                    break;
                case "boolean":
                    signature.add(Boolean.class);
                    break;
                case "string":
                    signature.add(String.class);
                    break;
                default: System.err.println("Wrong name of type");
                         System.exit(3);
                    break;
            }
        }
        return signature;
    }
}
