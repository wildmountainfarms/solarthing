package me.retrodaredevil.solarthing.rest.spring;

import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Map;

@Component
public class ApplicationConfig {

	@Bean
	public CustomEditorConfigurer customEditorConfigurer() {
		CustomEditorConfigurer configurer = new CustomEditorConfigurer();
		configurer.setCustomEditors(Map.of(
				Path.class, NioPathPropertyEditorSupport.class
		));

		return configurer;
	}
}
