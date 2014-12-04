package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import java.io.IOException;
import java.util.*;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableCreate implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() < 3) {
            throw new DatabaseException("wrong number of args for create");
        }

        String name = args.elementAt(1);

        if (dProvider.referenceToTableInfo.get(name) != null) {
            System.out.println(name + "exists");
            return;
        }

        HashMap<String, Class<?>> stringToClassMap = new HashMap<>();

        stringToClassMap.put("int", Integer.class);
        stringToClassMap.put("long", Long.class);
        stringToClassMap.put("byte", Byte.class);
        stringToClassMap.put("float", Float.class);
        stringToClassMap.put("double", Double.class);
        stringToClassMap.put("boolean", Boolean.class);
        stringToClassMap.put("String", String.class);

        String jSON = new String();
        for (int i = 2; i < args.size(); ++i) {
            jSON = jSON + args.get(i);
        }

        jSON = jSON.trim();
        if (jSON.length() < 3 || jSON.charAt(0) != '[' || jSON.charAt(jSON.length() - 1) != ']') {
            throw new DatabaseException("can't parse " + jSON.toString());
        }

        List<Class<?>> signature = new LinkedList<>();

        String[] elements = jSON.substring(1, jSON.length() - 1).split(",");

        for (int i = 0; i < elements.length; ++i) {
            String currentElement = elements[i];
            currentElement = currentElement.trim();
            if (stringToClassMap.containsKey(currentElement)) {
                signature.add(stringToClassMap.get(currentElement));
            } else {
                throw new DatabaseException("can't parse " + currentElement);
            }
        }

        try {
            dProvider.createTable(name, signature);
            System.out.println("created");
        } catch (IOException ioexc) {
            throw new DatabaseException("couldn't create " + name);
        }
    }
}
