package me.retrodaredevil.solarthing.database;

import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.type.closed.authorization.AuthorizationPacket;
import me.retrodaredevil.solarthing.database.exception.SolarThingDatabaseException;
import me.retrodaredevil.solarthing.type.closed.meta.RootMetaPacket;
import org.jetbrains.annotations.Contract;

import static java.util.Objects.requireNonNull;

public interface SolarThingDatabase {

	/**
	 *
	 * @param updateToken The update token to validate
	 */
	@Contract("_ -> param1")
	@NotNull UpdateToken validateUpdateToken(@NotNull UpdateToken updateToken);

	@NotNull MillisDatabase getStatusDatabase();
	@NotNull MillisDatabase getEventDatabase();
	@NotNull MillisDatabase getOpenDatabase();

	@NotNull AlterDatabase getAlterDatabase();

	/**
	 * @param updateToken The update token. null will be returned if the meta document has not been updated since this update token. If null, the returned packet will not be null
	 * @return The packet or null if it has not been updated since {@code updateToken}
	 * @throws SolarThingDatabaseException Thrown on connection errors or parsing errors
	 */
	@Nullable VersionedPacket<RootMetaPacket> queryMetadata(UpdateToken updateToken) throws SolarThingDatabaseException;
	default @NotNull VersionedPacket<RootMetaPacket> queryMetadata() throws SolarThingDatabaseException { return requireNonNull(queryMetadata(null)); }

	/**
	 * @param updateToken The update token. null will be returned if the meta document has not been updated since this update token. If null, the returned packet will not be null
	 * @return The packet or null if it has not been updated since {@code updateToken}
	 * @throws me.retrodaredevil.solarthing.database.exception.NotFoundSolarThingDatabaseException Thrown if the packet does not exist
	 * @throws SolarThingDatabaseException Thrown on connection errors or parsing errors
	 */
	@Nullable VersionedPacket<AuthorizationPacket> queryAuthorized(UpdateToken updateToken) throws SolarThingDatabaseException;
	default @NotNull VersionedPacket<AuthorizationPacket> queryAuthorized() throws SolarThingDatabaseException { return requireNonNull(queryAuthorized(null)); }

	void updateAuthorized(@NotNull AuthorizationPacket authorizationPacket, @Nullable UpdateToken updateToken) throws SolarThingDatabaseException;

	@NotNull SessionInfo getSessionInfo() throws SolarThingDatabaseException;
}
