package jv.triersistemas.reserva_restaurante.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jv.triersistemas.reserva_restaurante.dto.ClienteDto;

public interface ClienteService {
	ClienteDto adicionarCliente(ClienteDto novoCliente);

	List<ClienteDto> getTodosClientes();

	ClienteDto atualizarCliente(Long id, ClienteDto ClienteAtualizado);

	ClienteDto bloquearCliente(Long clienteId);

	ClienteDto desbloquearCliente(Long clienteId);
	
	Page<ClienteDto> listarClientes(Pageable pageable, String searchTerm);
	
	List<ClienteDto>  buscarClientesComMaiorValorGasto();

}
