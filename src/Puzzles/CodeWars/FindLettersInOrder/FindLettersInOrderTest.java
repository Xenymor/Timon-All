package Puzzles.CodeWars.FindLettersInOrder;

import org.junit.Test;

import static org.junit.Assert.*;

public class FindLettersInOrderTest {
    @Test public void test1() {
        assertTrue(FindLettersInOrder.wordsMatch("test", "tst"));
        assertTrue(FindLettersInOrder.wordsMatch("äffchen", "äffchen"));
        assertFalse(FindLettersInOrder.wordsMatch("tst", "test"));
        assertFalse(FindLettersInOrder.wordsMatch("öalskjdfölakjsdfölkj", "uiuuiuiuiuiuiui"));
    }
}
