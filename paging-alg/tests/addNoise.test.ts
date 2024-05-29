import { addNoise } from '../algorithms/addNoise';

describe('addNoise', () => {
  test('generated h-hat sequence length should match input h-sequence length', () => {
    const hSeq = [1, 2, 3, 4, 5];
    const tau = 0.3;
    const w = 2;

    const hHatSeq = addNoise(hSeq, tau, w);

    expect(hHatSeq.length).toBe(hSeq.length);
  });

  test('generated h-hat sequence values should be within the expected range', () => {
    const hSeq = [1, 2, 3, 4, 5];
    const tau = 0.3;
    const w = 2;

    const hHatSeq = addNoise(hSeq, tau, w);

    for (let i = 0; i < hSeq.length; i++) {
      expect(hHatSeq[i]).toBeGreaterThanOrEqual(Math.max(i + 1, hSeq[i] - Math.floor(w / 2)));
      expect(hHatSeq[i]).toBeLessThanOrEqual(Math.max(i + 1, hSeq[i] - Math.floor(w / 2)) + w);
    }
  });
});
