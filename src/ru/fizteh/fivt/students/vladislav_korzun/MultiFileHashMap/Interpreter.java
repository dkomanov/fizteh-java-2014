
package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Interpreter {
    Interpreter(String[] args, Path dbdir) {
        if (args.length > 0) {
            batchMode(args, dbdir);
        } else {
            interactiveMode(dbdir);
        }
    }
    
    void batchMode(String[] args, Path dbdir) {
        CommandExecutor cmd =  new CommandExecutor(dbdir);
        List<String[]> commands = new LinkedList<String[]>();
        Parser parser = new Parser();
        String buffer = new String();
        String[] command = null;
        for (int i = 0; i < args.length; i++) {
            buffer += (args[i] + " ");
        }
        commands = parser.mainparser(buffer);
        for (int i = 0; i < commands.size(); i++) {
            command = commands.get(i);
            cmd.executeCommands(command);
        } 
    }
    
    
   
    void interactiveMode(Path dbdir) {
        CommandExecutor cmd =  new CommandExecutor(dbdir);
        List<String[]> commands = new LinkedList<String[]>();
        Parser parser = new Parser();
        Scanner in = new Scanner(System.in);
        String[] command = null;
        String request = new String();
        do {
            System.out.print("$ ");
            request = in.nextLine(); 
            commands = parser.mainparser(request);
            for (int i = 0; i < commands.size(); i++) {
                command = commands.get(i);
                cmd.executeCommands(command);
            }
        } while (!(command[0].equals("exit")));
        in.close();
    }
}

class Parser {
    List<String[]> mainparser(String args) {
        List<String[]> answer = new LinkedList<String[]>();
        String[] arg = semicolonparser(args);
        for (int i = 0; i < arg.length; i++) {
            answer.add(spaceparser(arg[i]));
        }
        return answer;
    }
    
    String[] semicolonparser(final String arg) {
        String[] answer = null;
        String buffer = new String();
        buffer = arg.trim();    
        answer = buffer.split(";");
        return answer;
    }
    
    String[] spaceparser(final String arg) {
        String[] answer = null;
        String buffer = new String();
        buffer = arg.trim();
        answer = buffer.split(" ");
        return answer;
    }
}
