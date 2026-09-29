import { useState } from 'react'
import {
  cadastrarCliente,
  atualizarCliente,
} from '../services/clienteService'

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

export default function ClienteForm({
  cliente,
  onSalvo,
  onCancelar,
  salvando,
  onSalvandoChange,
}) {
  const [erro, setErro] = useState('')
  const [errosCampos, setErrosCampos] = useState({})
  const editando = Boolean(cliente)

    async function enviar(evento) {
    evento.preventDefault()

    if (salvando) return

    const formulario = evento.currentTarget
    const dados = new FormData(formulario)

    const dadosCliente = {
      nome: dados.get('nome').trim(),
      telefone: dados.get('telefone').trim(),
      email: dados.get('email').trim() || null,
      cpf: dados.get('cpf').trim() || null,
      observacoes: dados.get('observacoes').trim() || null,
    }

    onSalvandoChange(true)
    setErro('')
    setErrosCampos({})

    try {
      const clienteSalvo = editando
        ? await atualizarCliente(cliente.id, dadosCliente)
        : await cadastrarCliente(dadosCliente)

      if (!editando) {
        formulario.reset()
      }

      onSalvo(clienteSalvo)
    } catch (error) {
      setErro(
        error instanceof TypeError
          ? 'Não foi possível conectar. Confira sua conexão e tente novamente.'
          : error.message
      )
      setErrosCampos(error.campos || {})
    } finally {
      onSalvandoChange(false)
    }
  }

  return (
    <section className="painel-formulario" aria-labelledby="titulo-cadastro">
      <h2 id="titulo-cadastro">
        {editando ? 'Editar cliente' : 'Novo cliente'}</h2>
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
                  defaultValue={cliente?.[campo.nome] ?? ''}
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
              defaultValue={cliente?.observacoes ?? ''}
            />
          </div>

          <div className="acoes-formulario">
            <button type="submit">
              {salvando
                ? 'Salvando...'
                : editando
                  ? 'Salvar alterações'
                  : 'Salvar cliente'}
            </button>

            {editando && (
              <button
                type="button"
                className="botao-secundario"
                onClick={onCancelar}
              >
                Cancelar edição
              </button>
            )}
          </div>
        </fieldset>

        {erro && <p className="erro-campo" role="alert">{erro}</p>}
      </form>
    </section>
  )
}