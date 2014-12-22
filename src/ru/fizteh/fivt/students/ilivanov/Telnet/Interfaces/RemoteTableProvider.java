package ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces;


import java.io.Closeable;

/**
 *  Расширенный интерфейс {@link TableProvider}, с предоставлением метода {@link #close()}.
 */
public interface RemoteTableProvider extends TableProvider, Closeable {
}
