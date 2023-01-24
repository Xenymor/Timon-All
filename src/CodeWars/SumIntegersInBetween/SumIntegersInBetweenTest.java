package CodeWars.SumIntegersInBetween;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runners.JUnit4;


public class SumIntegersInBetweenTest {

    SumIntegersInBetween s = new SumIntegersInBetween();

    @Test
    public void Test1()
    {
        assertEquals(-1, s.GetSumInBetween(0, -1));
        assertEquals(1, s.GetSumInBetween(0, 1));
    }

}
