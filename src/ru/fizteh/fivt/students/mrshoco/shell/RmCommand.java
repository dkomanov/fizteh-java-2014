import java.io.File;

public class RmCommand extends Command
{
    RmCommand(String[] cmd)
    {
        super(cmd);
    }
    public void run() throws Exception
    {
        if(args.length < 2 || (args[1] == "-r" && args.length < 3))
        {
            throw new Exception("rm: missing operand");
        }
        File file;

        if(args[1].equals("-r"))
        {
            file = new File(System.getProperty("user.dir"), args[2]);
        }
        else
        {
            file = new File(System.getProperty("user.dir"), args[1]);
            if(file.isDirectory())
            {
                throw new Exception("rm: Is a directory");
            }
        }
        if(!file.exists())
        {
            System.out.println(file.getAbsolutePath());
            throw new Exception("rm: No such file or directory");
        }
        try
        {
            if(!remove(file))
            {
                throw new Exception("rm: cannot delete file");
            }
        } catch(Exception e) {
            throw new Exception("rm: cannot delete file");
        }
    }

    private boolean remove(File folder) throws Exception
    {
        File[] files = folder.listFiles();
        if(files != null)
        {
            for(File file : files)
            {
                if(file.isDirectory())
                {
                    if(!remove(file))
                        return false;
                }
                else
                {
                    file.delete();
                }
            }
        }
        return folder.delete();
    }
}
