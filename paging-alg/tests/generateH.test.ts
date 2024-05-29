import { generateH } from '../algorithms/generateH';

describe('generateH', () => {
  test('generated h-sequence length should match input sequence length', () => {
    const seq = [1, 2, 3, 4, 5, 1, 2, 3, 4, 1];
    const hSeq = generateH(seq);

    expect(hSeq.length).toBe(seq.length);
  });

  test('generated h-sequence values should be correct', () => {
    const seq = [1, 2, 3, 4, 5, 1, 2, 3, 4, 1];
    const hSeq = generateH(seq);
    const expectedHSeq = [6, 7, 8, 9, 11, 10, 11, 11, 11, 11];

    expect(hSeq).toEqual(expectedHSeq);
  });
});
