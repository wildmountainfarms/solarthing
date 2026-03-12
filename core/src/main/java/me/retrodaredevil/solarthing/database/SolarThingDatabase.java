package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.type.closed.meta.RootMetaPacket;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public interface SolarThingDatabase {

	DatabaseManagementSource getDatabaseManagementSource();

	/**
	 *
	 * @param updateToken The update token to validate
	 */
	@Contract("_ -> param1")
	UpdateToken validateUpdateToken(UpdateToken updateToken);

	MillisDatabase getStatusDatabase();
	MillisDatabase getEventDatabase();
	MillisDatabase getOpenDatabase();

	AlterDatabase getAlterDatabase();

	/**
	 * There is no object to represent the closed database, so you can call this method to get its {@link DatabaseSource}
	 * @return The DatabaseSource of the solarthing_closed database
	 */
	DatabaseSource getClosedDatabaseSource();

	/**
	 * There is not object to represent the cache database, so you can call thsi method to get its {@link DatabaseSource}
	 * @return The DatabaseSource of the solarthing_cache database
	 */
	DatabaseSource getCacheDatabaseSource();

	/**
	 * @param updateToken The update token. null will be returned if the meta document has not been updated since this update token. If null, the returned packet will not be null
	 * @return The packet or null if it has not been updated since {@code updateToken}
	 * @throws SolarThingDatabaseException Thrown on connection errors or parsing errors
	 */
	@Contract("null -> !null")
	@Nullable VersionedPacket<RootMetaPacket> queryMetadata(@Nullable UpdateToken updateToken) throws SolarThingDatabaseException;
	default VersionedPacket<RootMetaPacket> queryMetadata() throws SolarThingDatabaseException { return requireNonNull(queryMetadata(null)); }

	/**
	 * @param updateToken The update token. null will be returned if the meta document has not been updated since this update token. If null, the returned packet will not be null
	 * @return The packet or null if it has not been updated since {@code updateToken}
	 * @throws me.retrodaredevil.solarthing.database.exception.NotFoundSolarThingDatabaseException Thrown if the packet does not exist
	 * @throws SolarThingDatabaseException Thrown on connection errors or parsing errors
	 */
	@Contract("null -> !null")
	@Nullable VersionedPacket<AuthorizationPacket> queryAuthorized(@Nullable UpdateToken updateToken) throws SolarThingDatabaseException;
	default VersionedPacket<AuthorizationPacket> queryAuthorized() throws SolarThingDatabaseException { return requireNonNull(queryAuthorized(null)); }

	void updateAuthorized(AuthorizationPacket authorizationPacket, @Nullable UpdateToken updateToken) throws SolarThingDatabaseException;

	SessionInfo getSessionInfo() throws SolarThingDatabaseException;
}
