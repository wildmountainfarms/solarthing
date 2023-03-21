package me.retrodaredevil.solarthing.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
	// thanks https://stackoverflow.com/a/42998817/5434860
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/{spring:[a-zA-Z0-9-_]+}")
				.setViewName("forward:/");
	}
}
