/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

/**
 *
 * @author shakarim
 */
public interface Table {

    String put(String key, String value);

    String get(String key);

    String[] list();

    String remove(String key);
}
