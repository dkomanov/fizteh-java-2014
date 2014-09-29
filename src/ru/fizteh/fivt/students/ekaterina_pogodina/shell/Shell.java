package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.IOException;
import java.util.Scanner;

public final class Shell {
    private Shell() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Bash mode
        if (args.length != 0) {
            String line = "";
            for (int i = 0; i < args.length; i++) {
                line = line.concat(args[i]);
                if (i != args.length - 1) {
                    line = line.concat(" ");
                }
            }
            String[] commands = line.split(" ; ");

            for (int k = 0; k < commands.length; k++) {
                boolean flag = false;
                int index, j;
                index = 0;
                j = 0;
                String[] Args = new String[5];
                for (int i = 0; i < commands[k].length(); i++) {
                    if (commands[k].charAt(i) == ' ') {
                        Args[j] = commands[k].substring(index, i);
                        j++;
                        index = i + 1;
                    }
                }
                Args[j] = commands[k].substring(index, commands[k].length());
                if (j != 0 && Args[1].equals("-r")) {
                    flag = true;
                }
                Parser.parse(Args, flag, true, j);
            }
            /*	if (args[i].equals(" ; ")) {
					String[] s = new String[5];
					for (int j = 0; j < bashArgs.size(); j++) {
						s[j] = bashArgs.get(j);
					}
					boolean flag = false;
					if (s.length >=2 && s[1].equals("-r")) {
						 flag = true;
					}
					Parser.parse(s, flag);
					bashArgs.clear();
				}
				bashArgs.add(args[i]);
			}
			boolean flag = false;
			if (args.length >=2 && args[1].equals("-r")) {
				 flag = true;
			}
			Parser.parse(args, flag); */
        } else {
            while (true) {
                System.out.print("$ ");
                Scanner sc = new Scanner(System.in);
                String line = "";
                if (sc.hasNext()) {
                    line = sc.nextLine();
                } else {
                    System.exit(0);
                }
                String[] commands = line.split(" ; ");
                for (int k = 0; k < commands.length; k++) {
                    boolean flag = false;
                    int index, j;
                    index = 0;
                    j = 0;
                    String[] Args = new String[5];
                    for (int i = 0; i < commands[k].length(); i++)
                        if (commands[k].charAt(i) == ' ') {
                            Args[j] = commands[k].substring(index, i);
                            j++;
                            index = i + 1;
                        }
                    Args[j] = commands[k].substring(index, commands[k].length());
                    if (j != 0 && Args[1].equals("-r")) {
                        flag = true;
                    }
                    Parser.parse(Args, flag, false, j);
                }
            }
        }

    }
}
