import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Grid from "@mui/system/Unstable_Grid";
import Button from "@mui/material/Button";
import { useData } from "../../../context/GlobalContext";
import Lru from "./algorithms/lru";
import BlindOracle from "./algorithms/blind";
import Combined from "./algorithms/combined";
import { generateH } from "../../../../algorithms/generateH";
import { addNoise } from "../../../../algorithms/addNoise";
import { LRU } from "../../../../algorithms/LRU";
import { blindOracle } from "../../../../algorithms/blindOracle";
import { combinedAlg } from "../../../../algorithms/combinedAlgorithm";

export default function Body() {
  const { data, setData } = useData();

  const handleChange = (event: any) => {
    let value = event.target.value;
    let name = event.target.name;

    setData((prevalue: any) => {
      return {
        ...prevalue, // Spread Operator
        [name]: value,
      };
    });
  };

  const simulateFunction = () => {

    let inputArr = data.input.split(",").map(Number);

    console.log("input : " + inputArr);
    console.log("input len : " + inputArr.length);

    const hSeq = generateH(inputArr);
    const hSeqNoise = addNoise(hSeq, data.tau, data.w);

    // k, input
    const lru = LRU(data.k, inputArr);
    
    // k, input & predicted hSeq
    const blind = blindOracle(data.k, inputArr, hSeqNoise);

    // k, input, predicted hSeq & thr
    const combined = combinedAlg(data.k, inputArr, hSeqNoise, data.thr);

    setData((prevalue: any) => {
      return {
        ...prevalue, // Spread Operator
        lruPageFaults: lru,
        blindPageFaults: blind,
        combinedPageFaults: combined
      };
    });
  }

  return (
    <>
      <Grid
        container
        spacing={2}
        justifyContent="center"
        sx={{ bgcolor: "#cfe8fc", m: 1 }}
      >
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="K"
            variant="outlined"
            value={data.k}
            onChange={handleChange}
            name="k"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="input"
            variant="outlined"
            value={data.input}
            onChange={handleChange}
            name="input"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="epsilon"
            variant="outlined"
            value={data.epsilon}
            onChange={handleChange}
            name="epsilon"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="tau"
            variant="outlined"
            value={data.tau}
            onChange={handleChange}
            name="tau"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="w"
            variant="outlined"
            value={data.w}
            onChange={handleChange}
            name="w"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="thr"
            variant="outlined"
            value={data.thr}
            onChange={handleChange}
            name="thr"
          />
        </Grid>
        <Grid xs={12} container justifyContent={"center"}>
          <Button variant="contained" onClick={simulateFunction}>Simulate</Button>
        </Grid>

        <Grid xs={12} container>
          {JSON.stringify(data)}
        </Grid>


        <Grid xs={12} container justifyContent={"center"}>
          <Grid xs={4}>
            <Lru />
          </Grid>

          <Grid xs={4}>
            <BlindOracle />
          </Grid>

          <Grid xs={4}>
            <Combined />
          </Grid>
        </Grid>


      </Grid>
    </>
  );
}
