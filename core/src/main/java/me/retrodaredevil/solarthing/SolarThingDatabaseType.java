package me.retrodaredevil.solarthing;

import me.retrodaredevil.solarthing.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static me.retrodaredevil.solarthing.SolarThingConstants.*;

public enum SolarThingDatabaseType {
	/**
	 * The status database
	 * <p>
	 * Write Access: uploader user
	 * <p>
	 * Read Access: public
	 */
	STATUS(STATUS_DATABASE, true),
	/**
	 * The event database
	 * <p>
	 * Write Access: uploader user
	 * <p>
	 * Read Access: public
	 */
	EVENT(EVENT_DATABASE, true),
	/**
	 * The closed database
	 * <p>
	 * Write Access: super admins (no user)
	 * <p>
	 * Read Access: public
	 */
	CLOSED(CLOSED_DATABASE, false),
	/**
	 * The open database
	 * <p>
	 * Write Access: public
	 * <p>
	 * Read Access: public
	 */
	OPEN(OPEN_DATABASE, true),
	/**
	 * The cache database
	 * <p>
	 * Write Access: manager user
	 * <p>
	 * Read Access: manager user
	 */
	CACHE(CACHE_DATABASE, false),
	/**
	 * The alter database
	 * <p>
	 * Write Access: manager user
	 * <p>
	 * Read Access: public
	 */
	ALTER(ALTER_DATABASE, false),
	;
	private final String name;
	private final boolean needsMillisView;

	SolarThingDatabaseType(String name, boolean needsMillisView) {
		this.name = name;
		this.needsMillisView = needsMillisView;
	}

	public boolean needsAnyViews() {
		return needsMillisView() || needsReadonlyValidateFunction() || needsSimpleAllDocsView();
	}

	public boolean needsMillisView() {
		return needsMillisView;
	}

	public boolean needsReadonlyValidateFunction() {
		// open database is only database where anyone can add documents to it even if they're unauthorized
		// cache doesn't need to be readonly because it is not public
		return this != OPEN && this != CACHE;
	}
	public boolean needsSimpleAllDocsView() {
		return this == ALTER;
	}

	/**
	 * @return true if this database cannot have document added to by anyone except super admins
	 */
	public boolean isReadonlyByAll() {
		return this == CLOSED;
	}
	public @NotNull Set<UserType> getUsersWithWritePermission() {
		if (this == CLOSED || this == OPEN) { // This database is readonly by all
			// closed is readonly by all users
			// open already has permissive upload ability, so we don't need any user to get admin access
			return Collections.emptySet();
		}
		if (this == CACHE || this == ALTER) {
			return Collections.singleton(UserType.MANAGER);
		}
		if (this == EVENT) {
			// When the automation program is running the alter manager action, we want the manager user to upload to the events database
			return EnumSet.of(UserType.UPLOADER, UserType.MANAGER);
		}
		return Collections.singleton(UserType.UPLOADER);
	}
	public boolean isPublic() {
		// all except CACHE need to be public because for all the other databases, we want to allow anyone to read them.
		// status, event -> anyone can read data
		// closed -> anyone can read metadata
		// open -> anyone can add documents
		// cache -> only people with access to this should be people with write access
		return this != CACHE;
	}

	public String getName() {
		return name;
	}

	public enum UserType {
		UPLOADER("uploader"),
		MANAGER("manager"),
		;
		private final String recommendedName;
		UserType(String recommendedName) {
			this.recommendedName = recommendedName;
		}

		public String getRecommendedName() {
			return recommendedName;
		}
	}
}
