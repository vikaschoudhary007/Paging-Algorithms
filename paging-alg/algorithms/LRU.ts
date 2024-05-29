/**
 * Simulates the LRU cache.
 * @param k Cache size.
 * @param seq Input sequence.
 * @returns Number of page faults.
 */
export function LRU(k: number, seq: number[]): number {
    let pageFaults = 0;
    const cache: number[] = [];
    const cacheH: number[] = [];

    for (let i = 0; i < seq.length; i++) {
        if (i < k) {
            pageFaults++;
            cache.push(seq[i]);
            cacheH.push(i);
        } else {
            const valueIndex = cache.indexOf(seq[i]);
            if (valueIndex === -1) {
                const minValue = Math.min(...cacheH);
                const index = cacheH.indexOf(minValue);
                cache[index] = seq[i];
                cacheH[index] = i;
                pageFaults++;
            } else {
                cacheH[valueIndex] = i;
            }
        }
    }

    return pageFaults;
}
