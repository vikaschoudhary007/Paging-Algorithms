import { useEffect, useState } from 'react';
import { useSpring, animated } from 'react-spring';
import styles from './algorithm.css'


const LRU = ({ k, seq }) => {
    const [pageFaults, setPageFaults] = useState(0);
    const [cache, setCache] = useState([]);
    const [springs, setSprings] = useSpring(() => ({ from: { transform: 'scale(1)' } }));

    useEffect(() => {
        if (seq.length > 0) {
            runSimulation();
        }
    }, [seq]);

    const runSimulation = () => {
        let currentCache = [];
        let faults = 0;

        seq.forEach((page, i) => {
            setTimeout(() => {
                const valueIndex = currentCache.indexOf(page);
                if (valueIndex === -1) {
                    if (currentCache.length >= k) {
                        currentCache.shift();
                    }
                    currentCache.push(page);
                    faults++;
                } else {
                    currentCache.splice(valueIndex, 1);
                    currentCache.push(page);
                }

                setCache([...currentCache]);
                setPageFaults(faults);

                // Trigger animation
                setSprings({ transform: 'scale(1.1)', from: { transform: 'scale(1)' } });
            }, i * 500); // Adjust the delay as needed for the animation speed
        });
    };

    return (
        <div className={styles.algorithm_simulator}>
            <h2>LRU</h2>
            <div className={styles.cache_container}>
                {cache.map((page, index) => (
                    <animated.div key={index} className={styles.cache_page} style={springs}>
                        {page}
                    </animated.div>
                ))}
            </div>
            <div>Page Faults: {pageFaults}</div>
        </div>
    );
};

export default LRU;
