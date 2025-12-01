package Puzzles.CodeWars.ParsePhoneNumber;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParseNumberTests {
    @Test
    public void tests() {
        assertEquals("(123) 456-7890", ParseNumber.createPhoneNumber((new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0})));
        assertEquals("(113) 456-7890", ParseNumber.createPhoneNumber((new int[]{1, 1, 3, 4, 5, 6, 7, 8, 9, 0})));
        assertEquals("(133) 456-5890", ParseNumber.createPhoneNumber((new int[]{1, 3, 3, 4, 5, 6, 5, 8, 9, 0})));
        assertEquals("(123) 456-7890", ParseNumber.createPhoneNumber((new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0})));
    }
}
