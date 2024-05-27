package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CombinedTest {
    @Test
    public void testGenerateRandomSequence() {
        int k = 3;
        int N = 10;
        int n = 7;
        double e = 0.6;

        List<Integer> seq = Combined.generateRandomSequence(k, N, n, e);
        assertEquals(n, seq.size(), "Generated random sequence length does not match");

        // Ensure the first k elements are from 1 to k
        for (int i = 0; i < k; i++) {
            assertEquals(i + 1, seq.get(i), "First k elements are not from 1 to k");
        }
    }

    @Test
    public void testGenerateH() {
        List<Integer> seq = List.of(1, 2, 3, 4, 5, 1, 2, 3, 4, 1);
        List<Integer> hSeq = Combined.generateH(seq);

        assertEquals(seq.size(), hSeq.size(), "Generated hSeq length does not match");

        // Ensure h values are correct
        List<Integer> expectedHSeq = List.of(6, 7, 8, 9, 11, 10, 11, 11, 11, 11);
        assertEquals(expectedHSeq, hSeq, "Generated hSeq values are incorrect");
    }


    @Test
    public void testAddNoise() {
        List<Integer> hSeq = List.of(1,2,3,4,5);
        double tau = 0.6;
        int w = 2;

        List<Integer> predictedHSeq = Combined.addNoise(hSeq, tau, w);

        assertEquals(hSeq.size(), predictedHSeq.size(), "Generated predictedHSeq length does not match");

        // Ensure hi values are within the expected range
        for (int i = 0; i < hSeq.size(); i++) {
            int lowerBound = Math.max(i + 1, hSeq.get(i) - Math.floorDiv(w,2));
            int upperBound = lowerBound + w;
            assertTrue(lowerBound <= predictedHSeq.get(i) && predictedHSeq.get(i) <= upperBound,
                    "Generated predictedHSeq values are out of range");
        }
    }

    @Test
    public void testBlindOracle1() {
        int k = 3;
        int pageFaults = 6;

        List<Integer> seq = List.of(1, 2, 3, 4, 1, 2, 5, 1, 2, 3);
        List<Integer> hSeq = Combined.generateH(seq);

        int calPageFaults = Combined.blindOracle(k, seq, hSeq);
        assertEquals(pageFaults, calPageFaults, "BlindOracle page faults count is incorrect");
    }



}
