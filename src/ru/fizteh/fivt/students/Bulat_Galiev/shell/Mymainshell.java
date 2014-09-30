import java.io.*;
import java.util.StringTokenizer;

public class Mymainshell {
    public static void main(String[] args) throws IOException {
	String input = "";

	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	input = in.readLine();
	if (input.equals("java Shell")) {
	    do {
		System.out.print("$ ");

		input = in.readLine();
		final StringTokenizer tok = new StringTokenizer(input, ";");
		while (tok.hasMoreTokens()) {
		    final StringTokenizer argtok = new StringTokenizer(
			    tok.nextToken(), " ");

		    String[] arg = new String[4];
		    arg[1] = "";
		    arg[2] = "";
		    arg[3] = "";
		    int i = 0;
		    while (argtok.hasMoreTokens()) {
			arg[i++] = argtok.nextToken();
		    }
		    if (arg[0].equals("cp")) {
			try {
			    Shellfunc.cp(arg[1], arg[2], arg[3]);
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} // [-r]
		    } else if (arg[0].equals("cd")) {
			Shellfunc.cd(arg[1]);
		    } else if (arg[0].equals("mkdir")) {
			Shellfunc.mkdir(arg[1]);
		    } else if (arg[0].equals("pwd")) {
			Shellfunc.pwd();
		    } else if (arg[0].equals("rm")) {
			try {
			    Shellfunc.rm(arg[1], arg[2]);
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} // [-r]
		    } else if (arg[0].equals("mv")) {
			Shellfunc.mv(arg[1], arg[2]);
		    } else if (arg[0].equals("ls")) {
			Shellfunc.ls();
		    } else if (arg[0].equals("cat")) {
			try {
			    Shellfunc.cat(arg[1]);
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
		    } else if (arg[0].equals("exit")) {
		    } else {
			System.out.println("Please enter proper command");
		    }

		}
	    } while (!input.equals("exit"));
	} else {
	    input = input.substring(11);
	    final StringTokenizer tok = new StringTokenizer(input, ";");
	    while (tok.hasMoreTokens()) {
		int i = 0;
		String[] arg = new String[4];
		arg[1] = "";
		arg[2] = "";
		arg[3] = "";
		StringTokenizer argtok = new StringTokenizer(tok.nextToken(),
			" ");
		while (argtok.hasMoreTokens()) {
		    arg[i++] = argtok.nextToken();
		}
		if (arg[0].equals("cp")) {
		    try {
			Shellfunc.cp(arg[1], arg[2], arg[3]);
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    } // [-r]
		} else if (arg[0].equals("cd")) {
		    Shellfunc.cd(arg[1]);
		} else if (arg[0].equals("mkdir")) {
		    Shellfunc.mkdir(arg[1]);
		} else if (arg[0].equals("pwd")) {
		    Shellfunc.pwd();
		} else if (arg[0].equals("rm")) {
		    try {
			Shellfunc.rm(arg[1], arg[2]);
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    } // [-r]
		} else if (arg[0].equals("mv")) {
		    Shellfunc.mv(arg[1], arg[2]);
		} else if (arg[0].equals("ls")) {
		    Shellfunc.ls();
		} else if (arg[0].equals("cat")) {
		    try {
			Shellfunc.cat(arg[1]);
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }
	}
    }
}
