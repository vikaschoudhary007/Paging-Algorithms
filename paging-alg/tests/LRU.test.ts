import { LRU } from '../algorithms/LRU';

describe('LRU', () => {
  test('page faults count should match expected value', () => {
    const k = 4;
    const seq = [1, 2, 3, 4, 2, 1, 5, 6, 2, 1, 2, 3, 7, 6, 3, 2, 1, 2, 3, 6];
    const expectedPageFaults = 10;

    const calculatedPageFaults = LRU(k, seq);

    expect(calculatedPageFaults).toBe(expectedPageFaults);
  });
});
