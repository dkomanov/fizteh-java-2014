package ru.fizteh.fivt.students.VasilevKirill.telnet.Storeable;

import org.json.JSONArray;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyStorable;

import java.text.ParseException;

/**
 * Created by Kirill on 16.11.2014.
 */
public class StoreableParser {
    public static Storeable stringToStoreable(String value, Class[] typeList) throws ParseException {
        if (value == null || (value.charAt(0) != '[' && value.charAt(value.length() - 1) != ']')) {
            throw new IllegalArgumentException("StoreableParser: illegal argument");
        }
        Storeable result = new MyStorable(typeList);
        JSONArray parser = new JSONArray(value);
        for (int i = 0; i < parser.length(); ++i) {
            try {
                if (parser.isNull(i)) {
                    result.setColumnAt(i, null);
                } else {
                    result.setColumnAt(i, typeList[i].cast(parser.get(i)));
                }
            } catch (ClassCastException e) {
                throw new ParseException(parser.get(i).toString(), 0);
            }
        }
        return result;
    }
}
