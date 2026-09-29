import { useEffect, useState } from "react";
import { listarClientes } from "../services/clienteService";
import ClienteForm from "../components/ClienteForm";

export default function ClientesPage() {
  const [clientes, setClientes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [clienteEmEdicao, setClienteEmEdicao] = useState(null);
  const [salvando, setSalvando] = useState(false);
  const [sucesso, setSucesso] = useState("");

  useEffect(() => {
    const controller = new AbortController();

    async function carregarClientes() {
      try {
        const dados = await listarClientes(controller.signal);

        if (!controller.signal.aborted) {
          setClientes(dados);
        }
      } catch {
        if (!controller.signal.aborted) {
          setErro("Não foi possível carregar os clientes. Tente novamente.");
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false);
        }
      }
    }

    carregarClientes();

    return () => controller.abort();
  }, []);

  function salvarClienteNaLista(clienteSalvo) {
    setClientes((atuais) => {
      const jaExiste = atuais.some((cliente) => cliente.id === clienteSalvo.id);

      if (jaExiste) {
        return atuais.map((cliente) =>
          cliente.id === clienteSalvo.id ? clienteSalvo : cliente,
        );
      }

      return [...atuais, clienteSalvo];
    });

    setSucesso(
      clienteEmEdicao
        ? "Cliente atualizado com sucesso."
        : "Cliente cadastrado com sucesso.",
    );

    setClienteEmEdicao(null);
  }

  function iniciarEdicao(cliente) {
    setSucesso("");
    setClienteEmEdicao(cliente);
    document.getElementById("titulo-cadastro")?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  }

  function cancelarEdicao() {
    setClienteEmEdicao(null);
    setSucesso("");
  }

  return (
    <main className="pagina">
      <header className="cabecalho-pagina">
        <p className="etiqueta">CADASTROS</p>
        <h1>Clientes</h1>
        <p>Consulte os clientes da oficina e seus contatos.</p>
      </header>

      {carregando && (
        <p className="aviso" role="status">
          Carregando clientes...
        </p>
      )}

      {erro && (
        <div className="aviso aviso-erro" role="alert">
          <p>{erro}</p>
          <button type="button" onClick={() => window.location.reload()}>
            Tentar novamente
          </button>
        </div>
      )}

      {!carregando && !erro && (
        <>
          <ClienteForm
            key={clienteEmEdicao?.id ?? "novo"}
            cliente={clienteEmEdicao}
            onSalvo={salvarClienteNaLista}
            onCancelar={cancelarEdicao}
            salvando={salvando}
            onSalvandoChange={setSalvando}
          />

          {sucesso && (
            <p className="mensagem-sucesso" role="status">
              {sucesso}
            </p>
          )}

          <p className="resumo">{clientes.length} cliente(s) cadastrado(s)</p>

          {clientes.length === 0 ? (
            <p className="aviso">Nenhum cliente cadastrado ainda.</p>
          ) : (
            <ul className="lista-clientes">
              {clientes.map((cliente) => (
                <li className="cartao-cliente" key={cliente.id}>
                  <h2>{cliente.nome}</h2>
                  <dl>
                    <dt>Telefone</dt>
                    <dd>{cliente.telefone}</dd>

                    <dt>Email</dt>
                    <dd>{cliente.email || "Não informado"}</dd>

                    {cliente.observacoes && (
                      <>
                        <dt>Observações</dt>
                        <dd className="observacoes-cliente">
                          {cliente.observacoes}
                        </dd>
                      </>
                    )}
                  </dl>
                  <button
                    type="button"
                    className="botao-secundario"
                    disabled={salvando}
                    onClick={() => iniciarEdicao(cliente)}
                    aria-label={`Editar ${cliente.nome}`}
                  >
                    Editar
                  </button>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </main>
  );
}
