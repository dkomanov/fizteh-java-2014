package junit.strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.util.TableHandler;

// I know how to do it nice, but it's too long to rewrite. Sorry.

public class RusakTable implements Table {
    private TableHandler handler;

    public RusakTable(TableHandler getHandler) {
        handler = getHandler;
    }

    @Override
    public String getName() {
        return handler.getName();
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        try {
            String resString = handler.work(new String[]{"get", key });
            if (resString.equals("not found")) {
                return null;
            } else {
                return resString.split("\n")[1];
            }
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        try {
            String oldVal = get(key);
            handler.work(new String[] {"put", key, value });
            return oldVal;
        } catch (IOException | IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        try {
            String oldVal = get(key);
            handler.work(new String[] {"remove", key });
            return oldVal;
        } catch (IOException | IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public int size() {
        return handler.getCount();
    }

    @Override
    public int commit() {
        try {
            return Integer.parseInt(handler.commit());
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public int rollback() {
        try {
            return Integer.parseInt(handler.rollback());
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public List<String> list() {
        Set<String> resSet = handler.getList();
        List<String> resList = new ArrayList<String>();
        resList.addAll(resSet);
        return resList;
    }

}
