package Puzzles.CodeWars.DigitalRoot;


import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class DRootTest {
    @Test
    public void Test1() {
        assertEquals(7, DRoot.digital_root(16));
        assertEquals(6, DRoot.digital_root(456));
        assertEquals(9, DRoot.digital_root(9));
    }
}