package com.example.springdatamongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringdataMongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringdataMongodbApplication.class, args);
	}
}

@Component
@RequiredArgsConstructor
@Log4j2
class SampleDataInitializer {

	private final ReservationRepository reservationRepository;

//	private final

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {

		Flux<Reservation> reservations = Flux
				.just("Ali", "Amina", "Sobia", "Safi", "Asma", "Abdul", "Moiz", "Ayesha")
				.map(name -> new Reservation(null, name))
				.flatMap(this.reservationRepository::save);

		this.reservationRepository
				.deleteAll()
				.thenMany(reservations)
				.thenMany(this.reservationRepository.findAll())
				.subscribe(log::info);

//		reservations.subscribe(log::info);
	}
}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, Integer> {

	@Query("select * from reservation where name = $1 ")
	Flux<Reservation> findByName(String name);
}

//@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {

	@Id
	private Integer id;
	private String name;
}