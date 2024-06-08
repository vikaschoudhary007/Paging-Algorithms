'use client';   
import { useState } from 'react';
import LRU from '../components/LRU';
import BlindOracle from '../components/BlindOracle';
import Combined from '../components/Combined';
import Footer from '../components/Footer';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';


export default function Home() {
    const [params, setParams] = useState({ k: 0, input: [], hSeq: [], epsilon: 0, thr: 0 });
    const [simulate, setSimulate] = useState(false);

    const handleSimulate = (newParams:any) => {
        setParams(newParams);
        setSimulate(true);
    };

    return (
        <div>
            <div className="algorithm-section">
                <LRU k={params.k} seq={params.input} />
                {/* <BlindOracle k={params.k} seq={params.input} hSeq={params.hSeq}/>
                <Combined k={params.k} seq={params.input} hSeq={params.hSeq} thr={params.thr} /> */}
            </div>
            <Footer onSimulate={handleSimulate} />

            <Stack spacing={2} direction="row">
                <Button variant="text">Text</Button>
                <Button variant="contained">Contained</Button>
                <Button variant="outlined">Outlined</Button>
            </Stack>
        </div>
    );
}


