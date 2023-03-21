package me.retrodaredevil.solarthing.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SolarThingGraphQLApplication {

	public SolarThingGraphQLApplication() {
		// constructor is blank to satisfy ErrorProne. This class instantiated by spring for some reason
	}

	public static void main(String[] args) {
		SpringApplication.run(SolarThingGraphQLApplication.class, args);
	}

}
