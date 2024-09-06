package jv.triersistemas.reserva_restaurante.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jv.triersistemas.reserva_restaurante.dto.ClienteDto;
import jv.triersistemas.reserva_restaurante.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;

	@GetMapping
	public Page<ClienteDto> listarClientes(@RequestParam(defaultValue = "0", required = false) Integer page,
			@RequestParam(defaultValue = "10", required = false) Integer size,
			@RequestParam(required = false) String searchTerm) {
		return clienteService.listarClientes(Pageable.ofSize(size).withPage(page), searchTerm);
	}

	@GetMapping("/maiorValor")
	public List<ClienteDto> buscarClientesComMaiorValorGasto() {
		return clienteService.buscarClientesComMaiorValorGasto();
	}

	@PostMapping
	public ClienteDto adicionarCliente(@RequestBody ClienteDto clienteDto) {
		return clienteService.adicionarCliente(clienteDto);
	}

	@PutMapping("/{id}")
	public ClienteDto atualizarCliente(@PathVariable Long id, @RequestBody ClienteDto clienteAtualizado) {
		return clienteService.atualizarCliente(id, clienteAtualizado);
	}

	@PutMapping("/desbloquear/{id}")
	public ClienteDto desbloquearCliente(@PathVariable Long id) {
		return clienteService.desbloquearCliente(id);
	}

	@PutMapping("/bloquear/{id}")
	public ClienteDto bloquearCliente(@PathVariable Long id) {
		return clienteService.bloquearCliente(id);
	}
}
