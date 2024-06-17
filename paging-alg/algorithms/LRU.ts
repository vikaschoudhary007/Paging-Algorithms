/**
 * Simulates the LRU cache.
 * @param k Cache size.
 * @param seq Input sequence.
 * @returns Number of page faults.
 */
export function LRU(k: number, seq: number[]): number {
    let pageFaults: number = 0;
    let cache: number[] = [];
    let cacheH: number[] = [];

    for (let i: number = 0; i < seq.length; i++) {
        if (i < k) {
            pageFaults += 1;
            cache.push(seq[i]);
            cacheH.push(i);
        } else {
            let valueIndex: number = cache.indexOf(seq[i]);
            if (valueIndex === -1) {
                let minValue: number = Math.min(...cacheH);
                let index: number = cacheH.indexOf(minValue);
                cache[index] = seq[i];
                cacheH[index] = i;
                pageFaults += 1;
            } else {
                cacheH[valueIndex] = i;
            }
        }
    }
    return pageFaults;
}
