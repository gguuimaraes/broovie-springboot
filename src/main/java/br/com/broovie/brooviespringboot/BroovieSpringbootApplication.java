package br.com.broovie.brooviespringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BroovieSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BroovieSpringbootApplication.class, args);
	}
}
