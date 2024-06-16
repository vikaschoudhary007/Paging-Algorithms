"use client";
import {createContext, useContext, useState, ReactNode, Children} from 'react';

// data struct for context -- this will contain all props
interface GlobalContextType {
    data : any;
    setData: (data: any) => void;
}

interface DataType {
    name: string;
    count?: number;
}

// context with default value
const GlobalContext = createContext<GlobalContextType | undefined>(undefined);

// provider 
const GlobalProvider = ({children} : {children : ReactNode}) => {
    const [data, setData] = useState<DataType>({name : 'vikas'});
    return(
        <GlobalContext.Provider value={{data, setData}}>
            {children}
        </GlobalContext.Provider>
    )
}

const useData = () => {
    const context = useContext(GlobalContext);
    if(context == undefined){
        throw new Error("useData must be used within a GlobalProvider");
    }

    return context;
}

export {GlobalProvider, useData};

