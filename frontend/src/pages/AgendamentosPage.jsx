import { useEffect, useState } from 'react'
import AgendamentoForm from '../components/AgendamentoForm'
import { listarAgendamentos } from '../services/agendamentoService'

const situacoes = {
  AGENDADO: 'Agendado',
  RECEBIDO: 'Recebido',
  CANCELADO: 'Cancelado',
}

function formatarData(data) {
  return data.split('-').reverse().join('/')
}

export default function AgendamentosPage() {
  const [dataFiltro, setDataFiltro] = useState('')
  const [agendamentos, setAgendamentos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [atualizacao, setAtualizacao] = useState(0)

  useEffect(() => {
    const controller = new AbortController()

    async function carregar() {
      setCarregando(true)
      setErro('')

      try {
        const lista = await listarAgendamentos(
          dataFiltro,
          controller.signal
        )

        if (!controller.signal.aborted) {
          setAgendamentos(lista)
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setErro(
            error instanceof TypeError
              ? 'Não foi possível conectar ao servidor. Tente novamente.'
              : error.message
          )
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false)
        }
      }
    }

    carregar()

    return () => controller.abort()
  }, [dataFiltro, atualizacao])

  function atualizarLista() {
    setCarregando(true)
    setAtualizacao((atual) => atual + 1)
  }

  function aoCadastrar(salvo) {
    setDataFiltro(salvo.dataAgendamento)
    atualizarLista()
  }

  return (
    <main className="pagina">
      <header className="cabecalho-pagina">
        <p className="etiqueta">ATENDIMENTO</p>
        <h1>Agendamentos</h1>
        <p>
          Agende o atendimento e consulte a agenda da oficina.
        </p>
      </header>

      <AgendamentoForm onSalvo={aoCadastrar} />

      <section
        className="painel-formulario"
        aria-labelledby="titulo-consulta-agenda"
      >
        <h2 id="titulo-consulta-agenda">Consultar agenda</h2>

        <div className="campo">
          <label htmlFor="data-filtro-agenda">
            Data — deixe em branco para ver todos
          </label>

          <input
            id="data-filtro-agenda"
            type="date"
            value={dataFiltro}
            onChange={(evento) => {
              const novaData = evento.target.value

              if (novaData !== dataFiltro) {
                setDataFiltro(novaData)
                setCarregando(true)
              }
            }}
          />
        </div>

        <button
          type="button"
          className="botao-secundario"
          disabled={carregando}
          onClick={atualizarLista}
        >
          Atualizar lista
        </button>
      </section>

      {carregando && (
        <p className="aviso" role="status">
          Carregando agendamentos...
        </p>
      )}

      {!carregando && erro && (
        <div className="aviso aviso-erro" role="alert">
          <p>{erro}</p>

          <button type="button" onClick={atualizarLista}>
            Tentar novamente
          </button>
        </div>
      )}

      {!carregando && !erro && (
        <>
          <p className="resumo">
            {agendamentos.length} agendamento(s)
            {dataFiltro
              ? ` em ${formatarData(dataFiltro)}`
              : ' em todas as datas'}
          </p>

          {agendamentos.length === 0 ? (
            <p className="aviso">
              Nenhum agendamento encontrado.
            </p>
          ) : (
            <ul className="lista-clientes">
              {agendamentos.map((item) => (
                <li className="cartao-cliente" key={item.id}>
                  <h2>
                    Agendamento #{item.id} — {item.clienteNome}
                  </h2>

                  <dl>
                    <dt>Data e horário</dt>
                    <dd>
                      {formatarData(item.dataAgendamento)}
                      {' às '}
                      {item.horaAgendamento.slice(0, 5)}
                    </dd>

                    <dt>Situação</dt>
                    <dd>
                      {situacoes[item.status] || item.status}
                    </dd>

                    <dt>Telefone</dt>
                    <dd>{item.clienteTelefone}</dd>

                    <dt>Veículo</dt>
                    <dd>
                      {item.veiculoTipo} — {item.veiculoMarca}
                      {' '}
                      {item.veiculoModelo}
                    </dd>

                    <dt>Cor / placa</dt>
                    <dd>
                      {item.veiculoCor}
                      {' / '}
                      {item.veiculoPlaca || 'Placa não informada'}
                    </dd>

                    <dt>Mecânico</dt>
                    <dd>{item.mecanicoNome}</dd>

                    <dt>Problema relatado</dt>
                    <dd className="observacoes-cliente">
                      {item.relatoCliente}
                    </dd>
                  </dl>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </main>
  )
}