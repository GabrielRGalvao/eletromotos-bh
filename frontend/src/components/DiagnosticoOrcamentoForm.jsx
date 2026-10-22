import { useState } from 'react'
import { registrarDiagnostico } from '../services/ordemServicoService'

const valoresIniciais = {
  diagnostico: '',
  descricaoOrcamento: '',
  valorOrcamento: '',
}

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? 'Não foi possível conectar ao servidor. Tente novamente.'
    : erro.message
}

export default function DiagnosticoOrcamentoForm({
  ordemServico,
  onSalvo,
}) {
  const [dados, setDados] = useState(valoresIniciais)
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState('')
  const [errosCampos, setErrosCampos] = useState({})

  function alterarCampo(evento) {
    const { name, value } = evento.target

    setDados((atuais) => ({
      ...atuais,
      [name]: value,
    }))

    setErro('')
    setErrosCampos({})
  }

  async function salvar(evento) {
    evento.preventDefault()

    if (salvando) return

    const erros = {}
    const valor = Number(dados.valorOrcamento)

    if (!dados.diagnostico.trim()) {
      erros.diagnostico = 'Informe o diagnóstico'
    }

    if (!dados.descricaoOrcamento.trim()) {
      erros.descricaoOrcamento =
        'Informe a descrição do orçamento'
    }

    if (!Number.isFinite(valor) || valor <= 0) {
      erros.valorOrcamento =
        'Informe um valor maior que zero'
    }

    setErro('')
    setErrosCampos(erros)

    if (Object.keys(erros).length > 0) {
      setErro('Confira os campos informados.')
      return
    }

    setSalvando(true)

    try {
      const salva = await registrarDiagnostico(
        ordemServico.id,
        {
          diagnostico: dados.diagnostico.trim(),
          descricaoOrcamento:
            dados.descricaoOrcamento.trim(),
          valorOrcamento: valor,
        }
      )

      onSalvo(salva)
    } catch (error) {
      setErro(mensagemErro(error))
      setErrosCampos(error.campos || {})
    } finally {
      setSalvando(false)
    }
  }

  return (
    <form
      className="formulario-cartao"
      onSubmit={salvar}
      noValidate
    >
      <h3>Diagnóstico e orçamento</h3>

      <fieldset disabled={salvando}>
        <div className="campo">
          <label htmlFor={`diagnostico-${ordemServico.id}`}>
            Diagnóstico
          </label>

          <textarea
            id={`diagnostico-${ordemServico.id}`}
            name="diagnostico"
            value={dados.diagnostico}
            onChange={alterarCampo}
            rows={3}
            maxLength={5000}
            required
            aria-invalid={Boolean(errosCampos.diagnostico)}
          />

          {errosCampos.diagnostico && (
            <span className="erro-campo">
              {errosCampos.diagnostico}
            </span>
          )}
        </div>

        <div className="campo">
          <label htmlFor={`orcamento-${ordemServico.id}`}>
            Descrição do orçamento
          </label>

          <textarea
            id={`orcamento-${ordemServico.id}`}
            name="descricaoOrcamento"
            value={dados.descricaoOrcamento}
            onChange={alterarCampo}
            rows={3}
            maxLength={5000}
            required
            aria-invalid={Boolean(
              errosCampos.descricaoOrcamento
            )}
          />

          {errosCampos.descricaoOrcamento && (
            <span className="erro-campo">
              {errosCampos.descricaoOrcamento}
            </span>
          )}
        </div>

        <div className="campo">
          <label htmlFor={`valor-${ordemServico.id}`}>
            Valor do orçamento (R$)
          </label>

          <input
            id={`valor-${ordemServico.id}`}
            name="valorOrcamento"
            type="number"
            min="0.01"
            step="0.01"
            value={dados.valorOrcamento}
            onChange={alterarCampo}
            required
            aria-invalid={Boolean(errosCampos.valorOrcamento)}
          />

          {errosCampos.valorOrcamento && (
            <span className="erro-campo">
              {errosCampos.valorOrcamento}
            </span>
          )}
        </div>

        <button type="submit">
          {salvando
            ? 'Enviando orçamento...'
            : 'Enviar orçamento para aprovação'}
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