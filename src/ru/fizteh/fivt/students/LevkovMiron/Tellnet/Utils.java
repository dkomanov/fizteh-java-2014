package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Мирон on 10.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class Utils {

    ArrayList<Class<?>> readSignature(String name) throws ParseException {
        ArrayList<Class<?>> signature = new ArrayList<Class<?>>();
        File inputSignature = new File(System.getProperty("db.file") + File.separator + name
                + File.separator + "signature.tsv");
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputSignature);
        } catch (FileNotFoundException e) {
            throw new ParseException("Incorrect file with signature", 0);
        }
        String input = scanner.nextLine();
        return signature(input);
    }

    public ArrayList<Class<?>> signature(String input) throws ParseException {
        ArrayList<Class<?>> signature = new ArrayList<>();
        input = input.trim();
        String[] listSignature = input.split("\\s+");
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
                case "String":
                    signature.add(String.class);
                    break;
                default:
                    throw new ParseException(s + " wrong type", 0);
            }
        }
        return signature;
    }
}
