import React, { useEffect, useState } from 'react';
import './App.css';
import { FaChartLine, FaDollarSign, FaLightbulb, FaArrowRight, FaLock, FaShieldAlt, FaFileContract, FaChevronRight } from 'react-icons/fa';
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login"; 
import Dashboard from "./pages/Dashboard";

function App() {
  const [hideHeader, setHideHeader] = useState(false);
  const [expandedCardIndex, setExpandedCardIndex] = useState<number | null>(null);

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

  const toggleCard = (index: number) => {
    setExpandedCardIndex(prevIndex => (prevIndex === index ? null : index));
  };

  const cards = [
    { 
      icon: <FaChartLine size={30} />,
      title: "Controle Financeiro Completo", 
      desc: "Nunca perca o controle do seu negócio! Cadastre receitas e despesas em segundos, acompanhe o fluxo de caixa em tempo real e tenha relatórios claros para entender a saúde da sua empresa. Nosso sistema permite também categorizar gastos, projetar cenários futuros e manter tudo organizado em um só lugar." 
    },
    { 
      icon: <FaDollarSign size={30} />, 
      title: "Pagamentos e Recebimentos Instantâneos", 
      desc: "Receba pagamentos de clientes de forma rápida e segura, sem burocracia. Gere boletos, PIX e links de pagamento automaticamente. Tenha um histórico detalhado de todos os recebimentos, saiba quem já pagou e quem ainda está em aberto, e facilite sua gestão financeira sem esforço." 
    },
    { 
      icon: <FaLightbulb size={30} />, 
      title: "Relatórios e Insights Inteligentes", 
      desc: "Transforme dados em decisões estratégicas! Nossa plataforma gera gráficos intuitivos e relatórios inteligentes para que você visualize tendências, encontre oportunidades de crescimento e evite desperdícios. É como ter um consultor financeiro disponível 24 horas por dia." 
    }
  ];

  const securityCards = [
    {
      icon: <FaLock size={30} color="var(--secondary-color)" />,
      title: "Criptografia de Ponta",
      desc: "Seus dados financeiros são protegidos com a mais alta tecnologia de criptografia, garantindo total privacidade e segurança contra acessos não autorizados."
    },
    {
      icon: <FaShieldAlt size={30} color="var(--secondary-color)" />,
      title: "Conformidade e Auditoria",
      desc: "Operamos em total conformidade com as normas do setor financeiro, e nossos sistemas são auditados regularmente para manter os mais altos padrões de segurança."
    },
    {
      icon: <FaFileContract size={30} color="var(--secondary-color)" />,
      title: "Controle de Acesso",
      desc: "Você define quem pode ver e editar as informações financeiras da sua empresa. Permissões de usuário detalhadas garantem que cada membro da equipe acesse apenas o necessário."
    }
  ];
  

  return (
    <Router>
      <div className="App">
        {/* Header - Navbar com efeito de vidro fosco (blur) */}
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
              {/* 1. Hero Section (Destaque principal) */}
              <section className="hero">
                <div className="hero-content-wrapper">
                  {/* Imagem Matheus (Esquerda) */}
                  <div className="hero-image left-image">
                    <img src="/img/Matheus.png" alt="Matheus - Gerenciamento" />
                  </div>

{/* Texto Centralizado */}
                  <div className="hero-text center-text">
                    {/* NOVO H2 COM QUEBRA DE LINHA FORÇADA */}
                    <h2>
                      <span className="orange-text">CONTROLE FINANCEIRO</span>
                      <br /> {/* FORÇA QUEBRA DE LINHA AQUI! */}
                      MODERNO E PRÁTICO
                    </h2>
                    <p className="slogan">Transforme sua forma de lidar com dinheiro em poucos cliques.</p>
                    {/* ... O botão de CTA permanece aqui ... */}
                  </div>

                  {/* Imagem Kaic (Direita) */}
                  <div className="hero-image right-image">
                    <img src="/img/Kaic.png" alt="Controle financeiro" /> 
                  </div>
                </div>
              </section>
{/* 2. Seção de Confiança (Social Proof) - Carrossel Infinito com Neon Preto */}
<section className="trust-bar">
  {/* O texto é repetido DENTRO desta div para criar o loop visual perfeito com a animação de -100% */}
  <div className="marquee-content">
    {/* Bloco 1: Conteúdo Principal */}
    A solução de Gestão Financeira com a seriedade que seu negócio precisa. &bull;&nbsp;&nbsp;
    A solução de Gestão Financeira com a seriedade que seu negócio precisa. &bull;&nbsp;&nbsp;
    A solução de Gestão Financeira com a seriedade que seu negócio precisa. &bull;&nbsp;&nbsp;
    
    {/* Bloco 2: Cópia Exata para Repetição Perfeita */}
    A solução de Gestão Financeira com a seriedade que seu negócio precisa. &bull;&nbsp;&nbsp;
    A solução de Gestão Financeira com a seriedade que seu negócio precisa. &bull;&nbsp;&nbsp;
    A solução de Gestão Financeira com a seriedade que seu negócio precisa. &bull;&nbsp;&nbsp;
  </div>
</section>

              {/* 3. Nova Seção Imagem + Texto (Gestão Simples) */}
              <section className="info-section">
                <div className="info-content">
                  <div className="info-image-container">
                    {/* Assumindo que a imagem Gestão Financeira.png está em /public/img/ */}
                    <img src="/img/Gestão Financeira.png" alt="Gestão financeira" />
                  </div>
                  <div className="info-divider-line"></div>
                  <div className="info-text">
                    <h2>Gestão financeira simples e eficiente</h2>
                    <p>
                      Tenha total controle sobre suas entradas e saídas, visualize relatórios claros, projete cenários futuros
                      e melhore a saúde financeira do seu negócio com praticidade e precisão.
                    </p>
                    <Link to="/sobre" className="btn-primary btn-secondary-style">Saiba Mais</Link>
                  </div>
                </div>
              </section>
              
              {/* Separador visual mais robusto */}
              <div className="section-separator"></div>

              {/* 4. Cards Section (Recursos) */}
              <section className="cards">
                {cards.map((card, index) => (
                  <div 
                    key={index} 
                    className={`card ${expandedCardIndex === index ? 'expanded' : ''}`}
                    onClick={() => toggleCard(index)}
                  >
                    <div className="card-header">
                      <div className="card-icon">{card.icon}</div>
                      <h3>{card.title}</h3>
                      <FaChevronRight size={20} className="card-arrow-icon" />
                    </div>
                    <p className="card-description">{card.desc}</p>
                  </div>
                ))}
              </section>

              {/* Separador visual mais robusto */}
              <div className="section-separator"></div>

              {/* 5. Seção de Segurança e Transparência */}
              <section className="security-section">
                <h2>Segurança e Transparência em Primeiro Lugar</h2>
                <div className="security-cards">
                  {securityCards.map((card, index) => (
                    <div key={index} className="security-card">
                      {card.icon}
                      <h3>{card.title}</h3>
                      <p>{card.desc}</p>
                    </div>
                  ))}
                </div>
              </section>

              {/* 6. CTA Final */}
              <section className="final-cta">
                <h2>Pronto para levar a sua gestão ao próximo nível?</h2>
                <p>Junte-se a milhares de empresas que transformaram suas finanças com FinançaPro.</p>
                <Link to="/Register" className="btn-primary" style={{ fontSize: '1.2rem', padding: '18px 45px' }}>
                  Crie sua Conta Grátis Agora!
                </Link>
              </section>
              
              {/* 7. Footer Moderno */}
              <footer>
                <p>&copy; {new Date().getFullYear()} FinançaPro. Todos os direitos reservados.</p>
              </footer>
            </main>
          } />
          <Route path="/Register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/Dashboard" element={<Dashboard />} />
          <Route path="/sobre" element={<div>Página Sobre Nós</div>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;