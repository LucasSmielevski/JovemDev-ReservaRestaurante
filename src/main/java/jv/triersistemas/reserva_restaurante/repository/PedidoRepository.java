package jv.triersistemas.reserva_restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jv.triersistemas.reserva_restaurante.entity.PedidoEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long>{

}
