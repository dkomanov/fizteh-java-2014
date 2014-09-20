package ru.fizteh.fivt.students.gudkov394.shell;

public class MoveFile
{
    public MoveFile(String[] current_args, CurrentDirectory cd)
    {
        if(current_args.length > 2)
        {
            System.err.println("extra arguments for mv");
            System.exit(1);
        }
        Copy copy = new Copy(current_args, cd);
        String[] args_for_rm = new String[2];
        args_for_rm[0] = "rm";
        args_for_rm[1] = current_args[1];
        Remove_derictory rm = new Remove_derictory(args_for_rm, cd);
    }
}
