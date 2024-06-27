import React from "react";
import { useData } from "../../../../context/GlobalContext";

export default function Lru(){
    const { data, setData } = useData();

    return (
        <div>
            <h2>LRU</h2>
            
            <div>Page Faults: {data.lruPageFaults}</div>
        </div>
    );
};

