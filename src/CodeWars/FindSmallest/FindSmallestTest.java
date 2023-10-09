package CodeWars.FindSmallest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FindSmallestTest {
    @Test
    public void testFindSmallest() {
        assertEquals(3, new FindSmallest().findSmallest(3, 5, 7, 9));
        assertEquals(-5, new FindSmallest().findSmallest(-5, 0, 0, 0));
        assertEquals(-10, new FindSmallest().findSmallest(-10, -10, -10, -10, -10, -10, -10));
    }
}
