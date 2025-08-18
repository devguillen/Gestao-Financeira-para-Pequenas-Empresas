import express from "express";

const app = express();
app.use(express.json());

app.get("/", (req, res) => {
  res.send("Backend rodando!");
});

const PORT = 3333;
app.listen(PORT, () => console.log(`Servidor rodando na porta ${PORT}`));
