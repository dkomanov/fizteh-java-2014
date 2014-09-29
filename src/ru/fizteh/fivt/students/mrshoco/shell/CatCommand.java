import java.io.File;
import java.util.Scanner;

public class CatCommand extends Command
{
    CatCommand(String[] cmd)
    {
        super(cmd);
    }
    public void run()
    {
        if(args.length < 2)
        {
            System.out.println("missing operand");
            return;
        }
        File file = new File(System.getProperty("user.dir"), args[1]);
        if(file.isDirectory())
        {
            System.out.println("Is a directory");
            return;
        }
        
        try
        {
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine())
            {
                System.out.println(sc.nextLine());
            }
            sc.close();
        } catch(Exception e) {
            System.out.println("No such file or directory");
        }
    }
}
