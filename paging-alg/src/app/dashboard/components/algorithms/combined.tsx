import React from "react";
import { useData } from "../../../../context/GlobalContext";

export default function Combined() {
    const { data, setData } = useData();

    return (
        <div>
            <h2>Combined</h2>
            
            <div>Page Faults: {data.combinedPageFaults}</div>
        </div>
    );
};

