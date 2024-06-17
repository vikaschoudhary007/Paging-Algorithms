"use client";
import {
  createContext,
  useContext,
  useState,
  ReactNode,
  Children,
} from "react";
import {
  GlobalContextType,
  DataType,
  AlgoParameters,
} from "../interface/interfaces";

// context with default value
const GlobalContext = createContext<GlobalContextType | undefined>(undefined);

// provider
const GlobalProvider = ({ children }: { children: ReactNode }) => {
  const [data, setData] = useState<AlgoParameters>({
    k: 2,
    input: "1,2,3",
    epsilon: 0.7,
    tau: 0.3,
    w: 200,
    thr: 0.3,
    lruPageFaults: 0,
    blindPageFaults: 0,
    combinedPageFaults: 0
  });
  
  return (
    <GlobalContext.Provider value={{ data, setData }}>
      {children}
    </GlobalContext.Provider>
  );
};

const useData = () => {
  const context = useContext(GlobalContext);
  if (context == undefined) {
    throw new Error("useData must be used within a GlobalProvider");
  }

  return context;
};

export { GlobalProvider, useData };
