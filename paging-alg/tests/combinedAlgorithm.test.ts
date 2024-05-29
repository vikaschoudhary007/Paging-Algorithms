import { generateH } from '../algorithms/generateH';
import { combinedAlg } from '../algorithms/combinedAlgorithm';

describe('Combined Algorithm', () => {
  test('should calculate the correct number of page faults', () => {
    const k = 3;
    const seq = [1, 2, 3, 4, 1, 2, 5, 1, 2, 3];
    const h_seq = generateH(seq);
    const expectedPageFaults = 10;

    const calculatedPageFaults = combinedAlg(k, seq, h_seq, 0);

    expect(calculatedPageFaults).toBe(expectedPageFaults);
  });
});
