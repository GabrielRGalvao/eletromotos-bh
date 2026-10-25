import { useState } from 'react'
import {
  concluirOrdemServico,
  iniciarExecucao,
} from '../services/ordemServicoService'

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? 'Não foi possível conectar ao servidor. Tente novamente.'
    : erro.message
}

export default function ExecucaoConclusaoForm({
  ordemServico,
  onSalvo,
}) {
  const [observacoesConclusao, setObservacoesConclusao] =
    useState('')
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState('')

  async function iniciar() {
    if (salvando) return

    setSalvando(true)
    setErro('')

    try {
      const salva = await iniciarExecucao(ordemServico.id)
      onSalvo(salva)
    } catch (error) {
      setErro(mensagemErro(error))
    } finally {
      setSalvando(false)
    }
  }

  async function concluir(evento) {
    evento.preventDefault()
    if (salvando) return

    setSalvando(true)
    setErro('')

    try {
      const salva = await concluirOrdemServico(
        ordemServico.id,
        {
          observacoesConclusao:
            observacoesConclusao.trim() || null,
        }
      )

      onSalvo(salva)
    } catch (error) {
      setErro(mensagemErro(error))
    } finally {
      setSalvando(false)
    }
  }

  if (ordemServico.status === 'APROVADO') {
    return (
      <section className="formulario-cartao">
        <h3>Execução do serviço</h3>

        <button
          type="button"
          className="botao-sucesso"
          disabled={salvando}
          onClick={iniciar}
        >
          {salvando
            ? 'Iniciando...'
            : 'Iniciar execução do serviço'}
        </button>

        {erro && (
          <p className="erro-campo" role="alert">
            {erro}
          </p>
        )}
      </section>
    )
  }

  return (
    <form
      className="formulario-cartao"
      onSubmit={concluir}
      noValidate
    >
      <h3>Concluir e liberar veículo</h3>

      <fieldset disabled={salvando}>
        <div className="campo">
          <label htmlFor={`conclusao-${ordemServico.id}`}>
            Observações de conclusão
          </label>

          <textarea
            id={`conclusao-${ordemServico.id}`}
            value={observacoesConclusao}
            onChange={(evento) => {
              setObservacoesConclusao(evento.target.value)
              setErro('')
            }}
            rows={3}
            maxLength={5000}
          />
        </div>

        <button type="submit" className="botao-sucesso">
          {salvando
            ? 'Concluindo...'
            : 'Concluir ordem de serviço'}
        </button>
      </fieldset>

      {erro && (
        <p className="erro-campo" role="alert">
          {erro}
        </p>
      )}
    </form>
  )
}