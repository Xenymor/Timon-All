package Puzzles.CodeWars.FindeUniqueNumber;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

// TODO: Replace examples and use TDD development by writing your own tests

public class FindUniqueTest {

    @Test
    public void sampleTestCases() {
        double precision = 0.0000000000001;
        assertEquals(1.0, FindUniqueNumber.findUnique(new double[]{0, 1, 0}), precision);
        assertEquals(2.0, FindUniqueNumber.findUnique(new double[]{1, 1, 1, 2, 1, 1}), precision);
    }
}
