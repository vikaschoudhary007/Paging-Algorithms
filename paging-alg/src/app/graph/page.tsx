"use client"; // This is a client component 👈🏽
import Grid from "@mui/material/Grid";
import { useState } from "react";
import { LineChart } from "@mui/x-charts/LineChart";
import { useEffect } from "react";

export default function GraphPage() {
  const [uData, setUData] = useState<number[]>([]);

  const [pData, setPData] = useState<number[]>([]);
  const [xLables, setXLables] = useState<any[]>([]);

  useEffect(() => {
    const newxLabels = [
      "Page A",
      "Page B",
      "Page C",
      "Page D",
      "Page E",
      "Page F",
      "Page G",
    ];
    setXLables(newxLabels);

    const newUData = [4000, 3000, 2000, 2780, 1890, 2390, 3490];
    setUData(newUData);

    const newpData = [2400, 1398, 9800, 3908, 4800, 3800, 4300];
    setPData(newpData);

    setInterval(updateData, 5000);
    
  }, []);

  const updateData = ()=>{
    
    setXLables((oldValues) => [...oldValues, "Page "+Math.floor(Math.random() * 100)]);
    setUData((oldValues) => [...oldValues, Math.floor(Math.random() * 4000)]);
    setPData((oldValues) => [...oldValues, Math.floor(Math.random() * 6000)]);
      
  }

  console.log(uData)

  return (
    <>
      <Grid
        container
        spacing={2}
        justifyContent="center"
        sx={{ bgcolor: "#cfe8fc", m: 1 }}
      >
        Graph Component
        <LineChart
          width={500}
          height={300}
          series={[
            { data: pData, label: "pv" },
            { data: uData, label: "uv" },
          ]}
          xAxis={[{ scaleType: "point", data: xLables }]}
        />
      </Grid>
    </>
  );
}
