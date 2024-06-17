import React from "react";
import { useData } from "../../../../context/GlobalContext";

export default function BlindOracle() {
    const { data, setData } = useData();

    return (
        <div>
            <h2>BlindOracle</h2>
            
            <div>Page Faults: {data.blindPageFaults} </div>
        </div>
    );
};
