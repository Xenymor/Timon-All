package Puzzles.CodeWars.DigitalRoot;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DRootTest {
    @Test
    public void Test1() {
        assertEquals(7, DRoot.digital_root(16));
        assertEquals(6, DRoot.digital_root(456));
        assertEquals(9, DRoot.digital_root(9));
    }
}