import { useRef, useState } from 'react'
import { receberAgendamento } from '../services/ordemServicoService'

const valoresIniciais = {
  agendamentoId: '',
  carregadorRecebido: '',
  acessoriosRecebidos: '',
  observacoesRecebimento: '',
  video: null,
}

const tiposAceitos = [
  'video/mp4',
  'video/webm',
  'video/quicktime',
]

function formatarData(data) {
  return data.split('-').reverse().join('/')
}

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? 'Não foi possível conectar ao servidor. Tente novamente.'
    : erro.message
}

function Campo({ id, rotulo, erro, children }) {
  return (
    <div className="campo">
      <label htmlFor={id}>{rotulo}</label>
      {children}

      {erro && (
        <span className="erro-campo" id={`erro-${id}`}>
          {erro}
        </span>
      )}
    </div>
  )
}

export default function RecebimentoForm({
  agendamentos,
  onSalvo,
}) {
  const [dados, setDados] = useState(valoresIniciais)
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState('')
  const [errosCampos, setErrosCampos] = useState({})
  const [sucesso, setSucesso] = useState('')
  const campoVideo = useRef(null)

  function alterarCampo(evento) {
    const { name, value } = evento.target

    setDados((atuais) => ({
      ...atuais,
      [name]: value,
    }))

    setErro('')
    setErrosCampos({})
    setSucesso('')
  }

  function alterarVideo(evento) {
    const video = evento.target.files?.[0] || null

    setDados((atuais) => ({
      ...atuais,
      video,
    }))

    setErro('')
    setErrosCampos({})
    setSucesso('')
  }

  function limparFormulario() {
    setDados(valoresIniciais)
    campoVideo.current.value = ''
  }

  async function salvar(evento) {
    evento.preventDefault()

    if (salvando) return

    const erros = {}

    if (!dados.agendamentoId) {
      erros.agendamentoId = 'Selecione o agendamento'
    }

    if (!dados.carregadorRecebido) {
      erros.carregadorRecebido =
        'Informe se o carregador foi recebido'
    }

    if (!dados.acessoriosRecebidos.trim()) {
      erros.acessoriosRecebidos =
        'Informe os acessórios ou escreva Nenhum'
    }

    if (!dados.video) {
      erros.video = 'Selecione o vídeo de vistoria'
    } else if (!tiposAceitos.includes(dados.video.type)) {
      erros.video = 'Envie um vídeo MP4, WebM ou MOV'
    } else if (dados.video.size > 100 * 1024 * 1024) {
      erros.video = 'O vídeo deve ter no máximo 100 MB'
    }

    setErrosCampos(erros)
    setErro('')
    setSucesso('')

    if (Object.keys(erros).length > 0) {
      setErro('Confira os campos informados.')
      return
    }

    setSalvando(true)

    try {
      const ordemServico = await receberAgendamento(
        Number(dados.agendamentoId),
        {
          carregadorRecebido:
            dados.carregadorRecebido === 'true',
          acessoriosRecebidos:
            dados.acessoriosRecebidos.trim(),
          observacoesRecebimento:
            dados.observacoesRecebimento.trim() || null,
        },
        dados.video
      )

      limparFormulario()
      setSucesso(
        `Ordem de serviço #${ordemServico.id} aberta com sucesso.`
      )

      onSalvo(ordemServico)
    } catch (error) {
      setErro(mensagemErro(error))
      setErrosCampos(error.campos || {})
    } finally {
      setSalvando(false)
    }
  }

  return (
    <section
      className="painel-formulario"
      aria-labelledby="titulo-recebimento"
    >
      <h2 id="titulo-recebimento">Receber veículo</h2>

      {agendamentos.length === 0 ? (
        <p className="aviso">
          Não há agendamentos pendentes de recebimento.
        </p>
      ) : (
        <form onSubmit={salvar} noValidate>
          <fieldset disabled={salvando}>
            <legend className="legenda-formulario">
              Selecione o agendamento e registre a vistoria.
            </legend>

            <Campo
              id="recebimento-agendamento"
              rotulo="Agendamento"
              erro={errosCampos.agendamentoId}
            >
              <select
                id="recebimento-agendamento"
                name="agendamentoId"
                value={dados.agendamentoId}
                onChange={alterarCampo}
                required
                aria-invalid={Boolean(errosCampos.agendamentoId)}
              >
                <option value="">
                  Selecione o agendamento
                </option>

                {agendamentos.map((agendamento) => (
                  <option
                    key={agendamento.id}
                    value={agendamento.id}
                  >
                    #{agendamento.id} — {agendamento.clienteNome}
                    {' — '}
                    {agendamento.veiculoMarca}{' '}
                    {agendamento.veiculoModelo}
                    {' — '}
                    {formatarData(agendamento.dataAgendamento)}
                  </option>
                ))}
              </select>
            </Campo>

            <fieldset className="grupo-opcoes">
              <legend>Carregador recebido?</legend>

              <label>
                <input
                  type="radio"
                  name="carregadorRecebido"
                  value="true"
                  checked={dados.carregadorRecebido === 'true'}
                  onChange={alterarCampo}
                />
                Sim
              </label>

              <label>
                <input
                  type="radio"
                  name="carregadorRecebido"
                  value="false"
                  checked={dados.carregadorRecebido === 'false'}
                  onChange={alterarCampo}
                />
                Não
              </label>

              {errosCampos.carregadorRecebido && (
                <span className="erro-campo">
                  {errosCampos.carregadorRecebido}
                </span>
              )}
            </fieldset>

            <Campo
              id="recebimento-acessorios"
              rotulo="Acessórios recebidos"
              erro={errosCampos.acessoriosRecebidos}
            >
              <textarea
                id="recebimento-acessorios"
                name="acessoriosRecebidos"
                value={dados.acessoriosRecebidos}
                onChange={alterarCampo}
                rows={3}
                maxLength={2000}
                required
                placeholder="Ex.: Retrovisor esquerdo, capacete. Escreva Nenhum se não houver."
                aria-invalid={Boolean(
                  errosCampos.acessoriosRecebidos
                )}
              />
            </Campo>

            <Campo
              id="recebimento-observacoes"
              rotulo="Observações do recebimento"
              erro={errosCampos.observacoesRecebimento}
            >
              <textarea
                id="recebimento-observacoes"
                name="observacoesRecebimento"
                value={dados.observacoesRecebimento}
                onChange={alterarCampo}
                rows={3}
                maxLength={5000}
              />
            </Campo>

            <Campo
              id="recebimento-video"
              rotulo="Vídeo de vistoria"
              erro={errosCampos.video}
            >
              <input
                ref={campoVideo}
                id="recebimento-video"
                name="video"
                type="file"
                accept="video/mp4,video/webm,video/quicktime"
                onChange={alterarVideo}
                required
                aria-invalid={Boolean(errosCampos.video)}
              />

              <span className="ajuda-campo">
                Formatos aceitos: MP4, WebM ou MOV. Máximo: 100 MB.
              </span>
            </Campo>

            <button type="submit">
              {salvando
                ? 'Registrando recebimento...'
                : 'Abrir ordem de serviço'}
            </button>
          </fieldset>
        </form>
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
  )
}