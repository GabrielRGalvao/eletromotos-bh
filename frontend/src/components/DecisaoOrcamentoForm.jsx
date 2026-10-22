import { useState } from 'react'
import {
  registrarDecisaoOrcamento,
} from '../services/ordemServicoService'

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? 'Não foi possível conectar ao servidor. Tente novamente.'
    : erro.message
}

export default function DecisaoOrcamentoForm({
  ordemServico,
  onSalvo,
}) {
  const [observacao, setObservacao] = useState('')
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState('')

  async function registrarDecisao(aprovado) {
    if (salvando) return

    setSalvando(true)
    setErro('')

    try {
      const salva = await registrarDecisaoOrcamento(
        ordemServico.id,
        {
          aprovado,
          observacao: observacao.trim() || null,
        }
      )

      onSalvo(salva)
    } catch (error) {
      setErro(mensagemErro(error))
    } finally {
      setSalvando(false)
    }
  }

  return (
    <section className="formulario-cartao">
      <h3>Decisão do cliente</h3>

      <div className="campo">
        <label htmlFor={`decisao-${ordemServico.id}`}>
          Observação da decisão
        </label>

        <textarea
          id={`decisao-${ordemServico.id}`}
          value={observacao}
          onChange={(evento) => {
            setObservacao(evento.target.value)
            setErro('')
          }}
          rows={3}
          maxLength={5000}
          disabled={salvando}
        />
      </div>

      <div className="acoes-formulario">
        <button
          type="button"
          className="botao-sucesso"
          disabled={salvando}
          onClick={() => registrarDecisao(true)}
        >
          {salvando ? 'Salvando...' : 'Aprovar orçamento'}
        </button>

        <button
          type="button"
          className="botao-perigo"
          disabled={salvando}
          onClick={() => registrarDecisao(false)}
        >
          {salvando ? 'Salvando...' : 'Reprovar orçamento'}
        </button>
      </div>

      {erro && (
        <p className="erro-campo" role="alert">
          {erro}
        </p>
      )}
    </section>
  )
}