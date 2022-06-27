package me.retrodaredevil.solarthing.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SolarThingGraphQLApplication {
	private SolarThingGraphQLApplication() { throw new UnsupportedOperationException(); }

	public static void main(String[] args) {
		SpringApplication.run(SolarThingGraphQLApplication.class, args);
	}

}
