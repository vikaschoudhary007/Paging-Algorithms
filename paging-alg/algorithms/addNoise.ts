import * as math from 'mathjs';

/**
 * Introduces noise in the h-sequence.
 * @param hSeq Input h-sequence.
 * @param t Probability of noise addition.
 * @param w Width of the noise window.
 * @returns Modified h-sequence with noise.
 */
export function addNoise(hSeq: number[], t: number, w: number): number[] {
    const hSeq1: number[] = [];

    for (let i = 0; i < hSeq.length; i++) {
        const prob = Math.random();
        let ele = hSeq[i];

        if (prob <= t) {
            ele = Math.floor(Math.random() * w) + i + 1 + 1;
            ele = Math.min(Math.max(ele, i + 1 + 1), ele + w);
        }

        hSeq1.push(ele);
    }

    return hSeq1;
}
