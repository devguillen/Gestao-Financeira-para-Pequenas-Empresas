import React, { useState, useEffect } from 'react';
import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const AnimatedBarChart = () => {
  const [data, setData] = useState({
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May'],
    datasets: [
      {
        label: 'Receita',
        data: [12, 19, 7, 14, 9],
        backgroundColor: '#ff6600',
        borderRadius: 10, // cantos arredondados das barras
      },
    ],
  });

  useEffect(() => {
    const interval = setInterval(() => {
      setData((prevData) => ({
        ...prevData,
        datasets: prevData.datasets.map(ds => ({
          ...ds,
          data: ds.data.map(() => Math.floor(Math.random() * 25) + 5)
        }))
      }));
    }, 6000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div style={{ maxWidth: '400px', margin: '0 auto' }}>
      <Bar
        data={data}
        options={{
          responsive: true,
          animation: { duration: 1000 },
          plugins: {
            legend: { display: false }, // remove a legenda
            tooltip: { enabled: false } // remove tooltip, opcional
          },
          scales: {
            y: {
              display: false, // remove números do eixo Y
              grid: { drawTicks: false, drawBorder: false, drawOnChartArea: false }
            },
            x: {
              display: false, // remove labels do eixo X
              grid: { drawTicks: false, drawBorder: false, drawOnChartArea: false }
            }
          }
        }}
      />
    </div>
  );
};

export default AnimatedBarChart;
