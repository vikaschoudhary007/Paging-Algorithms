
export function generateRandomSequence(k: number, N: number, n: number, e: number): number[] {
    /**
     * Generates a random sequence of length n with k distinct elements and probability e of repetition.
     *
     * @param k Number of distinct elements.
     * @param N Upper bound for element values.
     * @param n Length of the sequence.
     * @param e Probability of repeating an element.
     * @returns Randomly generated sequence.
     */
    const ans: number[] = [];
    const L: number[] = [];
    const Y: number[] = [];
    
    for (let i = 0; i < k; i++) {
        ans.push(i + 1);
        L.push(i + 1);
    }

    for (let i = k; i < N; i++) {
        Y.push(i + 1);
    }

    for (let i = k; i < n; i++) {
        const prob = Math.random();
        const ele_x = L[Math.floor(Math.random() * L.length)];
        const ele_y = Y[Math.floor(Math.random() * Y.length)];

        if (prob <= e) {
            ans.push(ele_x);
        } else {
            ans.push(ele_y);
            const index_x = L.indexOf(ele_x);
            if (index_x !== -1) {
                L.splice(index_x, 1);
                L.push(ele_y);
            }
            const index_y = Y.indexOf(ele_y);
            if (index_y !== -1) {
                Y.splice(index_y, 1);
                Y.push(ele_x);
            }
        }
    }

    return ans;
}
