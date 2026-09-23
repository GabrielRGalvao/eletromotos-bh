export async function listarClientes(signal) {
  const resposta = await fetch('/api/clientes', { signal })

  if (!resposta.ok) {
    throw new Error('Não foi possível carregar os clientes.')
  }

  return resposta.json()
}