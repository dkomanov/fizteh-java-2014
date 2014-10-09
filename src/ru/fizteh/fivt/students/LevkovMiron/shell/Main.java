package ru.fizteh.fivt.students.LevkovMiron.shell;


import java.io.*;
/**
 * Левков Мирон, 394 группа.
 */
class Main {

    public static void main(final  String[] args) {
        if (args.length > 0) {
            new PacketFileMap().readCommands(args);
        } else {
            new StreamFileMap().readCommands();
        }
    }
}

/*
@Test
@Before
@After
@BeforeClass
#AfterClass
assertNull
assertEquals
verify(class, numberOfTimes).method_name
 */