package ru.fizteh.fivt.students.AndrewFedorov.filemap;

import static ru.fizteh.fivt.students.AndrewFedorov.filemap.Utility.handleError;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Commands {
    static class Exit implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 1) {
		handleError("Wrong arguments number; invocation: exit");
	    }
	    shell.writeDatabaseMap();
	    System.exit(0);
	}

	@Override
	public String getInfo() {
	    return "saves all data to file and stops interpretation";
	}

	@Override
	public String getInvocation() {
	    return null;
	}
    }

    static class Get implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError("Wrong arguments number; invocation: get <key>");
	    }
	    String key = args[1];

	    String value = shell.getActiveFilemap().get(key);

	    if (value == null) {
		System.out.println("not found");
	    } else {
		System.out.println("found");
		System.out.println(value);
	    }
	}

	@Override
	public String getInfo() {
	    return "obtains value by the key";
	}

	@Override
	public String getInvocation() {
	    return "<key>";
	}
    }

    static class Help implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    Iterator<Entry<String, Class<?>>> commands = shell.listCommands();

	    System.out
		    .println("FileMap is an utility that lets you work with simple database");
	    System.out
		    .println(String
			    .format("You can set db file to work with using environment variable '%s'",
				    Shell.DB_FILE_PROPERTY_NAME));

	    while (commands.hasNext()) {
		Entry<String, Class<?>> cmdEntry = commands.next();
		String cmdName = cmdEntry.getKey();
		Class<?> cmd = cmdEntry.getValue();

		try {
		    Command cmdInstance = (Command) cmd.newInstance();

		    System.out.println(String.format("\t%s%s\t%s", cmdName,
			    cmdInstance.getInvocation() == null ? ""
				    : (" " + cmdInstance.getInvocation()),
			    cmdInstance.getInfo()));
		} catch (Exception exc) {
		    Utility.handleError(exc,
			    String.format("%s: %s: cannot get method info",
				    args[0], cmdName), true);
		}
	    }
	}

	@Override
	public String getInfo() {
	    return "prints out description of shell commands";
	}

	@Override
	public String getInvocation() {
	    return null;
	}
    }

    static class List implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 1) {
		handleError("Wrong arguments number; invocation: list");
	    }
	    Set<String> keySet = shell.getActiveFilemap().keySet();
	    StringBuilder sb = new StringBuilder();

	    boolean comma = false;

	    for (String key : keySet) {
		sb.append(comma ? ", " : "").append(key);
		comma = true;
	    }

	    System.out.println(sb);
	}

	@Override
	public String getInfo() {
	    return "prints all keys stored in the map";
	}

	@Override
	public String getInvocation() {
	    return null;
	}
    }

    static class Put implements Command {

	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 3) {
		handleError("Wrong arugments number; invocation: put <key> <value>");
	    }
	    String key = args[1];
	    String value = args[2];

	    String oldValue = shell.getActiveFilemap().put(key, value);

	    if (oldValue == null) {
		System.out.println("new");
	    } else {
		System.out.println("overwrite");
		System.out.println("old " + oldValue);
	    }
	}

	@Override
	public String getInfo() {
	    return "assigns new value to the key";
	}

	@Override
	public String getInvocation() {
	    return "<key> <value>";
	}
    }

    static class Remove implements Command {
	@Override
	public void execute(Shell shell, String[] args) throws HandledException {
	    if (args.length != 2) {
		handleError("Wrong arguments number; invocation: remove <key>");
	    }

	    String key = args[1];
	    String oldValue = shell.getActiveFilemap().remove(key);

	    if (oldValue == null) {
		System.out.println("not found");
	    } else {
		System.out.println("removed");
	    }
	}

	@Override
	public String getInfo() {
	    return "removes value by the key";
	}

	@Override
	public String getInvocation() {
	    return "<key>";
	}
    }
}
