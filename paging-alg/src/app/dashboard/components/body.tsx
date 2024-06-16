import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Grid from "@mui/system/Unstable_Grid";
import Button from "@mui/material/Button";
import { useData } from "../../../context/GlobalContext";

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
          <Button variant="contained">Simulate</Button>
        </Grid>

        <Grid xs={12} container>
          {JSON.stringify(data)}
        </Grid>
      </Grid>
    </>
  );
}
