package com.dixonnet.dropquotes;

import junit.framework.TestCase;

public class EntropyScorerTest extends TestCase {

    public void testComputeScore() throws Exception {
        EntropyScorer e = new EntropyScorer();

        // Make sure unknown words score 33% (for terminals)
        assertEquals(33033.0, e.computeScore("mitochondrial bivalve"));

        // Make sure known words with a known pair score 100s
        assertEquals(100100.0, e.computeScore("common word"));

        // Make sure unknown words still score 33% - this verifies that counts
        // are not remembered between calls
        assertEquals(33033.0, e.computeScore("mitochondrial bivalve"));
    }
}