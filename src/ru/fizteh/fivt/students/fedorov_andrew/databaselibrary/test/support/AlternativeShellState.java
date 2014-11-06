package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.ExitRequest;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Command;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.ShellState;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.SimpleCommandContainer;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by phoenix on 06.11.14.
 */
public class AlternativeShellState extends SimpleCommandContainer<AlternativeShellState>
        implements ShellState<AlternativeShellState> {
    public static final AlternativeCommand ECHO = new AlternativeCommand("echo") {
        @Override
        public void execute(AlternativeShellState state, String[] args) throws TerminalException {
            System.out.println(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        }
    };

    public static final AlternativeCommand EXIT = new AlternativeCommand("exit") {
        @Override
        public void execute(AlternativeShellState state, String[] args) throws TerminalException {
            throw new ExitRequest(0);
        }
    };

    public static final AlternativeCommand THROW_RUNTIME = new AlternativeCommand("throw_runtime") {
        @Override
        public void execute(AlternativeShellState state, String[] args) throws TerminalException {
            throw new SpontaniousRuntimeException();
        }
    };

    public static final AlternativeCommand EXECUTE_SILENTLY =
            new AlternativeCommand("execute_silently") {
                @Override
                public void execute(AlternativeShellState state, String[] args)
                        throws TerminalException {
                    TestUtils.randInt(2, 3400);
                    return;
                }
            };

    private final Map<String, Command<AlternativeShellState>> commandMap;

    private boolean makeExceptionOnPersist;
    private boolean makeExceptionOnInit;
    private boolean makeRuntimeExceptionOnCleanup;

    public AlternativeShellState() {
        commandMap = super.getCommands();
    }

    public boolean isMakeExceptionOnPersist() {
        return makeExceptionOnPersist;
    }

    public void setMakeExceptionOnPersist(boolean makeExceptionOnPersist) {
        this.makeExceptionOnPersist = makeExceptionOnPersist;
    }

    public boolean isMakeExceptionOnInit() {
        return makeExceptionOnInit;
    }

    public void setMakeExceptionOnInit(boolean makeExceptionOnInit) {
        this.makeExceptionOnInit = makeExceptionOnInit;
    }

    public boolean isMakeRuntimeExceptionOnCleanup() {
        return makeRuntimeExceptionOnCleanup;
    }

    public void setMakeRuntimeExceptionOnCleanup(boolean makeRuntimeExceptionOnCleanup) {
        this.makeRuntimeExceptionOnCleanup = makeRuntimeExceptionOnCleanup;
    }

    @Override
    public void cleanup() {
        if (makeRuntimeExceptionOnCleanup) {
            throw new SpontaniousRuntimeException();
        }
    }

    @Override
    public String getGreetingString() {
        return "$ ";
    }

    @Override
    public void init(Shell<ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support.AlternativeShellState> host)
            throws Exception {
        if (makeExceptionOnInit) {
            throw new SpontaniousException();
        }
    }

    @Override
    public void persist() throws Exception {
        if (makeExceptionOnPersist) {
            throw new SpontaniousException();
        }
    }

    @Override
    public void prepareToExit(int exitCode) throws ExitRequest {
        throw new ExitRequest(exitCode);
    }

    @Override
    public Map<String, Command<AlternativeShellState>> getCommands() {
        return commandMap;
    }
}