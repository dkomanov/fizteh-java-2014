package ru.fizteh.fivt.students.AndrewFedorov.shell;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark methods that can be invoked from the {@link Shell}.
 * @author phoenix
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface ShellCommand {
    /**
     * Description of command invocation for manual.
     * @return
     */
    String description () default "Not documented yet";
}
