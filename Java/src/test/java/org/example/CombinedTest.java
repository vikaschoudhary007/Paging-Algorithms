package org.example;
import org.junit.jupiter.api.Test;
import java.util.List;

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

    @Test
    public void testOverallFunctionality1() {
        int k = 5;
        int N = 20;  // N >> k
        int n = 100;
        double e = 0.5;
        double t = 0.2;
        int w = 7;

        List<Integer> seq = Combined.generateRandomSequence(k, N, n, e);
        List<Integer> hSeq = Combined.generateH(seq);
        List<Integer> noisyHSeq = Combined.addNoise(hSeq, t, w);

        int pageFaults = Combined.blindOracle(k, seq, noisyHSeq);
        System.out.println(
                "Blind Oracle pageFaults for k:" + k + " N:" + N + " n:" + n + " e:" + e + " t:" + t + " w:" + w + " = " + pageFaults
        );
    }

    @Test
    public void testCombined() {
        int k = 3;
        List<Integer> sequence = List.of(1, 2, 3, 4, 1, 2, 5, 1, 2, 3);
        List<Integer> hSeq = Combined.generateH(sequence);
        int expectedPageFaults = 10;

        int calculatedPageFaults = Combined.combinedAlg(k, sequence, hSeq, 0);

        assertEquals(expectedPageFaults, calculatedPageFaults, "Combined PageFaults are wrong");
    }

    @Test
    public void testLRU() {
        int k = 4;
        List<Integer> sequence = List.of(1, 2, 3, 4, 2, 1, 5, 6, 2, 1, 2, 3, 7, 6, 3, 2, 1, 2, 3, 6);

        int pageFaults = Combined.LRU(k, sequence);
        assertEquals(10, pageFaults, "# pageFaults for k:" + k);
    }

    @Test
    public void testOverallLRU() {
        int k = 5;
        int N = 6;  // N >> k
        int n = 100;
        double e = 0.5;
        double t = 0.2;
        int w = 7;

        List<Integer> seq = Combined.generateRandomSequence(k, N, n, e);
        List<Integer> hSeq = Combined.generateH(seq);

        int pageFaults = Combined.LRU(k, seq);
        System.out.println(
                "LRU pageFaults for k:" + k + " N:" + N + " n:" + n + " e:" + e + " t:" + t + " w:" + w + " = " + pageFaults
        );
    }

    @Test
    public void testOverallCombined() {
        int k = 5;
        int N = 6;  // N >> k
        int n = 100;
        double e = 0.5;
        double t = 0.2;
        int w = 7;
        double thr = 0;

        List<Integer> seq = Combined.generateRandomSequence(k, N, n, e);
        List<Integer> hSeq = Combined.generateH(seq);
        List<Integer> noisyHSeq = Combined.addNoise(hSeq, t, w);

        int pageFaults = Combined.combinedAlg(k, seq, noisyHSeq, thr);
        System.out.println(
                "Combined pageFaults for k:" + k + " N:" + N + " n:" + n + " e:" + e + " t:" + t + " w:" + w + " = " + pageFaults
        );
    }

}
