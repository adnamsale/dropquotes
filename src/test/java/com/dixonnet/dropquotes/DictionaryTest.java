package com.dixonnet.dropquotes;

import junit.framework.TestCase;

public class DictionaryTest extends TestCase {

    public void testIsPrefix() throws Exception {
        Dictionary dict = new Dictionary(Dictionary.DictSize.s);

        // Make sure we read the last word
        assertTrue(dict.isPrefix("zyuganov".toCharArray()));

        // Make sure we remove apostrophes - they're ignored in dropquotes puzzles
        assertTrue(dict.isPrefix("dont".toCharArray())); // This is don't in the dictionary file

        // The large dictionary has some proper-cased words. Make sure they're found
        dict = new Dictionary(Dictionary.DictSize.l);
        assertTrue(dict.isPrefix("florence".toCharArray()));
    }
}