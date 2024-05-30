import { useEffect, useState } from 'react';
import { useSpring, animated } from 'react-spring';

const Combined = ({ k, seq, hSeq, thr }) => {
    const [pageFaults, setPageFaults] = useState(0);
    const [cache, setCache] = useState([]);
    const [springs, setSprings] = useSpring(() => ({ from: { transform: 'scale(1)' } }));

    useEffect(() => {
        if (seq.length > 0 && hSeq.length > 0) {
            runSimulation();
        }
    }, [seq, hSeq]);

    const runSimulation = () => {
        let cache = [];
        let pageFaultsCombined = 0;

        let cacheBlind = [];
        let cacheH = [];
        let pageFaultsBlind = 0;

        let cacheLRU = [];
        let cacheHLRU = [];
        let pageFaultsLRU = 0;

        let isLRU = true;

        seq.forEach((page, i) => {
            setTimeout(() => {
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

                setCache([...cache]);
                setPageFaults(pageFaultsCombined);

                // Trigger animation
                setSprings({ transform: 'scale(1.1)', from: { transform: 'scale(1)' } });
            }, i * 500); // Adjust the delay as needed for the animation speed
        });
    };

    

    return (
        <div className="algorithm-simulator">
            <h2>Combined</h2>
            <div className="cache-container">
                {cache.map((page, index) => (
                    <animated.div key={index} className="cache-page" style={springs}>
                        {page}
                    </animated.div>
                ))}
            </div>
            <div>Page Faults: {pageFaults}</div>
        </div>
    );
};

export default Combined;
