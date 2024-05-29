import { blindOracle } from '../algorithms/blindOracle';
import { generateH } from '../algorithms/generateH';

describe('blindOracle', () => {
  test('page faults count should match expected value', () => {
    const k = 3;
    const seq = [1, 2, 3, 4, 1, 2, 5, 1, 2, 3];
    const hSeq = generateH(seq);
    const expectedPageFaults = 6;

    const calculatedPageFaults = blindOracle(k, seq, hSeq);

    expect(calculatedPageFaults).toBe(expectedPageFaults);
  });
});
