"use client"; // This is a client component 👈🏽
import { useState } from "react";

import CssBaseline from "@mui/material/CssBaseline";
import Container from "@mui/material/Container";
import Navbar from "./components/Navbar";
import Body from "./components/body";

export default function Dashboard() {
  const [k, setK] = useState(0);
  const [input, setInput] = useState("");
  const [epsilon, setEpsilon] = useState(0);
  const [tau, setTau] = useState(0);
  const [w, setW] = useState(0);
  const [thr, setThr] = useState(0);

  return (
    <>
      <Navbar />
      <CssBaseline />
      <Container>
        <Body />
      </Container>
    </>
  );
}
