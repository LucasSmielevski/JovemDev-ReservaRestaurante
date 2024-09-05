package jv.triersistemas.reserva_restaurante.repository.impl;


import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jv.triersistemas.reserva_restaurante.dto.ClienteDto;
import jv.triersistemas.reserva_restaurante.entity.QClienteEntity;
import jv.triersistemas.reserva_restaurante.entity.QMesaEntity;
import jv.triersistemas.reserva_restaurante.entity.QReservaEntity;
import jv.triersistemas.reserva_restaurante.entity.QRestauranteEntity;
import jv.triersistemas.reserva_restaurante.repository.ClienteRepositoryCustom;

@Repository
public class ClienteRepositoryCustomImpl implements ClienteRepositoryCustom{

	
	@PersistenceContext
	private EntityManager em;

	final QRestauranteEntity restaurante = QRestauranteEntity.restauranteEntity;
	final QReservaEntity reserva = QReservaEntity.reservaEntity;
	final QMesaEntity mesa = QMesaEntity.mesaEntity;
	final QClienteEntity cliente = QClienteEntity.clienteEntity;
	
	@Override
	public Page<ClienteDto> buscaPaginadaClientePorNome(Pageable pageable, String searchTerm) {
		BooleanBuilder condicoes = new BooleanBuilder(); 
		
		if(Objects.nonNull(searchTerm) && !searchTerm.isEmpty()) {
			condicoes.and(cliente.nome.containsIgnoreCase(searchTerm));
		}
		
		JPAQuery<ClienteDto> query = new JPAQuery<>(em);
		query.select(Projections.constructor(ClienteDto.class, cliente))
		.from(restaurante)
		.join(restaurante.clientes, cliente)
		.where(condicoes)
		.orderBy(cliente.dataCadastro.asc(), cliente.nome.asc(), cliente.id.asc());
		
		query.limit(pageable.getPageSize());
		query.offset(pageable.getOffset());
		
		return new PageImpl<ClienteDto>(query.fetch(), pageable, query.fetchCount());
	}

}
