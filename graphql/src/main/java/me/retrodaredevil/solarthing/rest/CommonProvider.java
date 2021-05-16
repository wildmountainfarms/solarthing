package me.retrodaredevil.solarthing.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.DatabaseSettings;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.packets.collection.DefaultInstanceOptions;
import me.retrodaredevil.solarthing.packets.instance.InstanceSourcePacket;
import me.retrodaredevil.solarthing.program.DatabaseConfig;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class CommonProvider {
	@Value("${solarthing.config.database}")
	private File databaseFile;
	@Value("${solarthing.config.default_source:#{null}}")
	private @Nullable String defaultSourceId;
	@Value("${solarthing.config.default_fragment:#{null}}")
	private @Nullable Integer defaultFragmentId;

	private DefaultInstanceOptions defaultInstanceOptions;
	private CouchDbDatabaseSettings couchDbDatabaseSettings;


	private String getDefaultSourceId() {
		String r = defaultSourceId;
		if (r == null) {
			return InstanceSourcePacket.UNUSED_SOURCE_ID;
		}
		return r;
	}

	private int getDefaultFragmentId() {
		Integer r = defaultFragmentId;
		if (r == null) {
			return DefaultInstanceOptions.DEFAULT_DEFAULT_INSTANCE_OPTIONS.getDefaultFragmentId();
		}
		return r;
	}

	@PostConstruct
	public void init() {
		defaultInstanceOptions = DefaultInstanceOptions.create(getDefaultSourceId(), getDefaultFragmentId());
		System.out.println("Using defaultInstanceOptions=" + defaultInstanceOptions);

		System.out.println(new File(".").getAbsolutePath());
		System.out.println(databaseFile.getAbsolutePath());

		ObjectMapper objectMapper = JacksonUtil.defaultMapper();
		objectMapper.getSubtypeResolver().registerSubtypes(
				DatabaseSettings.class,
				CouchDbDatabaseSettings.class
		);
		final FileInputStream reader;
		try {
			reader = new FileInputStream(databaseFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		final DatabaseConfig databaseConfig;
		try {
			databaseConfig = objectMapper.readValue(reader, DatabaseConfig.class);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't parse data!", e);
		}
		DatabaseSettings databaseSettings = databaseConfig.getSettings();
		if(!(databaseSettings instanceof CouchDbDatabaseSettings)) {
			throw new UnsupportedOperationException("Only CouchDB is supported right now!");
		}
		couchDbDatabaseSettings = (CouchDbDatabaseSettings) databaseSettings;
	}

	@Bean
	public DefaultInstanceOptions defaultInstanceOptions() {
		return defaultInstanceOptions;
	}

	@Bean
	public CouchDbDatabaseSettings couchDbDatabaseSettings() {
		return couchDbDatabaseSettings;
	}

}
