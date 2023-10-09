package CodeWars.SimplePigLatin;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runners.JUnit4;
public class PigLatinTests {
    @Test
    public void FixedTests() {
        assertEquals("igPay atinlay siay oolcay", SimplePigLatin.pigIt("Pig latin is cool"));
        assertEquals("hisTay siay ymay tringsay", SimplePigLatin.pigIt("This is my string"));
    }
}