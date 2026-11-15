package me.retrodaredevil.solarthing.config.databases;

import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.config.databases.implementations.*;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.SubtypeResolver;
import tools.jackson.databind.cfg.MapperBuilder;

@UtilityClass
@NullMarked
public class DatabaseSettingsUtil {
	private DatabaseSettingsUtil() { throw new UnsupportedOperationException(); }

	public static <M extends ObjectMapper, B extends MapperBuilder<M, B>> B registerDatabaseSettings(B builder) {
		registerDatabaseSettingsSubtypes(builder.subtypeResolver());
		return builder;
	}
	public static void registerDatabaseSettingsSubtypes(SubtypeResolver subtypeResolver) {
		subtypeResolver.registerSubtypes(
				DatabaseSettings.class,
				CouchDbDatabaseSettings.class,
				InfluxDbDatabaseSettings.class,
				InfluxDb2DatabaseSettings.class,
				LatestFileDatabaseSettings.class,
				PostDatabaseSettings.class,
				MqttDatabaseSettings.class
		);
	}
}
