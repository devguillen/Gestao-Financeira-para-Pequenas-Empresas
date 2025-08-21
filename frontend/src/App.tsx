import React, { useEffect, useState } from 'react';
import './App.css';
import { FaChartLine, FaDollarSign, FaLightbulb } from 'react-icons/fa';
import AnimatedBarChart from './AnimatedBarChart';
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login"; // ✅ Import obrigatório

function App() {
  const [hideHeader, setHideHeader] = useState(false);

  useEffect(() => {
    let lastScroll = 0;
    const handleScroll = () => {
      const currentScroll = window.scrollY;
      if (currentScroll > lastScroll && currentScroll > 50) {
        setHideHeader(true);
      } else {
        setHideHeader(false);
      }
      lastScroll = currentScroll;
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const cards = [
    { icon: <FaChartLine size={40} color="#ff6600" />, title: "Controle Financeiro Completo", desc: "Nunca perca o controle do seu negócio!..." },
    { icon: <FaDollarSign size={40} color="#ff6600" />, title: "Pagamentos e Recebimentos Instantâneos", desc: "Receba pagamentos de clientes..." },
    { icon: <FaLightbulb size={40} color="#ff6600" />, title: "Relatórios e Insights Inteligentes", desc: "Transforme dados em decisões estratégicas..." }
  ];

  return (
    <Router>
      <div className="App">
        <header className={`header ${hideHeader ? 'hidden' : ''}`}>
          <div className="container">
            <h1 className="logo">FinançaPro</h1>
            <nav>
              <ul>
                <li><Link to="/">Home</Link></li>
                <li><Link to="/cadastro">Cadastre-se</Link></li>
                <li><Link to="/login">Login</Link></li>
                <li><Link to="/sobre">Sobre Nós</Link></li>
              </ul>
            </nav>
          </div>
        </header>

        <Routes>
          <Route path="/" element={
            <main>
              <section className="hero">
                <div className="hero-text">
                  <h2>Controle financeiro moderno e prático</h2>
                  <p>Organize suas finanças, acompanhe receitas e despesas e tome decisões inteligentes.</p>
                  <a href="#" className="btn-primary">Comece Agora</a>
                </div>
                <div className="hero-image">
                  <AnimatedBarChart />
                </div>
              </section>

              <div className="section-divider"></div>

              <section className="cards">
                {cards.map((card, index) => (
                  <div key={index} className="card">
                    <div className="card-icon">{card.icon}</div>
                    <h3>{card.title}</h3>
                    <p>{card.desc}</p>
                  </div>
                ))}
              </section>
            </main>
          } />
          <Route path="/cadastro" element={<Register />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
