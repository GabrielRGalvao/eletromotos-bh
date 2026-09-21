package br.com.eletromotosbh.cliente;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.eletromotosbh.cliente.dto.ClienteRequest;
import br.com.eletromotosbh.cliente.dto.ClienteResponse;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest request) {
        Cliente cliente = new Cliente(
            request.nome(),
            request.telefone(),
            request.email(),
            request.cpf(),
            request.observacoes()
        );

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteResponse(
            clienteSalvo.getId(),
            clienteSalvo.getNome(),
            clienteSalvo.getTelefone(),
            clienteSalvo.getEmail(),
            clienteSalvo.getCpf(),
            clienteSalvo.getObservacoes(),
            clienteSalvo.getDataCadastro()
        );
    }
}