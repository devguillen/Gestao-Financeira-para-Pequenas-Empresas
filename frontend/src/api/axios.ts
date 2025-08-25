// src/api/axios.ts
import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:5000/api", // ajuste conforme seu backend
  headers: {
    "Content-Type": "application/json",
  },
});
