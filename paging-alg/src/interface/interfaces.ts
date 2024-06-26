// data struct for context -- this will contain all props

export interface GlobalContextType {
    data : any;
    setData: (data: any) => void;
}

export interface DataType {
    name: string;
    count?: number;
}

export interface AlgoParameters {
    k: number;
    input : string;
    epsilon : number;
    tau: number;
    w: number;
    thr:number;
    lruPageFaults: number;
    blindPageFaults: number;
    combinedPageFaults: number;
}
