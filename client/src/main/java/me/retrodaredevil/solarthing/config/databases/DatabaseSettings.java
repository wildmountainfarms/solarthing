package me.retrodaredevil.solarthing.config.databases;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXTERNAL_PROPERTY, // the type is defined one level higher
		property = "type"
)
public interface DatabaseSettings {
	DatabaseType getDatabaseType();
}
