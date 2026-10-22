import { useEffect, useState } from 'react'
import DecisaoOrcamentoForm from '../components/DecisaoOrcamentoForm'
import DiagnosticoOrcamentoForm from '../components/DiagnosticoOrcamentoForm'
import RecebimentoForm from '../components/RecebimentoForm'
import { listarAgendamentos } from '../services/agendamentoService'
import { listarOrdensServico } from '../services/ordemServicoService'

const situacoes = {
  EM_DIAGNOSTICO: 'Em diagnóstico',
  AGUARDANDO_APROVACAO: 'Aguardando aprovação',
  APROVADO: 'Aprovado',
  REPROVADO: 'Reprovado',
}

const classesSituacao = {
  EM_DIAGNOSTICO: 'situacao-em-diagnostico',
  AGUARDANDO_APROVACAO: 'situacao-aguardando-aprovacao',
  APROVADO: 'situacao-aprovado',
  REPROVADO: 'situacao-reprovado',
}

function formatarDataHora(dataHora) {
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
    timeZone: 'America/Sao_Paulo',
  }).format(new Date(dataHora))
}

function formatarTamanho(tamanho) {
  const megabytes = tamanho / (1024 * 1024)

  return `${megabytes.toLocaleString('pt-BR', {
    maximumFractionDigits: 1,
  })} MB`
}

function formatarMoeda(valor) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(valor)
}

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? 'Não foi possível conectar ao servidor. Tente novamente.'
    : erro.message
}

