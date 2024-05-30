import { useState } from 'react';
import {generateH, addNoise} from "../lib/algorithms"

const Footer = ({ onSimulate }) => {
    const [k, setK] = useState(0);
    const [input, setInput] = useState('');
    const [epsilon, setEpsilon] = useState(0);
    const [tau, setTau] = useState(0);
    const [w, setW] = useState(0);
    const [thr, setThr] = useState(0);

    const handleSimulate = async () => {
        const inputArray = input.split(',').map(Number);
        let hSeq = generateH(inputArray);
        // let hSeqWithNoise = await addNoise(hSeq, tau, w);
        onSimulate({ k, input: inputArray, hSeq: hSeq, epsilon, thr });
    };

    return (
        <div className="footer">
            <div className="param-input">
                <label>K: <input type="number" value={k} onChange={(e) => setK(e.target.value)} /></label>
                <label>Input: <input type="text" value={input} onChange={(e) => setInput(e.target.value)} /></label>
                <label>Epsilon: <input type="number" value={epsilon} onChange={(e) => setEpsilon(e.target.value)} /></label>
                <label>Tau: <input type="number" value={tau} onChange={(e) => setTau(e.target.value)} /></label>
                <label>W: <input type="number" value={w} onChange={(e) => setW(e.target.value)} /></label>
                <label>Threshold: <input type="number" value={thr} onChange={(e) => setThr(e.target.value)} /></label>
            </div>
            <button onClick={handleSimulate}>Simulate</button>
        </div>
    );
};

export default Footer;
