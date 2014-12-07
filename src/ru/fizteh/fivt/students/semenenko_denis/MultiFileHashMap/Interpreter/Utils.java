package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap.Interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static void interpreterError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public static String[] findAll(String regexp, String text) {
        Matcher matcher = Pattern.compile(regexp).matcher(text);
        List<String> occurrences = new ArrayList<>();
        while (matcher.find()) {
            occurrences.add(matcher.group());
        }
        return occurrences.toArray(new String[occurrences.size()]);
    }
}
