import { useEffect, useState } from 'react'
import { listarClientes } from '../services/clienteService'

export default function ClientesPage() {
  const [clientes, setClientes] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    async function carregarClientes() {
      try {
        const dados = await listarClientes(controller.signal)

        if (!controller.signal.aborted) {
          setClientes(dados)
        }
      } catch {
        if (!controller.signal.aborted) {
          setErro('Não foi possível carregar os clientes. Tente novamente.')
        }
      } finally {
        if (!controller.signal.aborted) {
          setCarregando(false)
        }
      }
    }

    carregarClientes()

    return () => controller.abort()
  }, [])

  return (
    <main className="pagina">
      <header className="cabecalho-pagina">
        <p className="etiqueta">CADASTROS</p>
        <h1>Clientes</h1>
        <p>Consulte os clientes da oficina e seus contatos.</p>
      </header>

      {carregando && (
        <p className="aviso" role="status">
          Carregando clientes...
        </p>
      )}

      {erro && (
        <div className="aviso aviso-erro" role="alert">
          <p>{erro}</p>
          <button type="button" onClick={() => window.location.reload()}>
            Tentar novamente
          </button>
        </div>
      )}

      {!carregando && !erro && (
        <>
          <p className="resumo">
            {clientes.length} cliente(s) cadastrado(s)
          </p>

          {clientes.length === 0 ? (
            <p className="aviso">Nenhum cliente cadastrado ainda.</p>
          ) : (
            <ul className="lista-clientes">
              {clientes.map((cliente) => (
                <li className="cartao-cliente" key={cliente.id}>
                  <h2>{cliente.nome}</h2>
                  <dl>
                    <dt>Telefone</dt>
                    <dd>{cliente.telefone}</dd>

                    <dt>Email</dt>
                    <dd>{cliente.email || 'Não informado'}</dd>
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