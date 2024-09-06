package jv.triersistemas.reserva_restaurante.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jv.triersistemas.reserva_restaurante.dto.ClienteDto;
import jv.triersistemas.reserva_restaurante.entity.ClienteEntity;
import jv.triersistemas.reserva_restaurante.entity.ReservaEntity;
import jv.triersistemas.reserva_restaurante.entity.RestauranteEntity;
import jv.triersistemas.reserva_restaurante.enums.StatusEnum;
import jv.triersistemas.reserva_restaurante.repository.ClienteRepository;
import jv.triersistemas.reserva_restaurante.repository.ClienteRepositoryCustom;
import jv.triersistemas.reserva_restaurante.repository.ReservaRepository;
import jv.triersistemas.reserva_restaurante.repository.RestauranteRepository;
import jv.triersistemas.reserva_restaurante.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private ClienteRepositoryCustom clienteRepositoryCustom;

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private ReservaRepository reservaRepository;

	@Override
	public ClienteDto adicionarCliente(ClienteDto novoCliente) {
		RestauranteEntity restaurante = restauranteRepository.findById(novoCliente.getIdRestaurante())
				.orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado"));
		String cpfSemCaracteres = limparCpf(novoCliente.getCpf());
		validarIdade(novoCliente);
		ClienteEntity clienteEntity = new ClienteEntity(novoCliente);
		clienteEntity.setCpf(cpfSemCaracteres);
		clienteEntity.setRestaurante(restaurante);
		return new ClienteDto(repository.save(clienteEntity));
	}

	@Override
	public List<ClienteDto> getTodosClientes() {
		List<ClienteEntity> listaClientes = repository.findAll();
		return listaClientes.stream().map(ClienteDto::new).toList();
	}

	@Override
	public ClienteDto atualizarCliente(Long id, ClienteDto ClienteAtualizado) {
		Optional<ClienteEntity> clientePorId = repository.findById(id);
		if (clientePorId.isPresent()) {
			clientePorId.get().atualizaCliente(ClienteAtualizado);
			var entidadePersistida = repository.save(clientePorId.get());
			return new ClienteDto(entidadePersistida);
		}
		return null;
	}

	@Override
	public ClienteDto bloquearCliente(Long clienteId) {
		ClienteEntity cliente = repository.findById(clienteId).orElseThrow();

		List<ReservaEntity> reservasInadimplentes = reservaRepository.findByClienteIdAndStatus(clienteId,
				StatusEnum.INADIPLENTE);

		if (reservasInadimplentes.size() >= 3) {
			cliente.setBloqueado(true);
			repository.save(cliente);
		} else {
			throw new IllegalArgumentException("Esse cliente não tem 3 inadimplências ");
		}
		return new ClienteDto(cliente);
	}

	@Override
	public ClienteDto desbloquearCliente(Long clienteId) {
		ClienteEntity cliente = repository.findById(clienteId)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

		cliente.setBloqueado(false);

		repository.save(cliente);

		return new ClienteDto(cliente);
	}
	
	@Override
	public Page<ClienteDto> listarClientes(Pageable pageable, String searchTerm) {
		return clienteRepositoryCustom.buscaPaginadaClientePorNome(pageable, searchTerm);
	}
	
	@Override
	public List<ClienteDto> buscarClientesComMaiorValorGasto() {
		List<ClienteEntity> clientesMaiorValorGasto = clienteRepositoryCustom.buscarClientesComMaiorValorGasto();
		return clientesMaiorValorGasto.stream().map(ClienteDto::new).toList();
	}

	public void validarIdade(ClienteDto cliente) {
		LocalDate idadeMinima = LocalDate.now().minusYears(12);
		LocalDate idadeMaxima = LocalDate.now().minusYears(100);
		if (cliente.getDataNascimento().isAfter(idadeMinima) || cliente.getDataNascimento().isBefore(idadeMaxima)) {
			throw new IllegalArgumentException(
					"Os clientes não podem ter a mais de 100 anos e nem ter menos que 12 anos");
		}
	}

	public String limparCpf(String cpf) {
		return cpf.replaceAll("[^\\d]", "");
	}

}