export default function OrdensServicoPage() {
  const [agendamentos, setAgendamentos] = useState([])
  const [ordensServico, setOrdensServico] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [atualizacao, setAtualizacao] = useState(0)

  useEffect(() => {
    const controller = new AbortController()

    async function carregar() {
      setCarregando(true)
      setErro('')

      try {
        const [listaAgendamentos, listaOrdensServico] =
          await Promise.all([
            listarAgendamentos('', controller.signal),
            listarOrdensServico(controller.signal),
          ])

        if (!controller.signal.aborted) {
          setAgendamentos(listaAgendamentos)
          setOrdensServico(listaOrdensServico)
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setErro(mensagemErro(error))
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false)
        }
      }
    }

    carregar()

    return () => controller.abort()
  }, [atualizacao])

  function atualizarLista() {
    setAtualizacao((atual) => atual + 1)
  }

  function atualizarOrdem(ordemAtualizada) {
    setOrdensServico((atuais) =>
      atuais.map((ordem) =>
        ordem.id === ordemAtualizada.id
          ? ordemAtualizada
          : ordem
      )
    )
  }

  function aoSalvarOrdem(ordemServico) {
    setOrdensServico((atuais) => [
      ordemServico,
      ...atuais,
    ])

    setAgendamentos((atuais) =>
      atuais.map((agendamento) =>
        agendamento.id === ordemServico.agendamentoId
          ? { ...agendamento, status: 'RECEBIDO' }
          : agendamento
      )
    )
  }

  const agendamentosPendentes = agendamentos.filter(
    (agendamento) => agendamento.status === 'AGENDADO'
  )

  return (
    <main className="pagina">
      <header className="cabecalho-pagina">
        <p className="etiqueta">OFICINA</p>
        <h1>Ordens de serviço</h1>
        <p>
          Receba veículos, registre diagnósticos e acompanhe os
          orçamentos.
        </p>
      </header>

      {carregando && (
        <p className="aviso" role="status">
          Carregando ordens de serviço...
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
          <RecebimentoForm
            agendamentos={agendamentosPendentes}
            onSalvo={aoSalvarOrdem}
          />

          <section aria-labelledby="titulo-lista-os">
            <div className="cabecalho-lista">
              <h2 id="titulo-lista-os">Ordens abertas</h2>

              <button
                type="button"
                className="botao-secundario"
                onClick={atualizarLista}
              >
                Atualizar lista
              </button>
            </div>

            <p className="resumo">
              {ordensServico.length} ordem(ns) de serviço aberta(s)
            </p>

            {ordensServico.length === 0 ? (
              <p className="aviso">
                Nenhuma ordem de serviço aberta.
              </p>
            ) : (
              <ul className="lista-clientes">
                {ordensServico.map((ordemServico) => (
                  <li
                    className="cartao-cliente"
                    key={ordemServico.id}
                  >
                    <h2>
                      OS #{ordemServico.id} —{' '}
                      {ordemServico.clienteNome}
                    </h2>

                    <span
                      className={`situacao-os ${
                        classesSituacao[ordemServico.status] || ''
                      }`}
                    >
                      {situacoes[ordemServico.status] ||
                        ordemServico.status}
                    </span>

                    <dl>
                      <dt>Recebido em</dt>
                      <dd>
                        {formatarDataHora(
                          ordemServico.recebidoEm
                        )}
                      </dd>

                      <dt>Veículo</dt>
                      <dd>
                        {ordemServico.veiculoMarca}{' '}
                        {ordemServico.veiculoModelo}
                      </dd>

                      <dt>Mecânico responsável</dt>
                      <dd>{ordemServico.mecanicoNome}</dd>

                      <dt>Problema relatado</dt>
                      <dd className="observacoes-cliente">
                        {ordemServico.relatoCliente}
                      </dd>

                      <dt>Carregador</dt>
                      <dd>
                        {ordemServico.carregadorRecebido
                          ? 'Recebido'
                          : 'Não recebido'}
                      </dd>

                      <dt>Acessórios</dt>
                      <dd className="observacoes-cliente">
                        {ordemServico.acessoriosRecebidos}
                      </dd>

                      {ordemServico.observacoesRecebimento && (
                        <>
                          <dt>Observações do recebimento</dt>
                          <dd className="observacoes-cliente">
                            {
                              ordemServico.observacoesRecebimento
                            }
                          </dd>
                        </>
                      )}

                      {ordemServico.diagnostico && (
                        <>
                          <dt>Diagnóstico</dt>
                          <dd className="observacoes-cliente">
                            {ordemServico.diagnostico}
                          </dd>

                          <dt>Orçamento</dt>
                          <dd className="observacoes-cliente">
                            {
                              ordemServico.descricaoOrcamento
                            }
                            {' — '}
                            {formatarMoeda(
                              ordemServico.valorOrcamento
                            )}
                          </dd>

                          <dt>Orçamento enviado em</dt>
                          <dd>
                            {formatarDataHora(
                              ordemServico.orcamentoEnviadoEm
                            )}
                          </dd>
                        </>
                      )}

                      {ordemServico.decisaoOrcamentoEm && (
                        <>
                          <dt>Decisão registrada em</dt>
                          <dd>
                            {formatarDataHora(
                              ordemServico.decisaoOrcamentoEm
                            )}
                          </dd>

                          {ordemServico.observacaoDecisao && (
                            <>
                              <dt>Observação da decisão</dt>
                              <dd className="observacoes-cliente">
                                {
                                  ordemServico.observacaoDecisao
                                }
                              </dd>
                            </>
                          )}
                        </>
                      )}

                      <dt>Vídeo de vistoria</dt>
                      <dd>
                        {formatarTamanho(
                          ordemServico.videoTamanho
                        )}
                      </dd>
                    </dl>

                    <div className="acoes-formulario acoes-cliente">
                      <a
                        className="link-botao botao-secundario"
                        href={ordemServico.videoUrl}
                      >
                        Baixar vídeo
                      </a>
                    </div>

                    {ordemServico.status ===
                      'EM_DIAGNOSTICO' && (
                      <DiagnosticoOrcamentoForm
                        ordemServico={ordemServico}
                        onSalvo={atualizarOrdem}
                      />
                    )}

                    {ordemServico.status ===
                      'AGUARDANDO_APROVACAO' && (
                      <DecisaoOrcamentoForm
                        ordemServico={ordemServico}
                        onSalvo={atualizarOrdem}
                      />
                    )}
                  </li>
                ))}
              </ul>
            )}
          </section>
        </>
      )}
    </main>
  )
}