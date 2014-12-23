package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

public class FromString {

    private static String replaceInnerSpaces(String s, char occur) throws Exception {
        boolean flag = false;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == occur) {
                flag = true;
            }
            if (flag && s.charAt(i) == ' ') {
                result.append('`');
            } else {
                result.append(s.charAt(i));
            }
        }
        if (!flag) {
            return null;
        }
        return result.toString();
    }

    public static Command fromString(String s) throws Exception {
        if (s.length() < 1) {
            throw new Exception("");
        }
        if (s.length() > 4 && s.substring(0, 5).equals("show ")) {
            s = s.replaceFirst(" ", "_");
        }
        if (s.length() > 6 && s.substring(0, 7).equals("create ")) {
            s = replaceInnerSpaces(s, '(');
            if (s == null) {
                throw new Exception("wrong type (create statement must have types in ())");
            }
        }
        if (s.length() > 3 && s.substring(0, 4).equals("put ")) {
            s = replaceInnerSpaces(s, '<');
            if (s == null) {
                throw new Exception("wrong type (value must be xml-serialized)");
            }
        }
        String[] tokens = s.split("\\s+", 0);
        if (MyCommands.COMMANDS.containsKey(tokens[0])) {
            Command command = MyCommands.COMMANDS.get(tokens[0]);
            if (tokens.length - 1 != command.numberOfArguments()) {
                throw new Exception("Unexpected number of arguments: " + command.numberOfArguments() + " required");
            }
            command.putArguments(tokens);
            return command;
        } else {
            throw new Exception(tokens[0] + " is unknown command");
        }
    }
}
