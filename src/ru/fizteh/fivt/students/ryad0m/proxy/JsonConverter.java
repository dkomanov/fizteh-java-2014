package ru.fizteh.fivt.students.ryad0m.proxy;

import org.json.JSONArray;

import java.util.IdentityHashMap;

public class JsonConverter {

    private static JSONArray searchCyclic(Iterable list, IdentityHashMap<Object, Object> visited) {
        visited.put(list, null);
        JSONArray result = new JSONArray();
        for (Object object : list) {
            if (object instanceof Iterable) {
                if (visited.containsKey(object)) {
                    result.put("cyclic");
                } else {
                    result.put(searchCyclic((Iterable) object, visited));
                }
            } else {
                result.put(object);
            }
        }
        visited.remove(list);
        return result;
    }

    public static Object convertObject(Object object) {
        if (object instanceof Iterable) {
            return searchCyclic((Iterable) object, new IdentityHashMap<>());
        } else {
            return object;
        }
    }
}
