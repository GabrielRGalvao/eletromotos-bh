import { useEffect, useState } from "react";
import ClientesPage from "./pages/ClientesPage";
import MecanicosPage from "./pages/MecanicosPage";
import AgendamentosPage from "./pages/AgendamentosPage";
import OrdensServicoPage from "./pages/OrdensServicoPage";
import LoginPage from "./pages/LoginPage";
import { obterSessao, sair } from "./services/authService";

const paginas = [
  {
    id: "clientes",
    titulo: "Clientes",
    descricao: "Cadastro e gerenciamento de clientes",
    icone: "♙",
  },
  {
    id: "mecanicos",
    titulo: "Mecânicos",
    descricao: "Gerenciamento da equipe técnica",
    icone: "⚙",
  },
  {
    id: "agendamentos",
    titulo: "Agendamentos",
    descricao: "Controle de agendamentos da oficina",
    icone: "▦",
  },
  {
    id: "ordens-servico",
    titulo: "Ordens de serviço",
    descricao: "Acompanhamento dos serviços da oficina",
    icone: "▤",
  },
];

export default function App() {
  const [pagina, setPagina] = useState("clientes");
  const [usuario, setUsuario] = useState(undefined);
  const [menuAberto, setMenuAberto] = useState(false);
  const [saindo, setSaindo] = useState(false);

  useEffect(() => {
    obterSessao()
      .then(setUsuario)
      .catch(() => setUsuario(null));
  }, []);

  async function encerrarSessao() {
    if (saindo) return;

    setSaindo(true);

    try {
      await sair();
      setPagina("clientes");
      setUsuario(null);
    } finally {
      setSaindo(false);
    }
  }

  function navegar(destino) {
    setPagina(destino);
    setMenuAberto(false);
  }

  const paginaAtual = paginas.find((item) => item.id === pagina);

  if (usuario === undefined) {
    return (
      <main className="tela-carregamento">
        <div className="carregamento-conteudo">
          <span className="carregamento-icone">⚡</span>
          <p>Carregando EletroMotos BH...</p>
        </div>
      </main>
    );
  }

  if (usuario === null) {
    return <LoginPage aoEntrar={setUsuario} />;
  }

  return (
    <div className="estrutura-sistema">
      {menuAberto && (
        <button
          type="button"
          className="fundo-menu-mobile"
          aria-label="Fechar menu"
          onClick={() => setMenuAberto(false)}
        />
      )}

      <aside
        className={`menu-lateral ${menuAberto ? "menu-lateral-aberto" : ""}`}
      >
        <div className="marca-sistema">
          <div className="marca-icone">⚡</div>

          <div className="marca-textos">
            <strong>EletroMotos BH</strong>
            <span>GESTÃO DA OFICINA</span>
          </div>
        </div>

        <div className="menu-separador" />

        <nav className="menu-navegacao" aria-label="Menu principal">
          <span className="menu-legenda">MENU PRINCIPAL</span>

          {paginas.map((item) => (
            <button
              key={item.id}
              type="button"
              className={`item-navegacao ${
                pagina === item.id ? "item-navegacao-ativo" : ""
              }`}
              aria-current={pagina === item.id ? "page" : undefined}
              onClick={() => navegar(item.id)}
            >
              <span className="item-navegacao-icone" aria-hidden="true">
                {item.icone}
              </span>

              <span>{item.titulo}</span>
            </button>
          ))}
        </nav>

        <div className="menu-rodape">
          <div className="indicador-sistema">
            <span className="indicador-online" />
            <span>Sistema operacional</span>
          </div>

          <span className="versao-sistema">EletroMotos BH</span>
        </div>
      </aside>

      <div className="area-principal">
        <header className="cabecalho-sistema">
          <div className="cabecalho-esquerda">
            <button
              type="button"
              className="botao-menu-mobile"
              aria-label="Abrir menu"
              aria-expanded={menuAberto}
              onClick={() => setMenuAberto(true)}
            >
              ☰
            </button>

            <div className="titulo-cabecalho">
              <span className="caminho-sistema">ELETROMOTOS BH / SISTEMA</span>
              <h1>{paginaAtual?.titulo}</h1>
            </div>
          </div>

          <div className="usuario-cabecalho">
            <div className="usuario-identificacao">
              <div className="usuario-avatar" aria-hidden="true">
                {usuario.nome?.trim()?.charAt(0)?.toUpperCase() || "U"}
              </div>

              <div className="usuario-dados">
                <strong>{usuario.nome}</strong>
                <span>Usuário do sistema</span>
              </div>
            </div>

            <button
              type="button"
              className="botao-sair"
              onClick={encerrarSessao}
              disabled={saindo}
            >
              {saindo ? "Saindo..." : "Sair"}
            </button>
          </div>
        </header>

        <main className="conteudo-principal">
          <div className="introducao-pagina">
            <div>
              <span className="etiqueta-pagina">GESTÃO DA OFICINA</span>
              <p>{paginaAtual?.descricao}</p>
            </div>
          </div>

          <div className="conteudo-paginas">
            <div hidden={pagina !== "clientes"}>
              <ClientesPage />
            </div>

            <div hidden={pagina !== "mecanicos"}>
              {pagina === "mecanicos" && <MecanicosPage />}
            </div>

            {pagina === "agendamentos" && <AgendamentosPage />}

            {pagina === "ordens-servico" && <OrdensServicoPage />}
          </div>
        </main>
      </div>
    </div>
  );
}
