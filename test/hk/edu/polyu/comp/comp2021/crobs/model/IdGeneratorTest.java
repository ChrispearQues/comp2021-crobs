package hk.edu.polyu.comp.comp2021.crobs.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IdGeneratorTest {
    @Test
    void testSequentialGenerationAndPrefixTrim() {
        IdGenerator gen = new IdGenerator("  U ");
        assertEquals("U001", gen.next());
        assertEquals("U002", gen.next());
        assertEquals("U003", gen.next());
    }

    @Test
    void testCustomStartAndResetBoundary() {
        IdGenerator gen = new IdGenerator("R", 999);
        assertEquals("R999", gen.next());
        assertEquals("R1000", gen.next());

        gen.reset();
        assertEquals("R999", gen.next());
    }

    @Test
    void testInvalidPrefixAndStartThrowException() {
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new IdGenerator(null));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new IdGenerator("   "));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new IdGenerator("B", 0));
        assertThrows(CrobsException.MalformedCommandException.class,
                () -> new IdGenerator("B", -1));
    }
}
