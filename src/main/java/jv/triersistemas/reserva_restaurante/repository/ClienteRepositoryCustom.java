package jv.triersistemas.reserva_restaurante.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jv.triersistemas.reserva_restaurante.dto.ClienteDto;
import jv.triersistemas.reserva_restaurante.entity.ClienteEntity;

public interface ClienteRepositoryCustom {
	
	Page<ClienteDto> buscaPaginadaClientePorNome(Pageable pageable, String searchTerm);
	
	List<ClienteEntity> buscarClientesComMaiorValorGasto();
}
