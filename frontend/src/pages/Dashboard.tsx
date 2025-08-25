import { useEffect, useState } from "react";
import axios from "axios";

export default function Dashboard() {
  const [data, setData] = useState<any>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await axios.get("/dashboard");
        setData(res.data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchData();
  }, []);

  if (!data) return <p className="text-center mt-10">Carregando...</p>;

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>

      <div className="grid grid-cols-3 gap-6">
        <div className="bg-green-100 p-4 rounded-lg">
          <h2 className="text-xl font-semibold">Receitas</h2>
          <p className="text-2xl font-bold text-green-700">
            R$ {data.totalIncome}
          </p>
        </div>

        <div className="bg-red-100 p-4 rounded-lg">
          <h2 className="text-xl font-semibold">Despesas</h2>
          <p className="text-2xl font-bold text-red-700">
            R$ {data.totalExpense}
          </p>
        </div>

        <div className="bg-blue-100 p-4 rounded-lg">
          <h2 className="text-xl font-semibold">Saldo</h2>
          <p className="text-2xl font-bold text-blue-700">
            R$ {data.saldo}
          </p>
        </div>
      </div>

      <div className="mt-8">
        <h2 className="text-xl font-semibold mb-4">Top 5 Despesas</h2>
        <ul className="bg-white shadow rounded-lg p-4">
          {data.topExpenses.map((exp: any, i: number) => (
            <li key={i} className="flex justify-between border-b py-2">
              <span>{exp.category}</span>
              <span className="font-bold">R$ {exp.total}</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
