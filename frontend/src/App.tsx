import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [showHeader, setShowHeader] = useState(true);
  const [lastScroll, setLastScroll] = useState(0);

  useEffect(() => {
    const handleScroll = () => {
      const currentScroll = window.scrollY;
      if (currentScroll > lastScroll) {
        setShowHeader(false); // scroll para baixo -> esconde
      } else {
        setShowHeader(true); // scroll para cima -> mostra
      }
      setLastScroll(currentScroll);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [lastScroll]);

  const cards = [
    { title: "Controle Financeiro", desc: "Centralize receitas e despesas de forma fácil." },
    { title: "Bling Conta Digital", desc: "Receba e pague automaticamente sem complicação." },
    { title: "Relatórios Detalhados", desc: "Tome decisões inteligentes com dados claros." },
  ];

  const feedbacks = [
    { name: "João Silva", text: "O sistema facilitou muito a gestão da minha empresa!" },
    { name: "Maria Oliveira", text: "Economizei horas de trabalho com os relatórios automáticos." },
    { name: "Carlos Pereira", text: "Excelente ferramenta, recomendo para qualquer pequeno negócio." },
  ];

  return (
    <div className="App">
      <header className={`header ${showHeader ? 'visible' : 'hidden'}`}>
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
            <img 
              src="https://images.unsplash.com/photo-1605902711622-cfb43c443d30?auto=format&fit=crop&w=800&q=80" 
              alt="Finance"
            />
          </div>
        </section>

        {/* Cards informativos */}
        <section className="cards">
          {cards.map((card, index) => (
            <div key={index} className="card">
              <h3>{card.title}</h3>
              <p>{card.desc}</p>
            </div>
          ))}
        </section>

        {/* Feedbacks */}
        <section className="feedbacks">
          <h2>O que nossos clientes dizem</h2>
          <div className="feedback-list">
            {feedbacks.map((fb, index) => (
              <div key={index} className="feedback-card">
                <p>"{fb.text}"</p>
                <h4>- {fb.name}</h4>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

export default App;
