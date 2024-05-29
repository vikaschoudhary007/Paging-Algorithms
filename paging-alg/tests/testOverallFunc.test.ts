import { generateRandomSequence } from '../algorithms/generateRandomSequence';
import { generateH } from '../algorithms/generateH';
import { addNoise } from '../algorithms/addNoise';
import { blindOracle } from '../algorithms/blindOracle';

describe('Overall Functionality Test', () => {
  test('should calculate page faults using blind oracle', () => {
    const k = 5;
    const N = 20; // N >> k
    const n = 100;
    const e = 0.5;
    const t = 0.2;
    const w = 7;

    // Generate random sequence
    const seq = generateRandomSequence(k, N, n, e);

    // Generate h-sequence
    const hSeq = generateH(seq);

    // Add noise to h-sequence
    const nSeq = addNoise(hSeq, t, w);

    // Calculate page faults using blind oracle
    const pageFaults = blindOracle(k, seq, nSeq);

    console.log(
      `Blind Oracle pageFaults for k:${k} N:${N} n:${n} e:${e} t:${t} w:${w} = ${pageFaults}`
    );

    // Assert page faults count is a valid number
    expect(pageFaults).toBeGreaterThanOrEqual(0);
    expect(pageFaults).toBeLessThanOrEqual(n);
  });
});
