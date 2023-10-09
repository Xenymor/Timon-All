package CodeWars.FindLettersInOrder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FindLettersInOrderTest {
    @Test public void test1() {
        assertEquals(true, FindLettersInOrder.wordsMatch("test", "tst"));
        assertEquals(true, FindLettersInOrder.wordsMatch("äffchen", "äffchen"));
        assertEquals(false, FindLettersInOrder.wordsMatch("tst", "test"));
        assertEquals(false, FindLettersInOrder.wordsMatch("öalskjdfölakjsdfölkj", "uiuuiuiuiuiuiui"));
    }
}
