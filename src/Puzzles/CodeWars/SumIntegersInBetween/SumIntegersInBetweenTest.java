package Puzzles.CodeWars.SumIntegersInBetween;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SumIntegersInBetweenTest {

    final SumIntegersInBetween s = new SumIntegersInBetween();

    @Test
    public void Test1()
    {
        assertEquals(-1, s.GetSumInBetween(0, -1));
        assertEquals(1, s.GetSumInBetween(0, 1));
    }

}
