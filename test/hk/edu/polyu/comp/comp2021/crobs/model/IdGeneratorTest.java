package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdGeneratorTest {

    @Test
    void generatesZeroPaddedSequentialIds() {
        IdGenerator generator = new IdGenerator("U");

        assertEquals("U001", generator.next());
        assertEquals("U002", generator.next());
        assertEquals("U003", generator.next());
    }

    @Test
    void canStartAtAnotherNumber() {
        IdGenerator generator = new IdGenerator("B", 9);

        assertEquals("B009", generator.next());
        assertEquals("B010", generator.next());
    }
}
