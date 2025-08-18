import React from 'react';
import './App.css';

const App: React.FC = () => {
  return (
    <div className="container">
      <header className="header">
        <h1>Gestão Financeira</h1>
        <p>Bem-vindo ao seu painel</p>
      </header>

      <div className="cards">
        <div className="card">
          <h2>Receitas</h2>
          <p>R$ 12.500,00</p>
        </div>
        <div className="card">
          <h2>Despesas</h2>
          <p>R$ 7.300,00</p>
        </div>
        <div className="card">
          <h2>Saldo</h2>
          <p>R$ 5.200,00</p>
        </div>
      </div>
    </div>
  );
};

export default App;
