package ru.fizteh.fivt.students.LevkovMiron.Proxy;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Мирон on 10.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        CStoreable storeable = new CStoreable(Arrays.asList(1, "2", "avacd", null, 1.5));
        System.out.println(storeable.toString());
        XMLParser parser = new XMLParser();
        System.out.println(parser.parse(new Object[]{storeable, 2, null, "adsads", new ArrayList<>()}));
    }
}
