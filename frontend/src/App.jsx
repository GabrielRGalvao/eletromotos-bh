import { useState } from 'react'
import ClientesPage from './pages/ClientesPage'
import MecanicosPage from './pages/MecanicosPage'
import AgendamentosPage from './pages/AgendamentosPage'

export default function App() {
  const [pagina, setPagina] = useState('clientes')

  return (
    <>
      <header className="barra-superior">
        <span className="marca">EletroMotos BH</span>

        <nav
          className="navegacao-principal"
          aria-label="Navegação principal"
        >
          <button
            type="button"
            aria-current={
              pagina === 'clientes' ? 'page' : undefined
            }
            onClick={() => setPagina('clientes')}
          >
            Clientes
          </button>

          <button
            type="button"
            aria-current={
              pagina === 'mecanicos' ? 'page' : undefined
            }
            onClick={() => setPagina('mecanicos')}
          >
            Mecânicos
          </button>

          <button
            type="button"
            aria-current={
              pagina === 'agendamentos' ? 'page' : undefined
            }
            onClick={() => setPagina('agendamentos')}
          >
            Agendamentos
          </button>
        </nav>
      </header>

      <div hidden={pagina !== 'clientes'}>
        <ClientesPage />
      </div>

      <div hidden={pagina !== 'mecanicos'}>
        {pagina === 'mecanicos' && <MecanicosPage />}
      </div>

      {pagina === 'agendamentos' && <AgendamentosPage />}
    </>
  )
}