package jv.triersistemas.reserva_restaurante.service.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jv.triersistemas.reserva_restaurante.dto.RestauranteDto;
import jv.triersistemas.reserva_restaurante.entity.RestauranteEntity;
import jv.triersistemas.reserva_restaurante.repository.RestauranteRepository;
import jv.triersistemas.reserva_restaurante.service.RestauranteService;

@Service
public class RestauranteServiceImpl implements RestauranteService {

	@Autowired
	private RestauranteRepository repository;

	@Override
	public RestauranteDto adicionarRestaurante(RestauranteDto novoRestaurante) {
		String cnpjSemCaracteres = limparCnpj(novoRestaurante.getCnpj());
		RestauranteEntity restaurante = new RestauranteEntity(novoRestaurante);
		restaurante.setCnpj(cnpjSemCaracteres);
		return new RestauranteDto(repository.save(restaurante));
	}

	@Override
	public List<RestauranteDto> getTodosRestaurantes() {
		List<RestauranteEntity> listaRestaurantes = repository.findAll();

		for (RestauranteEntity restaurante : listaRestaurantes) {
			Hibernate.initialize(restaurante.getClientes());
		}

		return listaRestaurantes.stream().map(RestauranteDto::new).toList();
	}
	
	@Override
	public RestauranteDto obterRestaurantePorId(Long id) {
		Optional<RestauranteEntity> restauranteOpt = repository.findById(id);
		if (restauranteOpt.isPresent()) {
			RestauranteEntity restaurante = restauranteOpt.get();
			Hibernate.initialize(restaurante.getClientes());
			return new RestauranteDto(restaurante);
		}
		return null;
	}

	@Override
	public RestauranteDto atualizarRestaurante(Long id, RestauranteDto restauranteAtualizado) {
		Optional<RestauranteEntity> restaurantePorId = repository.findById(id);
		if (restaurantePorId.isPresent()) {
			restaurantePorId.get().atualizaRestaurante(restauranteAtualizado);
			var entidadePersistida = repository.save(restaurantePorId.get());
			return new RestauranteDto(entidadePersistida);
		}
		return null;
	}

	public String limparCnpj(String cnpj) {
		return cnpj.replaceAll("[^\\d]", "");
	}


}
