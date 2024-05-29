/**
 * Simulates the Combined cache.
 * @param k Cache size.
 * @param seq Input sequence.
 * @param hSeq Corresponding h-sequence.
 * @param thr Threshold for switch.
 * @returns Number of page faults.
 */
export function combinedAlg(k: number, seq: number[], hSeq: number[], thr: number): number {
    let cache: number[] = [];
    let pageFaultsCombined = 0;

    let cacheBlind: number[] = [];
    let cacheH: number[] = [];
    let pageFaultsBlind = 0;

    let cacheLRU: number[] = [];
    let cacheHLRU: number[] = [];
    let pageFaultsLRU = 0;

    let isLRU = true;

    for (let i = 0; i < seq.length; i++) {
        if (i < k) {
            pageFaultsBlind++;
            pageFaultsLRU++;
            cacheBlind.push(seq[i]);
            cacheLRU.push(seq[i]);
            cacheH.push(hSeq[i]);
            cacheHLRU.push(i);
            cache.push(i);
            pageFaultsCombined++;
        } else {
            if (isLRU && pageFaultsLRU > (1 + thr) * pageFaultsBlind) {
                pageFaultsCombined += k;
                isLRU = false;
                cache = [...cacheBlind];
            }

            if (!isLRU && pageFaultsBlind > (1 + thr) * pageFaultsLRU) {
                pageFaultsCombined += k;
                isLRU = true;
                cache = [...cacheLRU];
            }

            let valueIndexBlind = cacheBlind.indexOf(seq[i]);
            if (valueIndexBlind === -1) {
                const maxValue = Math.max(...cacheH);
                const index = cacheH.indexOf(maxValue);
                cacheBlind[index] = seq[i];
                cacheH[index] = hSeq[i];
                pageFaultsBlind++;

                if (!isLRU) {
                    cache = [...cacheBlind];
                    pageFaultsCombined++;
                }
            } else {
                cacheH[valueIndexBlind] = hSeq[i];
            }

            let valueIndexLRU = cacheLRU.indexOf(seq[i]);
            if (valueIndexLRU === -1) {
                const minValue = Math.min(...cacheHLRU);
                const index = cacheHLRU.indexOf(minValue);
                cacheLRU[index] = seq[i];
                cacheHLRU[index] = i;
                pageFaultsLRU++;
                if (isLRU) {
                    cache = [...cacheLRU];
                    pageFaultsCombined++;
                }
            } else {
                cacheHLRU[valueIndexLRU] = i;
            }
        }
    }

    return pageFaultsCombined;
}
