/**
 * Simulates the LRU cache.
 * @param k Cache size.
 * @param seq Input sequence.
 * @returns Number of page faults.
 */
export function lru(k, seq) {
    let cache = [];
    let pageFaults = 0;

    for (let i = 0; i < seq.length; i++) {
        const valueIndex = cache.indexOf(seq[i]);
        if (valueIndex === -1) {
            if (cache.length >= k) {
                cache.shift(); // Remove least recently used element
            }
            cache.push(seq[i]);
            pageFaults++;
        } else {
            cache.splice(valueIndex, 1); // Remove element from its current position
            cache.push(seq[i]); // Push it to the end to mark it as recently used
        }
    }

    return pageFaults;
}


/**
 * Generates the h-sequence from the given sequence.
 * @param {number[]} seq Input sequence.
 * @returns {number[]} Corresponding h-sequence.
 */
export function generateH(seq) {
    const hSeq = [];
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

/**
 * Introduces noise in the h-sequence.
 * @param {number[]} hSeq Input h-sequence.
 * @param {number} t Probability of noise addition.
 * @param {number} w Width of the noise window.
 * @returns {number[]} Modified h-sequence with noise.
 */
export async function addNoise(hSeq, t, w) {
    const hSeq1 = [];

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



export function blindOracle(k, seq, hSeq) {
    let cache = [];
    let cacheH = [];
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


/**
 * Simulates the Combined cache.
 * @param {number} k Cache size.
 * @param {number[]} seq Input sequence.
 * @param {number[]} hSeq Corresponding h-sequence.
 * @param {number} thr Threshold for switch.
 * @returns {number} Number of page faults.
 */
export function combinedAlg(k, seq, hSeq, thr) {
    let cache = [];
    let pageFaultsCombined = 0;

    let cacheBlind = [];
    let cacheH = [];
    let pageFaultsBlind = 0;

    let cacheLRU = [];
    let cacheHLRU = [];
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
