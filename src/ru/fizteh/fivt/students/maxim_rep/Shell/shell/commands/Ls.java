package shell.commands;

import java.io.File;

public class Ls implements ShellCommand {

	private String CurrentPath;

	public Ls(String CurrentPath) {
		this.CurrentPath = CurrentPath;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean execute() {
		File f = new File(CurrentPath);
		if (!f.exists()) {
			System.out
					.println("Current directory doesn't exists, returning to root.");
			shell.Shell.CurrentPath = "/";// System.getProperty("user.name");
		} else {
			String[] filelist = f.list();
			for (String filelist1 : filelist) {
				System.out.println(filelist1);
			}
		}
		return true;
	}

}
