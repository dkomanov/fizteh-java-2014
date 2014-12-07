package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.Closeable;

/**
 *  Расширенный интерфейс {@link ru.fizteh.fivt.storage.structured.TableProvider}, с предоставлением метода {@link #close()}.
 */
public interface RemoteTableProvider extends TableProvider, Closeable {
}
