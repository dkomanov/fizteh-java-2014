package ru.fizteh.fivt.students.moskupols.parallel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.fizteh.fivt.students.moskupols.junit.KnownDiffTable;
import ru.fizteh.fivt.students.moskupols.multifilehashmap.MultiFileMap;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ThreadSafeMultiFileMapTableAdaptorTest {
    MultiFileMap delegatedMap;
    KnownDiffTable adaptor;

    @Before
    public void setUp() throws Exception {
        delegatedMap = mock(MultiFileMap.class);
        adaptor = new ThreadSafeMultiFileMapTableAdaptor(delegatedMap);

        when(delegatedMap.getName()).thenReturn("myname");

        when(delegatedMap.get("0")).thenReturn("a");
        when(delegatedMap.put(eq("1"), Mockito.anyString())).thenReturn("b");
        when(delegatedMap.get("1")).thenReturn("b");
        when(delegatedMap.put(eq("1"), Mockito.anyString())).thenReturn("b");

        when(delegatedMap.list()).thenReturn(Arrays.asList("0", "1"));

        when(delegatedMap.remove(Mockito.anyString())).thenReturn(null);
        when(delegatedMap.remove("0")).thenReturn("a");
        when(delegatedMap.remove("1")).thenReturn("b");

        when(delegatedMap.size()).thenReturn(2);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("myname", adaptor.getName());
    }

    @Test
    public void testGet() throws Exception {
        assertEquals(null, adaptor.get("42"));
        assertEquals("a", adaptor.get("0"));
        assertEquals("b", adaptor.get("1"));
    }

    @Test
    public void testPut() throws Exception {
        assertEquals("b", adaptor.put("1", "d"));
        assertEquals(null, adaptor.put("42", "e"));

        assertEquals("d", adaptor.get("1"));
        assertEquals("e", adaptor.get("42"));
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(adaptor.remove("42"));
        assertEquals("a", adaptor.remove("0"));
        assertNull(adaptor.get("0"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(2, adaptor.size());
    }

    @Test
    public void testList() throws Exception {
        assertEquals(Arrays.asList("0", "1"), adaptor.list());
    }

    @Test
    public void testCommit() throws Exception {
        adaptor.put("2", "c");
        adaptor.commit();

        verify(delegatedMap).put("2", "c");
        verify(delegatedMap).flush();
    }

    @Test
    public void testRollback() throws Exception {
        adaptor.remove("a");
        verify(delegatedMap).get("a");

        verifyNoMoreInteractions(delegatedMap);

        adaptor.put("2", "c");
        adaptor.rollback();
    }

    @Test
    public void testDiff() throws Exception {
        assertEquals(0, adaptor.diff());

        adaptor.put("2", "c");
        adaptor.put("3", "d");
        assertEquals(2, adaptor.diff());

        adaptor.remove("3");
        assertEquals(1, adaptor.diff());

        adaptor.put("3", "e");
        assertEquals(2, adaptor.diff());

        adaptor.rollback();
        assertEquals(0, adaptor.diff());
    }
}
