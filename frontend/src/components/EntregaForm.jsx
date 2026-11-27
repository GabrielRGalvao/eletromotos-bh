import { useState } from "react";
import { registrarEntrega } from "../services/ordemServicoService";

const TAMANHO_MAXIMO = 100 * 1024 * 1024;

const TIPOS_VIDEO = ["video/mp4", "video/webm", "video/quicktime"];

const EXTENSOES_VIDEO = [".mp4", ".webm", ".mov"];

function mensagemErro(erro) {
  return erro instanceof TypeError
    ? "Não foi possível conectar ao servidor. Tente novamente."
    : erro.message;
}

function videoValido(video) {
  const nome = video.name.toLowerCase();

  return (
    TIPOS_VIDEO.includes(video.type) ||
    EXTENSOES_VIDEO.some((extensao) => nome.endsWith(extensao))
  );
}

export default function EntregaForm({ ordemServico, onSalvo }) {
  const [entreguePor, setEntreguePor] = useState("");
  const [video, setVideo] = useState(null);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  async function enviarEntrega(evento) {
    evento.preventDefault();

    if (salvando) return;

    const responsavel = entreguePor.trim();

    if (!responsavel) {
      setErro("Informe o funcionário responsável pela entrega.");
      return;
    }

    if (responsavel.length > 150) {
      setErro("O nome do responsável deve ter no máximo 150 caracteres.");
      return;
    }

    if (video && !videoValido(video)) {
      setErro("Selecione um vídeo nos formatos MP4, WebM ou MOV.");
      return;
    }

    if (video && video.size > TAMANHO_MAXIMO) {
      setErro("O vídeo deve ter no máximo 100 MB.");
      return;
    }

    setSalvando(true);
    setErro("");

    try {
      const salva = await registrarEntrega(ordemServico.id, responsavel, video);

      onSalvo(salva);
    } catch (error) {
      setErro(mensagemErro(error));
    } finally {
      setSalvando(false);
    }
  }

  return (
    <form className="formulario-cartao" onSubmit={enviarEntrega} noValidate>
      <h3>Registrar entrega do veículo</h3>

      <fieldset disabled={salvando}>
        <div className="campo">
          <label htmlFor={`entregue-por-${ordemServico.id}`}>
            Funcionário responsável pela entrega *
          </label>

          <input
            id={`entregue-por-${ordemServico.id}`}
            type="text"
            value={entreguePor}
            onChange={(evento) => {
              setEntreguePor(evento.target.value);
              setErro("");
            }}
            maxLength={150}
            required
          />
        </div>

        <div className="campo">
          <label htmlFor={`video-saida-${ordemServico.id}`}>
            Vídeo de vistoria de saída (opcional)
          </label>

          <input
            id={`video-saida-${ordemServico.id}`}
            type="file"
            accept="video/mp4,video/webm,video/quicktime,.mp4,.webm,.mov"
            onChange={(evento) => {
              setVideo(evento.target.files?.[0] || null);
              setErro("");
            }}
          />

          <small>MP4, WebM ou MOV. Tamanho máximo: 100 MB.</small>
        </div>

        <button type="submit" className="botao-sucesso">
          {salvando ? "Registrando entrega..." : "Confirmar entrega"}
        </button>
      </fieldset>

      {erro && (
        <p className="erro-campo" role="alert">
          {erro}
        </p>
      )}
    </form>
  );
}
