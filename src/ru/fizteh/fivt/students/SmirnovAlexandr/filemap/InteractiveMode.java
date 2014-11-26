package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
import java.util.Scanner;

public class InteractiveMode implements CommandProcess {

    @Override
    public void process(Container commandFromString) throws ExceptionUnknownCommand {
        Scanner sc = new Scanner(System.in);
        boolean flag = false;
        do {
            System.out.print("$ ");
            for (String s : sc.nextLine().split(";\\s*")) {
                try {
                    commandFromString.getCommandByName(s).execute();
                } catch (ExceptionStopProcess e) {
                    flag = true;
                } catch (ExceptionIncorrectInput e) {
                    continue;
                }
                if (flag) {
                    break;
                }
            }
        } while (!flag);
    }
}
