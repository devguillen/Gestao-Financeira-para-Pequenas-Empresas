import React, { useEffect, useState } from 'react';
import './App.css';
import { FaChartLine, FaDollarSign, FaLightbulb } from 'react-icons/fa';
import AnimatedBarChart from './AnimatedBarChart';

function App() {
  const [hideHeader, setHideHeader] = useState(false);

  useEffect(() => {
    let lastScroll = 0;
    const handleScroll = () => {
      const currentScroll = window.scrollY;
      if (currentScroll > lastScroll && currentScroll > 50) {
        setHideHeader(true); // esconde ao scroll para baixo
      } else {
        setHideHeader(false); // mostra ao scroll para cima
      }
      lastScroll = currentScroll;
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const cards = [
    { 
      icon: <FaChartLine size={40} color="#ff6600" />,
      title: "Controle Financeiro Completo", 
      desc: "Nunca perca o controle do seu negócio! Nosso sistema permite que você gerencie todas as receitas e despesas em um só lugar, acompanhe o fluxo de caixa em tempo real e visualize gráficos detalhados que mostram exatamente para onde cada centavo está indo. Ideal para empreendedores que querem tranquilidade e segurança financeira." 
    },
    { 
      icon: <FaDollarSign size={40} color="#ff6600" />,
      title: "Pagamentos e Recebimentos Instantâneos", 
      desc: "Receba pagamentos de clientes em segundos e faça pagamentos a fornecedores sem burocracia. Automatize transferências, boletos e cobranças, garantindo que o dinheiro esteja sempre disponível quando você precisa. Fique no controle total do seu fluxo financeiro!" 
    },
    { 
      icon: <FaLightbulb size={40} color="#ff6600" />,
      title: "Relatórios e Insights Inteligentes", 
      desc: "Transforme dados em decisões estratégicas. Geração automática de relatórios completos que mostram o desempenho financeiro da sua empresa, análise de lucros e perdas, e sugestões inteligentes para melhorar sua rentabilidade. Tenha informações precisas sempre que precisar e tome decisões seguras e rápidas." 
    },
  ];

  return (
    <div className="App">
      {/* Header com degradê e scroll fade */}
      <header className={`header ${hideHeader ? 'hidden' : ''}`}>
        <div className="container">
          <h1 className="logo">FinançaPro</h1>
          <nav>
            <ul>
              <li><a href="#">Home</a></li>
              <li><a href="#">Cadastre-se</a></li>
              <li><a href="#">Login</a></li>
              <li><a href="#">Sobre Nós</a></li>
            </ul>
          </nav>
        </div>
      </header>

      <main>
        {/* Hero */}
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
        {/* Cards informativos */}
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
    </div>
  );
}

export default App;
