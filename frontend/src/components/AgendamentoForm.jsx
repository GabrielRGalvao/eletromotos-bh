import { useEffect, useState } from "react";
import { listarClientes } from "../services/clienteService";
import { listarVeiculos } from "../services/veiculoService";
import { cadastrarAgendamento } from "../services/agendamentoService";

const valoresIniciais = {
  clienteId: "",
  veiculoId: "",
  dataAgendamento: "",
  horaAgendamento: "",
  relatoCliente: "",
};

const obrigatorios = {
  clienteId: "Selecione o cliente",
  veiculoId: "Selecione o veículo",
  dataAgendamento: "Informe a data do agendamento",
  horaAgendamento: "Informe o horário do agendamento",
  relatoCliente: "Informe o problema relatado pelo cliente",
};

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? "Não foi possível conectar ao servidor. Tente novamente."
    : erro.message;
}

function Campo({ nome, rotulo, erro, children }) {
  return (
    <div className="campo">
      <label htmlFor={`agendamento-${nome}`}>{rotulo}</label>
      {children}

      {erro && (
        <span className="erro-campo" id={`erro-agendamento-${nome}`}>
          {erro}
        </span>
      )}
    </div>
  );
}

export default function AgendamentoForm({ onSalvo }) {
  const [dados, setDados] = useState(valoresIniciais);
  const [clientes, setClientes] = useState([]);
  const [veiculos, setVeiculos] = useState([]);

  const [carregandoClientes, setCarregandoClientes] = useState(true);
  const [carregandoVeiculos, setCarregandoVeiculos] = useState(false);
  const [erroClientes, setErroClientes] = useState("");
  const [erroVeiculos, setErroVeiculos] = useState("");
  const [tentativaClientes, setTentativaClientes] = useState(0);
  const [tentativaVeiculos, setTentativaVeiculos] = useState(0);

  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");
  const [errosCampos, setErrosCampos] = useState({});
  const [sucesso, setSucesso] = useState("");

  useEffect(() => {
    const controller = new AbortController();

    listarClientes(controller.signal)
      .then((lista) => {
        if (!controller.signal.aborted) {
          setClientes(lista);
          setErroClientes("");
        }
      })
      .catch((error) => {
        if (!controller.signal.aborted) {
          setErroClientes(mensagemErro(error));
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setCarregandoClientes(false);
        }
      });

    return () => controller.abort();
  }, [tentativaClientes]);

  useEffect(() => {
    if (!dados.clienteId) {
      return undefined;
    }

    const controller = new AbortController();

    listarVeiculos(dados.clienteId, controller.signal)
      .then((lista) => {
        if (!controller.signal.aborted) {
          setVeiculos(lista);
          setErroVeiculos("");
        }
      })
      .catch((error) => {
        if (!controller.signal.aborted) {
          setErroVeiculos(mensagemErro(error));
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setCarregandoVeiculos(false);
        }
      });

    return () => controller.abort();
  }, [dados.clienteId, tentativaVeiculos]);

  function alterarCampo(evento) {
    const { name, value } = evento.target;

    setDados((atuais) => ({
      ...atuais,
      [name]: value,
      ...(name === "clienteId" ? { veiculoId: "" } : {}),
    }));

    if (name === "clienteId") {
      setVeiculos([]);
      setErroVeiculos("");
      setCarregandoVeiculos(Boolean(value));
    }

    setErro("");
    setErrosCampos({});
    setSucesso("");
  }

  function propriedades(nome) {
    return {
      id: `agendamento-${nome}`,
      name: nome,
      value: dados[nome],
      onChange: alterarCampo,
      required: true,
      "aria-invalid": Boolean(errosCampos[nome]),
      "aria-describedby": errosCampos[nome]
        ? `erro-agendamento-${nome}`
        : undefined,
    };
  }

  async function salvar(evento) {
    evento.preventDefault();

    const erros = {};

    for (const [nome, mensagem] of Object.entries(obrigatorios)) {
      if (!dados[nome].trim()) {
        erros[nome] = mensagem;
      }
    }

    if (
      dados.veiculoId &&
      !veiculos.some((veiculo) => String(veiculo.id) === dados.veiculoId)
    ) {
      erros.veiculoId = "Selecione um veículo deste cliente";
    }

    setSucesso("");
    setErrosCampos(erros);
    setErro("");

    if (Object.keys(erros).length > 0) {
      setErro("Confira os campos informados.");
      return;
    }

    setSalvando(true);

    try {
      const salvo = await cadastrarAgendamento({
        veiculoId: Number(dados.veiculoId),
        dataAgendamento: dados.dataAgendamento,
        horaAgendamento: dados.horaAgendamento,
        relatoCliente: dados.relatoCliente.trim(),
      });

      setDados(valoresIniciais);
      setVeiculos([]);
      setSucesso(
        `Agendamento #${salvo.id} cadastrado. Responsável: ${salvo.mecanicoNome}.`,
      );
      onSalvo(salvo);
    } catch (error) {
      setErro(mensagemErro(error));
      setErrosCampos(error.campos || {});
    } finally {
      setSalvando(false);
    }
  }

  return (
    <section className="painel-formulario" aria-labelledby="titulo-agendamento">
      <h2 id="titulo-agendamento">Novo agendamento</h2>

      {carregandoClientes && (
        <p role="status">Carregando clientes...</p>
      )}

      {!carregandoClientes && erroClientes && (
        <div className="aviso aviso-erro" role="alert">
          <p>{erroClientes}</p>
          <button
            type="button"
            onClick={() => {
              setCarregandoClientes(true);
              setTentativaClientes((atual) => atual + 1);
            }}
          >
            Tentar novamente
          </button>
        </div>
      )}

      {!carregandoClientes && !erroClientes && (
        <>
          {clientes.length === 0 && (
            <p className="aviso">
              Cadastre um cliente na aba Clientes para agendar.
            </p>
          )}

          <form onSubmit={salvar} noValidate>
            <fieldset disabled={salvando}>
              <legend className="legenda-formulario">
                Todos os campos são obrigatórios.
              </legend>

              <div className="grade-formulario">
                <Campo
                  nome="clienteId"
                  rotulo="Cliente"
                  erro={errosCampos.clienteId}
                >
                  <select {...propriedades("clienteId")}>
                    <option value="">Selecione o cliente</option>

                    {clientes.map((cliente) => (
                      <option key={cliente.id} value={cliente.id}>
                        {cliente.nome}
                      </option>
                    ))}
                  </select>
                </Campo>

                <Campo
                  nome="veiculoId"
                  rotulo="Veículo"
                  erro={errosCampos.veiculoId}
                >
                  <select
                    {...propriedades("veiculoId")}
                    disabled={
                      !dados.clienteId ||
                      carregandoVeiculos ||
                      Boolean(erroVeiculos)
                    }
                  >
                    <option value="">Selecione o veículo</option>

                    {veiculos.map((veiculo) => (
                      <option key={veiculo.id} value={veiculo.id}>
                        #{veiculo.id} — {veiculo.marca} {veiculo.modelo} —{" "}
                        {veiculo.cor}
                        {veiculo.placa ? ` — ${veiculo.placa}` : ""}
                      </option>
                    ))}
                  </select>

                  {carregandoVeiculos && (
                    <span role="status">Carregando veículos...</span>
                  )}

                  {erroVeiculos && (
                    <div role="alert">
                      <p className="erro-campo">{erroVeiculos}</p>
                      <button
                        type="button"
                        onClick={() => {
                          setCarregandoVeiculos(true);
                          setTentativaVeiculos((atual) => atual + 1);
                        }}
                      >
                        Tentar novamente
                      </button>
                    </div>
                  )}

                  {dados.clienteId &&
                    !carregandoVeiculos &&
                    !erroVeiculos &&
                    veiculos.length === 0 && (
                      <span>
                        Este cliente não possui veículos. Cadastre um na aba
                        Clientes.
                      </span>
                    )}
                </Campo>

                <Campo
                  nome="dataAgendamento"
                  rotulo="Data"
                  erro={errosCampos.dataAgendamento}
                >
                  <input type="date" {...propriedades("dataAgendamento")} />
                </Campo>

                <Campo
                  nome="horaAgendamento"
                  rotulo="Horário"
                  erro={errosCampos.horaAgendamento}
                >
                  <input type="time" {...propriedades("horaAgendamento")} />
                </Campo>
              </div>

              <Campo
                nome="relatoCliente"
                rotulo="Problema relatado pelo cliente"
                erro={errosCampos.relatoCliente}
              >
                <textarea
                  {...propriedades("relatoCliente")}
                  rows={4}
                  maxLength={5000}
                />
              </Campo>

              <button
                type="submit"
                disabled={
                  salvando ||
                  carregandoClientes ||
                  carregandoVeiculos ||
                  Boolean(erroClientes) ||
                  Boolean(erroVeiculos) ||
                  clientes.length === 0
                }
              >
                {salvando ? "Salvando..." : "Cadastrar agendamento"}
              </button>
            </fieldset>
          </form>
        </>
      )}

      {erro && (
        <p className="erro-campo" role="alert">
          {erro}
        </p>
      )}

      {sucesso && (
        <p className="mensagem-sucesso" role="status">
          {sucesso}
        </p>
      )}
    </section>
  );
}