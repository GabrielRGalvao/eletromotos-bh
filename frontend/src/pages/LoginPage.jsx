import { useState } from "react";
import { entrar } from "../services/authService";

export default function LoginPage({ aoEntrar }) {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [enviando, setEnviando] = useState(false);

  async function enviar(evento) {
    evento.preventDefault();
    setErro("");
    setEnviando(true);

    try {
      const usuario = await entrar({ email, senha });
      aoEntrar(usuario);
    } catch (erroAtual) {
      setErro(erroAtual.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <main className="tela-login">
      <form className="cartao-login" onSubmit={enviar}>
        <p className="etiqueta">ACESSO RESTRITO</p>
        <h1>EletroMotos BH</h1>
        <p>Entre para acessar a gestão da oficina.</p>

        <div className="campo">
          <label htmlFor="email">E-mail</label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={(evento) => setEmail(evento.target.value)}
            required
          />
        </div>

        <div className="campo">
          <label htmlFor="senha">Senha</label>
          <input
            id="senha"
            type="password"
            autoComplete="current-password"
            value={senha}
            onChange={(evento) => setSenha(evento.target.value)}
            required
          />
        </div>

        {erro && (
          <p className="erro-campo" role="alert">
            {erro}
          </p>
        )}

        <button type="submit" disabled={enviando}>
          {enviando ? "Entrando..." : "Entrar"}
        </button>
      </form>
    </main>
  );
}