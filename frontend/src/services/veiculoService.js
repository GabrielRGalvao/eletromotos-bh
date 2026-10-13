async function lerResposta(resposta) {
  if (!resposta.ok) {
    const dados = await resposta.json().catch(() => ({}))
    const erro = new Error(
      dados.mensagem || 'Não foi possível completar a operação.'
    )
    erro.campos = dados.campos || {}
    throw erro
  }

  return resposta.json()
}

export async function listarVeiculos(clienteId, signal) {
  const resposta = await fetch(
    `/api/veiculos?clienteId=${encodeURIComponent(clienteId)}`,
    { signal }
  )

  return lerResposta(resposta)
}

export async function cadastrarVeiculo(veiculo) {
  const resposta = await fetch('/api/veiculos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(veiculo),
  })

  return lerResposta(resposta)
}