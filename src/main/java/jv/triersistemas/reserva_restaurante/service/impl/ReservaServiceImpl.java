package jv.triersistemas.reserva_restaurante.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jv.triersistemas.reserva_restaurante.dto.ReservaDto;
import jv.triersistemas.reserva_restaurante.entity.ClienteEntity;
import jv.triersistemas.reserva_restaurante.entity.MesaEntity;
import jv.triersistemas.reserva_restaurante.entity.PedidoEntity;
import jv.triersistemas.reserva_restaurante.entity.ReservaEntity;
import jv.triersistemas.reserva_restaurante.enums.StatusEnum;
import jv.triersistemas.reserva_restaurante.repository.ClienteRepository;
import jv.triersistemas.reserva_restaurante.repository.MesaRepository;
import jv.triersistemas.reserva_restaurante.repository.ReservaRepository;
import jv.triersistemas.reserva_restaurante.service.ReservaService;

@Service
public class ReservaServiceImpl implements ReservaService {

	@Autowired
	private ReservaRepository repository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private MesaRepository mesaRepository;

	@Override
	public ReservaDto adicionarReserva(ReservaDto novaReserva) {
		ClienteEntity cliente = clienteRepository.findById(novaReserva.getIdCliente())
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
		MesaEntity mesa = mesaRepository.findById(novaReserva.getIdMesa())
				.orElseThrow(() -> new IllegalArgumentException("Mesa não encontrado"));

		validaDataReserva(novaReserva);
//		validaReservaNaMesmaData(novaReserva);
		verificarInadimplenciaCliente(cliente);
		if (verificaQuantidadeDeReservasCanceladas(cliente.getId()) == false) {
			throw new IllegalArgumentException(
					"O cliente tem 2 reservas canceladas no ultimo mês. Não pode mais fazer reservas no mês");
		}

		ReservaEntity reserva = new ReservaEntity(novaReserva);
		reserva.setCliente(cliente);
		reserva.setMesa(mesa);

		return new ReservaDto(repository.save(reserva));
	}

	@Override
	public List<ReservaDto> buscarReservasPorCliente(Long idCliente) {
		List<ReservaEntity> reservasCliente = repository.findByClienteId(idCliente);
		return reservasCliente.stream().map(ReservaDto::new).toList();
	}

	@Override
	public ReservaDto atualizaReserva(Long id, StatusEnum status) {
		Optional<ReservaEntity> reservaEntity = repository.findById(id);
		if (reservaEntity.isPresent()) {
			validaReservaConcluida(status, reservaEntity.get().getDataReserva());
			validaReservaCancelada(status, reservaEntity.get().getDataReserva());
			reservaEntity.get().atualizarStatus(status);
			var entidadePersistida = repository.save(reservaEntity.get());
			adicionarQuantidadeGastoENumReservas(id);
			return new ReservaDto(entidadePersistida);
		}
		
		return null;
	}

	@Override
	public void concluirReservaNaoFinalizada() {
		List<ReservaEntity> listaReservas = repository.findByStatus(StatusEnum.AGENDADA);
		for (ReservaEntity reserva : listaReservas) {
			if (reserva.getDataReserva().isBefore(LocalDate.now())) {
				if (reserva.getPedidos().isEmpty()) {
					reserva.setStatus(StatusEnum.INADIPLENTE);
					reserva.setObservacao("Reserva finalizada automaticamente pelo sistema");
				} else {
					reserva.setStatus(StatusEnum.CONCLUIDA);
					reserva.setObservacao("Reserva finalizada automaticamente pelo sistema");
				}
				repository.save(reserva);
			}
		}

	}

	public void validaDataReserva(ReservaDto reserva) {
		if (reserva.getDataReserva().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("A data de reserva não pode ser uma data no passado");
		}
	}

	public void validaReservaConcluida(StatusEnum status, LocalDate dataReserva) {
		if (status == StatusEnum.CONCLUIDA && (dataReserva.isBefore(LocalDate.now()))) {
			throw new IllegalArgumentException(
					"A reserva só pode ser alterada para concluida caso a data seja igual a da reserva ou após");
		}

	}

	public void validaReservaCancelada(StatusEnum status, LocalDate dataReserva) {
		if (status == StatusEnum.CANCELADA && dataReserva.equals(LocalDate.now())
				|| dataReserva.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("A reserva só pode ser cancelada com no máximo 1 dia de antecedência");
		}

	}

	public void adicionarQuantidadeGastoENumReservas(Long id) {
		ReservaEntity reserva = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada"));

		if (reserva.getStatus() == StatusEnum.CONCLUIDA) {
			BigDecimal totalPedidos = reserva.getPedidos().stream().map(PedidoEntity::getValor).reduce(BigDecimal.ZERO,
					BigDecimal::add);

			ClienteEntity cliente = reserva.getCliente();
			cliente.setQuantidadeValorGasto(cliente.getQuantidadeValorGasto().add(totalPedidos));
			cliente.setQuantidadeReservas(cliente.getQuantidadeReservas() + 1);

			clienteRepository.save(cliente);
		}
	}

	public boolean verificaQuantidadeDeReservasCanceladas(Long clienteId) {
		List<ReservaEntity> reservasCanceladas = repository.findByClienteIdAndStatus(clienteId, StatusEnum.CANCELADA);

		LocalDate umMesAtras = LocalDate.now().minusMonths(1);

		long canceladasUltimoMes = reservasCanceladas.stream().filter(r -> r.getDataReserva().isAfter(umMesAtras))
				.count();
		System.out.println(canceladasUltimoMes);
		return canceladasUltimoMes < 2;
	}

	public void verificarInadimplenciaCliente(ClienteEntity cliente) {
		ClienteEntity clienteInadimplente = clienteRepository.findById(cliente.getId()).orElseThrow();
		if (clienteInadimplente.isBloqueado()) {
			throw new IllegalArgumentException(
					"Este cliente está bloqueado pois possui 3 inadimplências, pagar taxa para desbloqueio");
		}
	}

}
