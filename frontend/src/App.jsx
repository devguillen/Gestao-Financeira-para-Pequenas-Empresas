import { useEffect, useState } from "react";
import api from "./services/api";

function App() {
  const [dados, setDados] = useState([]);

  useEffect(() => {
    api.get("/api/teste")
      .then((res) => setDados(res.data))
      .catch((err) => console.error("Erro na API:", err));
  }, []);

  return (
    <div>
      <h1>Dados do Backend</h1>
      <pre>{JSON.stringify(dados, null, 2)}</pre>
    </div>
  );
}

export default App;
