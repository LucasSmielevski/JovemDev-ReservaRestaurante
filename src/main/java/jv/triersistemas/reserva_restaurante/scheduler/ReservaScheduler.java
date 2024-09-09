package jv.triersistemas.reserva_restaurante.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import jv.triersistemas.reserva_restaurante.service.ReservaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReservaScheduler {

	@Autowired
	private ReservaService reservaService;

//	@Scheduled(cron = "0 0 6 * * *")
	@Transactional
	@Scheduled(initialDelay = 3000, fixedDelay = 1000*10*60)
	public void concluirReservaNaoFinalizada() {
		log.info("Atualizando Reservas");
		reservaService.concluirReservaNaoFinalizada();
	}
}
