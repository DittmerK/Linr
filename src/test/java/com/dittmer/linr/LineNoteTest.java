package com.dittmer.linr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LineNoteTest 
{

    @Test
    public void testAddToTable() 
    {
        LineNote testLN = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line", " ", 1, false);
        String[] actual = testLN.addToTable();
        String[] expected = new String[]{"1", "" + 2, "Called Line", "Test Line", " ", "" + 1};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testEquals() 
    {
        LineNote testLN = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line", " ", 1, false);
        LineNote testLN2 = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line", " ", 1, false);
        LineNote testLN3 = new LineNote("TESTTES", "1", 2, "Called Line", "Test Line", " ", 1, false);
        LineNote testLN4 = new LineNote("TESTTEST", "2", 2, "Called Line", "Test Line", " ", 1, false);
        LineNote testLN5 = new LineNote("TESTTEST", "1", 3, "Called Line", "Test Line", " ", 1, false);
        LineNote testLN6 = new LineNote("TESTTEST", "1", 2, "Paraphrased", "Test Line", " ", 1, false);
        LineNote testLN7 = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line2", " ", 1, false);
        LineNote testLN8 = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line", "No", 1, false);
        LineNote testLN9 = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line", " ", 2, false);
        LineNote testLN10 = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line", " ", 1, true);
        LineNote testLN2Null = new LineNote(null, "1", 2, "Called Line", "Test Line", " ", 1, false);
        LineNote testLN3Null = new LineNote("TESTTEST", null, 2, "Called Line", "Test Line", " ", 1, false);
        LineNote testLN5Null = new LineNote("TESTTEST", "1", 2, null, "Test Line", " ", 1, false);
        LineNote testLN6Null = new LineNote("TESTTEST", "1", 2, "Paraphrased", "Test Line", " ", 1, false);
        LineNote testLN7Null = new LineNote("TESTTEST", "1", 2, "Called Line", null, " ", 1, false);
        LineNote testLN8Null = new LineNote("TESTTEST", "1", 2, "Called Line", "Test Line", null, 1, false);
        assertTrue(testLN.equals(testLN2));
        assertFalse(testLN.equals(null));
        assertFalse(testLN.equals(testLN3));
        assertFalse(testLN.equals(testLN4));
        assertFalse(testLN.equals(testLN5));
        assertFalse(testLN.equals(testLN6));
        assertFalse(testLN.equals(testLN7));
        assertFalse(testLN.equals(testLN8));
        assertFalse(testLN.equals(testLN9));
        assertFalse(testLN.equals(testLN10));
        assertFalse(testLN2Null.equals(testLN));
        assertFalse(testLN3Null.equals(testLN));
        assertFalse(testLN5Null.equals(testLN));
        assertFalse(testLN6Null.equals(testLN));
        assertFalse(testLN7Null.equals(testLN));
        assertFalse(testLN8Null.equals(testLN));
        assertTrue(testLN.equals(testLN));
    }

    @Test
    public void testHashCode() 
    {
        LineNote testLn = new LineNote(null, null, 0, null, null, null, 0, false);
        assertEquals(-1807453226, testLn.hashCode());
    }

    @Test
    public void testParse() {
        LineNote expectedLN = new LineNote("Alexa", "1", 2, "Called Line", "Test Line", " ", 1, false);
        assertEquals(expectedLN, LineNote.parse("Alexa;1;2;Called Line;Test Line; ;1;FALSE"));
    }

    @Test
    public void testToString() 
    {
        LineNote testLn = new LineNote("Alexa", "1", 2, "Called Line", "Test Line", " ", 1, false);
        assertEquals("Alexa,1,2,Called Line,Test Line, ,1,FALSE", testLn.toString());
    }
}
