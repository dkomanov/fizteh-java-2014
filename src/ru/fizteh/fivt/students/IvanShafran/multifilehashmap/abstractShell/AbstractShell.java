package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell;

import java.util.*;


public class AbstractShell {
    public Map<String, Command> command;
    public boolean startedInteractiveMode;

    /**
     * ***********************-Modes-*************************
     */

    public void startInteractiveMode() {
        startedInteractiveMode = true;

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print('$');
            String userRequest;
            if (in.hasNext()) {
                userRequest = in.nextLine();
                processUserRequest(userRequest);
            } else {
                /*when we close program process not by exit command*/
                System.exit(0);
            }
        }
    }

    public void startPacketMode(String userRequest) {
        startedInteractiveMode = false;

        processUserRequest(userRequest);
    }

    /**
     * ***********************-Exception Printing-*************************
     */

    public void printException(String exceptionText) {
        if (startedInteractiveMode) {
            System.out.println(exceptionText);
        } else {
            System.err.println(exceptionText);
            System.exit(1);
        }
    }

    public static void printInformation(String information) {
        System.out.println(information);
    }

    /**
     * ***********************-Command Processing-*************************
     */

    private void processUserRequest(String userRequest) {
        for (CommandText commandText : getParsedUserRequest(userRequest)) {
            processCommand(commandText);
        }
    }

    private void processCommand(CommandText commandText) {
        try {
            if (command.containsKey(commandText.commandName)) {
                command.get(commandText.commandName).execute(commandText.arguments);
            } else {
                throw new Exception("command not found");
            }
        } catch (Throwable e) {
            printException(commandText.commandName + ": " + e.getMessage());
        }
    }

    /**
     * ***********************-Parsing-*************************
     */

    public static ArrayList<CommandText> getParsedUserRequest(String userRequest) {
        String[] listOfCommandRequest = userRequest.split(";");

        ArrayList<CommandText> parsedRequest = new ArrayList<CommandText>();
        for (String command : listOfCommandRequest) {
            String[] commandWords = command.split("\\s+");
            LinkedList<String> commandWordsList = new LinkedList<String>(Arrays.asList(commandWords));

            if (commandWordsList.size() > 0 && commandWordsList.get(0).equals("")) {
                /*remove first empty word in command*/
                commandWordsList.remove(0);
            }

            /*ignore empty command*/
            if (commandWordsList.size() > 0) {
                parsedRequest.add(new CommandText(commandWordsList));
            }
        }

        return parsedRequest;
    }
}
