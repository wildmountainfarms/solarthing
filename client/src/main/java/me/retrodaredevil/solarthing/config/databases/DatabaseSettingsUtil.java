package me.retrodaredevil.solarthing.config.databases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.implementations.*;

@UtilityClass
public class DatabaseSettingsUtil {
	private DatabaseSettingsUtil() { throw new UnsupportedOperationException(); }

	public static ObjectMapper registerDatabaseSettings(ObjectMapper mapper) {
		registerDatabaseSettingsSubtypes(mapper.getSubtypeResolver());
		return mapper;
	}
	public static void registerDatabaseSettingsSubtypes(SubtypeResolver subtypeResolver) {
		subtypeResolver.registerSubtypes(
				DatabaseSettings.class,
				CouchDbDatabaseSettings.class,
				InfluxDbDatabaseSettings.class,
				InfluxDb2DatabaseSettings.class,
				LatestFileDatabaseSettings.class,
				PostDatabaseSettings.class
		);
	}
}
