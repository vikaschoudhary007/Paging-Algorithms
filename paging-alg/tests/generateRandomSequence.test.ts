
import { generateRandomSequence } from '../algorithms/generateRandomSequence';

describe('generateRandomSequence', () => {
  test('generated sequence length should match specified length', () => {
    const input = { k: 4, N: 5, n: 5, e: 1 };
    const seq = generateRandomSequence(input.k, input.N, input.n, input.e);

    expect(seq.length).toBe(input.n);
  });

  test('first k elements should be from 1 to k', () => {
    const input = { k: 4, N: 5, n: 5, e: 1 };
    const seq = generateRandomSequence(input.k, input.N, input.n, input.e);

    for (let i = 0; i < input.k; i++) {
      expect(seq[i]).toBe(i + 1);
    }
  });
});
