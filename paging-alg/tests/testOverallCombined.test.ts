import { generateRandomSequence } from '../algorithms/generateRandomSequence';
import { generateH } from '../algorithms/generateH';
import { addNoise } from '../algorithms/addNoise';
import { combinedAlg } from '../algorithms/combinedAlgorithm';

describe('Overall Functionality Test for Combined Algorithm', () => {
  test('should calculate page faults using Combined Algorithm', () => {
    const k = 5;
    const N = 6; // N >> k
    const n = 100;
    const e = 0.5;
    const t = 0.2;
    const w = 7;
    const thr = 0;

    // Generate random sequence
    const seq = generateRandomSequence(k, N, n, e);

    // Generate h-sequence
    const hseq = generateH(seq);

    // Add noise to h-sequence
    const nseq = addNoise(hseq, t, w);

    // Calculate page faults using Combined Algorithm
    const pageFaults = combinedAlg(k, seq, nseq, thr);

    console.log(
      `Combined pageFaults for k:${k} N:${N} n:${n} e:${e} t:${t} w:${w} = ${pageFaults}`
    );

    // Assert page faults count is a valid number
    expect(pageFaults).toBeGreaterThanOrEqual(0);
    expect(pageFaults).toBeLessThanOrEqual(n);
  });
});
