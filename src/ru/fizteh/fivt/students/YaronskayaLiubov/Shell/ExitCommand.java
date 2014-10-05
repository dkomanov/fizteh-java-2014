package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

public class ExitCommand extends Command {
	void execute(String[] args) {
		System.out.println("Program ended with exit code: 0");
		System.exit(0);
	}

	ExitCommand() {
		name = "exit";
	}
}
