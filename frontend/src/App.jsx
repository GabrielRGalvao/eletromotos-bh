import { useEffect, useState } from "react";
import ClientesPage from "./pages/ClientesPage";
import MecanicosPage from "./pages/MecanicosPage";
import AgendamentosPage from "./pages/AgendamentosPage";
import OrdensServicoPage from "./pages/OrdensServicoPage";
import LoginPage from "./pages/LoginPage";
import { obterSessao, sair } from "./services/authService";

export default function App() {
  const [pagina, setPagina] = useState("clientes");
  const [usuario, setUsuario] = useState(undefined);

  useEffect(() => {
    obterSessao()
      .then(setUsuario)
      .catch(() => setUsuario(null));
  }, []);

  async function encerrarSessao() {
    await sair();
    setPagina("clientes");
    setUsuario(null);
  }

  if (usuario === undefined) {
    return <main className="tela-carregamento">Carregando sistema...</main>;
  }

  if (usuario === null) {
    return <LoginPage aoEntrar={setUsuario} />;
  }

  return (
    <>
      <header className="barra-superior">
        <span className="marca">EletroMotos BH</span>

        <nav className="navegacao-principal" aria-label="Navegação principal">
          <button type="button" aria-current={pagina === "clientes" ? "page" : undefined} onClick={() => setPagina("clientes")}>
            Clientes
          </button>

          <button type="button" aria-current={pagina === "mecanicos" ? "page" : undefined} onClick={() => setPagina("mecanicos")}>
            Mecânicos
          </button>

          <button type="button" aria-current={pagina === "agendamentos" ? "page" : undefined} onClick={() => setPagina("agendamentos")}>
            Agendamentos
          </button>

          <button type="button" aria-current={pagina === "ordens-servico" ? "page" : undefined} onClick={() => setPagina("ordens-servico")}>
            Ordens de serviço
          </button>
        </nav>

        <div className="area-usuario">
          <span>{usuario.nome}</span>
          <button type="button" className="botao-sair" onClick={encerrarSessao}>
            Sair
          </button>
        </div>
      </header>

      <div hidden={pagina !== "clientes"}>
        <ClientesPage />
      </div>

      <div hidden={pagina !== "mecanicos"}>
        {pagina === "mecanicos" && <MecanicosPage />}
      </div>

      {pagina === "agendamentos" && <AgendamentosPage />}
      {pagina === "ordens-servico" && <OrdensServicoPage />}
    </>
  );
}