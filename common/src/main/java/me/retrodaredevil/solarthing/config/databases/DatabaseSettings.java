package me.retrodaredevil.solarthing.config.databases;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DatabaseSettings {
	DatabaseType getDatabaseType();
}
