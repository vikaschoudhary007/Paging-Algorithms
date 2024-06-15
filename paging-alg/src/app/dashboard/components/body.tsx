import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Grid from "@mui/system/Unstable_Grid";
import Button from "@mui/material/Button";

export default function Body() {
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
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="input"
            variant="outlined"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="epsilon"
            variant="outlined"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="tau"
            variant="outlined"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="w"
            variant="outlined"
          />
        </Grid>
        <Grid xs={4}>
          <TextField
            fullWidth
            id="outlined-basic"
            label="thr"
            variant="outlined"
          />
        </Grid>
        <Grid xs={12} container justifyContent={"center"}>
          <Button variant="contained">Simulate</Button>
        </Grid>
      </Grid>
    </>
  );
}
