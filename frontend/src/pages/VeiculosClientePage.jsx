import { useEffect, useState } from "react";
import {
  cadastrarVeiculo,
  listarVeiculos,
  excluirVeiculo,
} from "../services/veiculoService";

const tipos = [
  "Moto Elétrica",
  "Scooter Elétrica",
  "Patinete",
  "Bicicleta Elétrica",
  "Autopropelido",
  "Triciclo Elétrico",
  "Patinete Bimotor",
  "Outros",
];

const campos = [
  { nome: "marca", rotulo: "Marca", limite: 100, obrigatorio: true },
  { nome: "modelo", rotulo: "Modelo", limite: 100, obrigatorio: true },
  { nome: "cor", rotulo: "Cor", limite: 50, obrigatorio: true },
  { nome: "placa", rotulo: "Placa — opcional", limite: 20, obrigatorio: false },
];

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? "Não foi possível conectar. Confira se o backend está funcionando."
    : erro.message;
}

function normalizarTexto(valor) {
  return String(valor ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();
}

export default function VeiculosClientePage({ cliente, onVoltar }) {
  const [veiculos, setVeiculos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erroLista, setErroLista] = useState("");
  const [tentativa, setTentativa] = useState(0);
  const [salvando, setSalvando] = useState(false);
  const [erroCadastro, setErroCadastro] = useState("");
  const [errosCampos, setErrosCampos] = useState({});
  const [sucesso, setSucesso] = useState("");
  const [excluindoId, setExcluindoId] = useState(null);
  const [erroExclusao, setErroExclusao] = useState("");
  const [sucessoExclusao, setSucessoExclusao] = useState("");
  const [pesquisa, setPesquisa] = useState("");

  const termoPesquisa = normalizarTexto(pesquisa);

  const veiculosFiltrados = veiculos.filter((veiculo) => {
    if (!termoPesquisa) return true;

    return [veiculo.marca, veiculo.modelo, veiculo.placa, veiculo.tipo].some(
      (campo) => normalizarTexto(campo).includes(termoPesquisa),
    );
  });

  useEffect(() => {
    const controller = new AbortController();

    async function carregar() {
      setCarregando(true);
      setErroLista("");

      try {
        const dados = await listarVeiculos(cliente.id, controller.signal);

        if (!controller.signal.aborted) {
          setVeiculos(dados);
        }
      } catch (erro) {
        if (!controller.signal.aborted) {
          setErroLista(mensagemErro(erro));
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false);
        }
      }
    }

    carregar();
    return () => controller.abort();
  }, [cliente.id, tentativa]);

  async function salvar(evento) {
    evento.preventDefault();
    if (salvando) return;

    const formulario = evento.currentTarget;
    const dados = new FormData(formulario);

    const veiculo = {
      clienteId: cliente.id,
      tipo: dados.get("tipo"),
      marca: dados.get("marca").trim(),
      modelo: dados.get("modelo").trim(),
      cor: dados.get("cor").trim(),
      placa: dados.get("placa").trim() || null,
    };

    setSalvando(true);
    setErroCadastro("");
    setErrosCampos({});
    setSucesso("");
    setErroExclusao("");
    setSucessoExclusao("");

    try {
      const salvo = await cadastrarVeiculo(veiculo);
      setVeiculos((atuais) => [...atuais, salvo]);
      formulario.reset();
      setSucesso("Veículo cadastrado com sucesso.");
    } catch (erro) {
      setErroCadastro(mensagemErro(erro));
      setErrosCampos(erro.campos || {});
    } finally {
      setSalvando(false);
    }
  }

  async function excluir(veiculo) {
    const confirmado = window.confirm(
      `Deseja realmente excluir o veículo ${veiculo.marca} ${veiculo.modelo}?`,
    );

    if (!confirmado || excluindoId !== null) return;

    setExcluindoId(veiculo.id);
    setErroExclusao("");
    setSucessoExclusao("");
    setSucesso("");

    try {
      await excluirVeiculo(veiculo.id);

      setVeiculos((atuais) => atuais.filter((item) => item.id !== veiculo.id));

      setSucessoExclusao("Veículo excluído com sucesso.");
    } catch (erro) {
      setErroExclusao(mensagemErro(erro));
    } finally {
      setExcluindoId(null);
    }
  }

  return (
    <main className="pagina">
      <button
        type="button"
        className="botao-secundario"
        disabled={salvando || excluindoId !== null}
        onClick={onVoltar}
      >
        Voltar para clientes
      </button>

      <header className="cabecalho-pagina cabecalho-veiculos">
        <p className="etiqueta">CADASTROS</p>
        <h1>Veículos de {cliente.nome}</h1>
        <p>Telefone: {cliente.telefone}</p>
      </header>

      {carregando && (
        <p className="aviso" role="status">
          Carregando veículos...
        </p>
      )}

      {!carregando && erroLista && (
        <div className="aviso aviso-erro" role="alert">
          <p>{erroLista}</p>
          <button
            type="button"
            onClick={() => setTentativa((atual) => atual + 1)}
          >
            Tentar novamente
          </button>
        </div>
      )}

      {!carregando && !erroLista && (
        <>
          <section
            className="painel-formulario"
            aria-labelledby="titulo-novo-veiculo"
          >
            <h2 id="titulo-novo-veiculo">Novo veículo</h2>
            <p>Tipo, marca, modelo e cor são obrigatórios.</p>

            <form onSubmit={salvar} noValidate>
              <fieldset disabled={salvando || excluindoId !== null}>
                <legend className="legenda-formulario">Dados do veículo</legend>

                <div className="grade-formulario">
                  <div className="campo">
                    <label htmlFor="veiculo-tipo">Tipo</label>
                    <select
                      id="veiculo-tipo"
                      name="tipo"
                      defaultValue=""
                      required
                      aria-invalid={Boolean(errosCampos.tipo)}
                      aria-describedby={
                        errosCampos.tipo ? "erro-veiculo-tipo" : undefined
                      }
                    >
                      <option value="" disabled>
                        Selecione o tipo
                      </option>
                      {tipos.map((tipo) => (
                        <option key={tipo} value={tipo}>
                          {tipo}
                        </option>
                      ))}
                    </select>

                    {errosCampos.tipo && (
                      <span className="erro-campo" id="erro-veiculo-tipo">
                        {errosCampos.tipo}
                      </span>
                    )}
                  </div>

                  {campos.map((campo) => (
                    <div className="campo" key={campo.nome}>
                      <label htmlFor={`veiculo-${campo.nome}`}>
                        {campo.rotulo}
                      </label>
                      <input
                        id={`veiculo-${campo.nome}`}
                        name={campo.nome}
                        type="text"
                        maxLength={campo.limite}
                        required={campo.obrigatorio}
                        aria-invalid={Boolean(errosCampos[campo.nome])}
                        aria-describedby={
                          errosCampos[campo.nome]
                            ? `erro-veiculo-${campo.nome}`
                            : undefined
                        }
                      />
                      {errosCampos[campo.nome] && (
                        <span
                          className="erro-campo"
                          id={`erro-veiculo-${campo.nome}`}
                        >
                          {errosCampos[campo.nome]}
                        </span>
                      )}
                    </div>
                  ))}
                </div>

                <button type="submit">
                  {salvando ? "Salvando..." : "Salvar veículo"}
                </button>
              </fieldset>

              {erroCadastro && (
                <p className="erro-campo" role="alert">
                  {erroCadastro}
                </p>
              )}
              {sucesso && (
                <p className="mensagem-sucesso" role="status">
                  {sucesso}
                </p>
              )}
            </form>
          </section>

          {erroExclusao && (
            <div className="aviso aviso-erro" role="alert">
              {erroExclusao}
            </div>
          )}

          {sucessoExclusao && (
            <p className="mensagem-sucesso" role="status">
              {sucessoExclusao}
            </p>
          )}

          {veiculos.length > 0 && (
            <section className="campo" aria-label="Pesquisar veículos">
              <label htmlFor="pesquisa-veiculos">Pesquisar veículos</label>
              <input
                id="pesquisa-veiculos"
                type="search"
                value={pesquisa}
                onChange={(evento) => setPesquisa(evento.target.value)}
                placeholder="Digite marca, modelo, placa ou tipo"
              />
            </section>
          )}

          <p className="resumo">
            {pesquisa.trim()
              ? `${veiculosFiltrados.length} de ${veiculos.length} veículo(s) encontrado(s)`
              : `${veiculos.length} veículo(s) cadastrado(s)`}
          </p>

          {veiculos.length === 0 ? (
            <p className="aviso">Este cliente ainda não possui veículos.</p>
          ) : veiculosFiltrados.length === 0 ? (
            <p className="aviso">
              Nenhum veículo encontrado para essa pesquisa.
            </p>
          ) : (
            <ul className="lista-clientes">
              {veiculosFiltrados.map((veiculo) => (
                <li className="cartao-cliente" key={veiculo.id}>
                  <h2>
                    {veiculo.marca} {veiculo.modelo}
                  </h2>
                  <dl>
                    <dt>Tipo</dt>
                    <dd>{veiculo.tipo}</dd>
                    <dt>Cor</dt>
                    <dd>{veiculo.cor}</dd>
                    <dt>Placa</dt>
                    <dd>{veiculo.placa || "Não informada"}</dd>
                  </dl>

                  <button
                    type="button"
                    className="botao-secundario"
                    disabled={excluindoId !== null || salvando}
                    onClick={() => excluir(veiculo)}
                  >
                    {excluindoId === veiculo.id
                      ? "Excluindo..."
                      : "Excluir veículo"}
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
