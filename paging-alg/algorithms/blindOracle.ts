/**
 * Simulates the blind oracle cache.
 * @param k Cache size.
 * @param seq Input sequence.
 * @param hSeq Corresponding h-sequence.
 * @returns Number of page faults.
 */
export function blindOracle(k: number, seq: number[], hSeq: number[]): number {
    let cache: number[] = [];
    let cacheH: number[] = [];
    let pageFaults = 0;

    for (let i = 0; i < seq.length; i++) {
        if (i < k) {
            pageFaults++;
            cache.push(seq[i]);
            cacheH.push(hSeq[i]);
        } else {
            const valueIndex = cache.indexOf(seq[i]);
            if (valueIndex === -1) {
                const maxValue = Math.max(...cacheH);
                const index = cacheH.indexOf(maxValue);
                cache[index] = seq[i];
                cacheH[index] = hSeq[i];
                pageFaults++;
            } else {
                cacheH[valueIndex] = hSeq[i];
            }
        }
    }

    return pageFaults;
}
