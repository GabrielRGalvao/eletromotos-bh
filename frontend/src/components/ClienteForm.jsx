import { useState } from 'react'
import { cadastrarCliente } from '../services/clienteService'

const campos = [
  {
    nome: 'nome',
    rotulo: 'Nome',
    tipo: 'text',
    limite: 150,
    obrigatorio: true,
  },
  {
    nome: 'telefone',
    rotulo: 'Telefone',
    tipo: 'tel',
    limite: 20,
    obrigatorio: true,
  },
  {
    nome: 'email',
    rotulo: 'Email',
    tipo: 'email',
    limite: 254,
    obrigatorio: false,
  },
  {
    nome: 'cpf',
    rotulo: 'CPF (somente números)',
    tipo: 'text',
    limite: 11,
    obrigatorio: false,
  },
]

export default function ClienteForm({ onCadastrado }) {
  const [salvando, setSalvando] = useState(false)
  const [erro, setErro] = useState('')
  const [errosCampos, setErrosCampos] = useState({})
  const [sucesso, setSucesso] = useState('')

  async function enviar(evento) {
    evento.preventDefault()

    if (salvando) return

    const formulario = evento.currentTarget
    const dados = new FormData(formulario)

    const cliente = {
      nome: dados.get('nome').trim(),
      telefone: dados.get('telefone').trim(),
      email: dados.get('email').trim() || null,
      cpf: dados.get('cpf').trim() || null,
      observacoes: dados.get('observacoes').trim() || null,
    }

    setSalvando(true)
    setErro('')
    setErrosCampos({})
    setSucesso('')

    try {
      const clienteSalvo = await cadastrarCliente(cliente)
      onCadastrado(clienteSalvo)
      formulario.reset()
      setSucesso('Cliente cadastrado com sucesso.')
    } catch (error) {
      setErro(
        error instanceof TypeError
          ? 'Não foi possível conectar. Confira sua conexão e tente novamente.'
          : error.message
      )
      setErrosCampos(error.campos || {})
    } finally {
      setSalvando(false)
    }
  }

  return (
    <section className="painel-formulario" aria-labelledby="titulo-cadastro">
      <h2 id="titulo-cadastro">Novo cliente</h2>
      <p>Nome e telefone são obrigatórios.</p>

      <form onSubmit={enviar} noValidate>
        <fieldset disabled={salvando}>
          <legend className="legenda-formulario">Dados do cliente</legend>

          <div className="grade-formulario">
            {campos.map((campo) => (
              <div className="campo" key={campo.nome}>
                <label htmlFor={`cliente-${campo.nome}`}>
                  {campo.rotulo}
                  {!campo.obrigatorio && ' — opcional'}
                </label>

                <input
                  id={`cliente-${campo.nome}`}
                  name={campo.nome}
                  type={campo.tipo}
                  maxLength={campo.limite}
                  required={campo.obrigatorio}
                  aria-invalid={Boolean(errosCampos[campo.nome])}
                  aria-describedby={
                    errosCampos[campo.nome]
                      ? `erro-${campo.nome}`
                      : undefined
                  }
                />

                {errosCampos[campo.nome] && (
                  <span
                    className="erro-campo"
                    id={`erro-${campo.nome}`}
                  >
                    {errosCampos[campo.nome]}
                  </span>
                )}
              </div>
            ))}
          </div>

          <div className="campo">
            <label htmlFor="cliente-observacoes">
              Observações — opcional
            </label>
            <textarea
              id="cliente-observacoes"
              name="observacoes"
              rows={3}
            />
          </div>

          <button type="submit">
            {salvando ? 'Salvando...' : 'Salvar cliente'}
          </button>
        </fieldset>

        {erro && <p className="erro-campo" role="alert">{erro}</p>}
        {sucesso && <p className="mensagem-sucesso" role="status">{sucesso}</p>}
      </form>
    </section>
  )
}