package jv.triersistemas.reserva_restaurante.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jv.triersistemas.reserva_restaurante.dto.ClienteDto;
import jv.triersistemas.reserva_restaurante.dto.RestauranteDto;
import jv.triersistemas.reserva_restaurante.enums.SexoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "cliente")
public class ClienteEntity extends PessoaBase {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private LocalDate dataCadastro;

	private int quantidadeReservas;

	private BigDecimal quantidadeValorGasto;

	private boolean bloqueado;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinColumn(name = "restaurante_id", nullable = false)
	@JsonIgnore
	private RestauranteEntity restaurante;

	public ClienteEntity(ClienteDto dto) {
		this.id = dto.getId();
		this.nome = dto.getNome();
		this.sobrenome = dto.getSobrenome();
		this.cpf = dto.getCpf();
		this.dataNascimento = dto.getDataNascimento();
		this.sexo = Objects.requireNonNullElse(dto.getSexo(), SexoEnum.INDEFINIDO);
		this.telefone = dto.getTelefone();
		this.dataCadastro = Objects.requireNonNullElse(dto.getDataCadastro(), LocalDate.now());
		this.quantidadeReservas = Objects.requireNonNullElse(dto.getQuantidadeReservas(), 0);
		this.quantidadeValorGasto = Objects.requireNonNullElse(dto.getQuantidadeValorGasto(), BigDecimal.ZERO);
		this.bloqueado = Objects.requireNonNullElse(dto.isBloqueado(), false);
	}

	public ClienteEntity atualizaCliente(ClienteDto dto) {
		this.nome = dto.getNome();
		this.sobrenome = dto.getSobrenome();
		this.cpf = dto.getCpf();
		this.dataNascimento = dto.getDataNascimento();
		this.sexo = dto.getSexo();
		this.telefone = dto.getTelefone();
		this.bloqueado = dto.isBloqueado();
		return this;
	}
}
