export async function listarClientes(signal) {
  const resposta = await fetch('/api/clientes', { signal })

  if (!resposta.ok) {
    throw new Error('Não foi possível carregar os clientes.')
  }

  return resposta.json()
}

export async function cadastrarCliente(cliente) {
  const resposta = await fetch('/api/clientes', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(cliente),
  })

  if (!resposta.ok) {
    const dados = await resposta.json().catch(() => ({}))

    const erro = new Error(
      dados.mensagem || 'Não foi possível cadastrar o cliente.'
    )

    erro.campos = dados.campos || {}
    throw erro
  }

  return resposta.json()
}