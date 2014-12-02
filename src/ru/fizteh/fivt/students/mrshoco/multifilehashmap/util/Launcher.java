package util;

import java.util.Map;

public class Launcher {
    Map<String, String> hashMap;

    public Launcher(Map<String, String> hm) {
        hashMap = hm;
    }

    public void run(String[] cmd) throws Exception {
        switch (cmd[0]) {
        case "put":
            if (cmd.length != 3) {
                throw new IndexOutOfBoundsException("Wrong number of arguments");
            }
            put(cmd[1], cmd[2]);
            break;
        case "get":
            if (cmd.length != 2) {
                throw new IndexOutOfBoundsException("Wrong number of arguments");
            }
            get(cmd[1]);
            break;
        case "remove":
            if (cmd.length != 2) {
                throw new IndexOutOfBoundsException("Wrong number of arguments");
            }
            remove(cmd[1]);
            break;
        case "list":
            if (cmd.length != 1) {
                throw new IndexOutOfBoundsException("Wrong number of arguments");
            }
            list();
            break;
        case "exit":
            if (cmd.length != 1) {
                throw new IndexOutOfBoundsException("Wrong number of arguments");
            }
            throw new ExitException("Exit");
        default:
            System.out.println("Bad command");
        }
    }

    private void put(String key, String value) {
        if (hashMap.containsKey(key)) {
            System.out.println("overwrite\nold value");
        } else {
            System.out.println("new");
        }
        hashMap.put(key, value);
    }

    private void get(String key) {
        if (hashMap.containsKey(key)) {
            System.out.println("found\n" + hashMap.get(key));
        } else {
            System.out.println("not found");
        }
    }

    private void remove(String key) {
        if (hashMap.containsKey(key)) {
            System.out.println("removed");
            hashMap.remove(key);
        } else {
            System.out.println("not found");
        }
    }

    private void list() {
        for (String key : hashMap.keySet()) {
            System.out.println(key + " ");
        }
    }
}
