package me.retrodaredevil.solarthing.database.couchdb;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUnauthorizedException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUpdateConflictException;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.database.exception.NotFoundSolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.UnauthorizedSolarThingDatabaseException;
import me.retrodaredevil.solarthing.database.exception.UpdateConflictSolarThingDatabaseException;

@UtilityClass
public final class ExceptionUtil {
	private ExceptionUtil() { throw new UnsupportedOperationException(); }

	public static SolarThingDatabaseException createFromCouchDbException(CouchDbException exception) {
		return createFromCouchDbException(null, exception);
	}
	@SuppressWarnings("InconsistentOverloads") // while this is an "inconsistent overload", it is consistent with how the exception constructors work
	public static SolarThingDatabaseException createFromCouchDbException(@Nullable String message, CouchDbException exception) {
		if (exception instanceof CouchDbUnauthorizedException) {
			return new UnauthorizedSolarThingDatabaseException(message, exception);
		}
		if (exception instanceof CouchDbUpdateConflictException) {
			return new UpdateConflictSolarThingDatabaseException(message, exception);
		}
		if (exception instanceof CouchDbNotFoundException) {
			return new NotFoundSolarThingDatabaseException(message, exception);
		}
		return new SolarThingDatabaseException(message, exception);
	}
}
