import React, { useEffect, useState } from 'react';
import './App.css';
import { FaChartLine, FaDollarSign, FaLightbulb } from 'react-icons/fa';
import AnimatedBarChart from './AnimatedBarChart';
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login"; 
import Dashboard from "./pages/Dashboard";

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
    { icon: <FaChartLine size={40} color="#000000" />, title: "Controle Financeiro Completo", desc: "Nunca perca o controle do seu negócio!..." },
    { icon: <FaDollarSign size={40} color="#000000" />, title: "Pagamentos e Recebimentos Instantâneos", desc: "Receba pagamentos de clientes..." },
    { icon: <FaLightbulb size={40} color="#000000" />, title: "Relatórios e Insights Inteligentes", desc: "Transforme dados em decisões estratégicas..." }
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
                <li><Link to="/Register">Cadastre-se</Link></li>
                <li><Link to="/Dashboard">Dashboard</Link></li>
                <li><Link to="/login">Login</Link></li>
                <li><Link to="/sobre">Sobre Nós</Link></li>
              </ul>
            </nav>
          </div>
        </header>

        <Routes>
          <Route path="/" element={
            <main>
              {/* Hero Section */}
              <section className="hero">
  <div className="hero-text">
    <h2>Controle financeiro moderno e prático</h2>
    <p className="slogan">Transforme sua forma de lidar com dinheiro em poucos cliques.</p>
        <a href="#" className="btn-primary">Comece Agora</a>
  </div>
  <div className="hero-image">
    <AnimatedBarChart />
  </div>
</section>

              <div className="section-divider"></div>

              {/* Nova Seção */}
              <section className="info-section">
                <div className="info-image">
                  <img src="https://via.placeholder.com/450x300" alt="Gestão financeira" />
                </div>
                <div className="info-text">
                  <h2>Gestão financeira simples e eficiente</h2>
                  <p>
                    Tenha total controle sobre suas entradas e saídas, visualize relatórios claros
                    e melhore a saúde financeira do seu negócio com praticidade.
                  </p>
                  <a href="#" className="btn-primary">Saiba Mais</a>
                </div>
              </section>

              <div className="section-divider"></div>

              {/* Cards Section */}
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
