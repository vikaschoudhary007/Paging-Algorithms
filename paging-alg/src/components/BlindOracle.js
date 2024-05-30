import { useEffect, useState } from 'react';
import { useSpring, animated } from 'react-spring';

const BlindOracle = ({ k, seq, hSeq }) => {
    const [pageFaults, setPageFaults] = useState(0);
    const [cache, setCache] = useState([]);
    const [springs, setSprings] = useSpring(() => ({ from: { transform: 'scale(1)' } }));

    useEffect(() => {
        if (seq.length > 0 && hSeq.length > 0) {
            runSimulation();
        }
    }, [seq, hSeq]);

    const runSimulation = () => {
        let currentCache = [];
        let cacheH = [];
        let faults = 0;

        seq.forEach((page, i) => {
            setTimeout(() => {
                if (i < k) {
                    faults++;
                    currentCache.push(page);
                    cacheH.push(hSeq[i]);
                } else {
                    const valueIndex = currentCache.indexOf(page);
                    if (valueIndex === -1) {
                        const maxValue = Math.max(...cacheH);
                        const index = cacheH.indexOf(maxValue);
                        currentCache[index] = page;
                        cacheH[index] = hSeq[i];
                        faults++;
                    } else {
                        cacheH[valueIndex] = hSeq[i];
                    }
                }

                setCache([...currentCache]);
                setPageFaults(faults);

                // Trigger animation
                setSprings({ transform: 'scale(1.1)', from: { transform: 'scale(1)' } });
            }, i * 500); // Adjust the delay as needed for the animation speed
        });
    };

    return (
        <div className="algorithm-simulator">
            <h2>Blind Oracle</h2>
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

export default BlindOracle;
