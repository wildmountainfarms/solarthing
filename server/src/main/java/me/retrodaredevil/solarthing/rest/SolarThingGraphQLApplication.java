package me.retrodaredevil.solarthing.rest;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.graphql.servlet.GraphQlWebMvcAutoConfiguration;

//@SpringBootApplication(exclude = {GraphQlWebMvcAutoConfiguration.class})
@SpringBootApplication
@NullMarked
public class SolarThingGraphQLApplication {

	public SolarThingGraphQLApplication() {
		// constructor is blank to satisfy ErrorProne. This class instantiated by spring for some reason
	}

	public static void main(String[] args) {
		SpringApplication.run(SolarThingGraphQLApplication.class, args);
	}

}
