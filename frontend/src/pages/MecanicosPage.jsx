import { useEffect, useRef, useState } from 'react'
import {
  alterarStatusMecanico,
  listarMecanicos,
  salvarMecanico,
} from '../services/mecanicoService'

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? 'Não foi possível conectar. Confira se o backend está funcionando.'
    : erro.message
}

export default function MecanicosPage() {
  const [mecanicos, setMecanicos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erroLista, setErroLista] = useState('')
  const [tentativa, setTentativa] = useState(0)

  const [editandoId, setEditandoId] = useState(null)
  const [nome, setNome] = useState('')
  const [salvando, setSalvando] = useState(false)
  const [alterandoId, setAlterandoId] = useState(null)

  const [erro, setErro] = useState('')
  const [erroNome, setErroNome] = useState('')
  const [sucesso, setSucesso] = useState('')

  const campoNome = useRef(null)
  const ocupado = salvando || alterandoId !== null

  useEffect(() => {
    const controller = new AbortController()

    async function carregar() {
      setCarregando(true)
      setErroLista('')

      try {
        const dados = await listarMecanicos(controller.signal)

        if (!controller.signal.aborted) {
          setMecanicos(dados)
        }
      } catch (error) {
        if (!controller.signal.aborted) {
          setErroLista(mensagemErro(error))
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false)
        }
      }
    }

    carregar()
    return () => controller.abort()
  }, [tentativa])

  function limparMensagens() {
    setErro('')
    setErroNome('')
    setSucesso('')
  }

  function atualizarLista(mecanicoSalvo) {
    setMecanicos((atuais) => {
      const existe = atuais.some((item) => item.id === mecanicoSalvo.id)

      return existe
        ? atuais.map((item) =>
            item.id === mecanicoSalvo.id ? mecanicoSalvo : item
          )
        : [...atuais, mecanicoSalvo]
    })
  }

  function iniciarEdicao(mecanico) {
    limparMensagens()
    setEditandoId(mecanico.id)
    setNome(mecanico.nome)
    campoNome.current?.focus()
  }

  function cancelarEdicao() {
    setEditandoId(null)
    setNome('')
    limparMensagens()
  }

  async function salvar(evento) {
    evento.preventDefault()
    if (ocupado) return

    setSalvando(true)
    limparMensagens()

    try {
      const salvo = await salvarMecanico(editandoId, nome.trim())
      atualizarLista(salvo)

      setSucesso(
        editandoId !== null
          ? 'Mecânico atualizado com sucesso.'
          : 'Mecânico cadastrado com sucesso.'
      )

      setEditandoId(null)
      setNome('')
    } catch (error) {
      setErro(mensagemErro(error))
      setErroNome(error.campos?.nome || '')
    } finally {
      setSalvando(false)
    }
  }

  async function alternarStatus(mecanico) {
    if (ocupado) return

    const acao = mecanico.ativo ? 'Inativar' : 'Ativar'

    if (!window.confirm(`${acao} o mecânico "${mecanico.nome}"?`)) {
      return
    }

    setAlterandoId(mecanico.id)
    limparMensagens()

    try {
      const salvo = await alterarStatusMecanico(
        mecanico.id,
        !mecanico.ativo
      )

      atualizarLista(salvo)
      setSucesso(
        salvo.ativo
          ? 'Mecânico ativado com sucesso.'
          : 'Mecânico inativado com sucesso.'
      )
    } catch (error) {
      setErro(mensagemErro(error))
    } finally {
      setAlterandoId(null)
    }
  }

  const mecanicosOrdenados = [...mecanicos].sort(
    (a, b) => a.nome.localeCompare(b.nome, 'pt-BR') || a.id - b.id
  )

  return (
    <main className="pagina">
      <header className="cabecalho-pagina">
        <p className="etiqueta">CADASTROS</p>
        <h1>Mecânicos</h1>
        <p>Cadastre os profissionais e mantenha sua situação atualizada.</p>
      </header>

      {carregando && (
        <p className="aviso" role="status">Carregando mecânicos...</p>
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
            aria-labelledby="titulo-mecanico"
          >
            <h2 id="titulo-mecanico">
              {editandoId !== null ? 'Editar mecânico' : 'Novo mecânico'}
            </h2>

            <form onSubmit={salvar} noValidate>
              <fieldset disabled={ocupado}>
                <legend className="legenda-formulario">
                  Dados do mecânico
                </legend>

                <div className="campo">
                  <label htmlFor="mecanico-nome">Nome — obrigatório</label>
                  <input
                    ref={campoNome}
                    id="mecanico-nome"
                    name="nome"
                    type="text"
                    value={nome}
                    onChange={(evento) => {
                      setNome(evento.target.value)
                      limparMensagens()
                    }}
                    maxLength={150}
                    required
                    aria-invalid={Boolean(erroNome)}
                    aria-describedby={
                      erroNome ? 'erro-mecanico-nome' : undefined
                    }
                  />

                  {erroNome && (
                    <span className="erro-campo" id="erro-mecanico-nome">
                      {erroNome}
                    </span>
                  )}
                </div>

                <div className="acoes-formulario">
                  <button type="submit">
                    {salvando
                      ? 'Salvando...'
                      : editandoId !== null
                        ? 'Salvar alterações'
                        : 'Cadastrar mecânico'}
                  </button>

                  {editandoId !== null && (
                    <button
                      type="button"
                      className="botao-secundario"
                      onClick={cancelarEdicao}
                    >
                      Cancelar edição
                    </button>
                  )}
                </div>
              </fieldset>
            </form>
          </section>

          {erro && <p className="erro-campo" role="alert">{erro}</p>}
          {sucesso && (
            <p className="mensagem-sucesso" role="status">{sucesso}</p>
          )}

          <p className="resumo">
            {mecanicos.length} mecânico(s) cadastrado(s)
          </p>

          {mecanicos.length === 0 ? (
            <p className="aviso">Nenhum mecânico cadastrado ainda.</p>
          ) : (
            <ul className="lista-clientes">
              {mecanicosOrdenados.map((mecanico) => (
                <li className="cartao-cliente" key={mecanico.id}>
                  <h2>{mecanico.nome}</h2>

                  <span
                    className={
                      mecanico.ativo
                        ? 'situacao-mecanico situacao-ativa'
                        : 'situacao-mecanico situacao-inativa'
                    }
                  >
                    {mecanico.ativo ? 'Ativo' : 'Inativo'}
                  </span>

                  <div className="acoes-formulario acoes-cliente">
                    <button
                      type="button"
                      className="botao-secundario"
                      disabled={ocupado}
                      onClick={() => iniciarEdicao(mecanico)}
                      aria-label={`Editar ${mecanico.nome}`}
                    >
                      Editar
                    </button>

                    <button
                      type="button"
                      className={
                        mecanico.ativo
                          ? 'botao-perigo'
                          : 'botao-secundario'
                      }
                      disabled={ocupado || editandoId !== null}
                      onClick={() => alternarStatus(mecanico)}
                      aria-label={
                        `${mecanico.ativo ? 'Inativar' : 'Ativar'} ${mecanico.nome}`
                      }
                    >
                      {alterandoId === mecanico.id
                        ? 'Atualizando...'
                        : mecanico.ativo
                          ? 'Inativar'
                          : 'Ativar'}
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </main>
  )
}