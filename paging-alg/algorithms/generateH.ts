/**
 * Generates the h-sequence from the given sequence.
 * @param seq Input sequence.
 * @returns Corresponding h-sequence.
 */
export function generateH(seq: number[]): number[] {
    const hSeq: number[] = [];
    for (let i = 0; i < seq.length; i++) {
        const ele = seq[i];
        let add = seq.length + 1;
        for (let j = i + 1; j < seq.length; j++) {
            if (ele === seq[j]) {
                add = j + 1;
                break;
            }
        }
        hSeq.push(add);
    }
    return hSeq;
}
