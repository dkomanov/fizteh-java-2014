package ru.fizteh.fivt.students.LebedevAleksey.junit.tests;

import org.junit.Test;
import ru.fizteh.fivt.students.LebedevAleksey.junit.AdditionalAssert;

import java.util.ArrayList;
import java.util.List;

public class AdditionalAssertTest {
    @Test
    public void testAssertListEqualsCorrect() throws Exception {
        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();
        first.add("a");
        second.add("a");
        first.add("b");
        second.add("b");
        AdditionalAssert.assertListEquals(first, second);
    }

//    @Test @Ignore
//    public void testAssertListEqualsIncorrect() throws Exception {
//        List<String> first = new ArrayList<>();
//        List<String> second = new ArrayList<>();
//        first.add("a");
//        second.add("a");
//        first.add("b");
//        second.add("b");
//        first.add("c");
//        AdditionalAssert.assertListEquals(first, second);//fail
//    }
//
//    @Test @Ignore
//         public void testAssertListEqualsIncorrect2() throws Exception {
//        List<String> first = new ArrayList<>();
//        List<String> second = new ArrayList<>();
//        first.add("a");
//        second.add("a");
//        first.add("b");
//        second.add("b");
//        second.add("c");
//        AdditionalAssert.assertListEquals(first, second);//fail
//    }
//
//
//    @Test @Ignore
//    public void testAssertListEqualsIncorrect3() throws Exception {
//        List<String> first = new ArrayList<>();
//        List<String> second = new ArrayList<>();
//        first.add("a");
//        second.add("a");
//        first.add("b");
//        second.add("b");
//        second.add("c");
//        first.add("d");
//        AdditionalAssert.assertListEquals(first, second);//fail
//    }
//
//    @Test @Ignore
//    public void testAssertListEqualsDifferentTipes() throws Exception {
//        List<String> first = new ArrayList<>();
//        List<int[]> second = new ArrayList<>();
//        first.add("a");
//        second.add(new int[0]);
//        AdditionalAssert.assertListEquals(first, second);//fail
//    }

    @Test
    public void testAssertArrayAndListEquals() throws Exception {
        List<String> first = new ArrayList<>();
        first.add("a");
        first.add("b");
        AdditionalAssert.assertArrayAndListEquals(new String[]{"a", "b"}, first);
    }
}
