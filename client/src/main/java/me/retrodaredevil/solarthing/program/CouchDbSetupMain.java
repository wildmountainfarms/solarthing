package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdb.design.MutablePacketsDesign;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUpdateConflictException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.couchdbjava.security.DatabaseSecurity;
import me.retrodaredevil.couchdbjava.security.SecurityGroup;
import me.retrodaredevil.solarthing.SolarThingDatabaseType;
import me.retrodaredevil.solarthing.annotations.NotNull;
import me.retrodaredevil.solarthing.annotations.Nullable;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class CouchDbSetupMain {
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();

	private final CouchDbInstance instance;
	private final PrintStream out;
	private final Prompt prompt;

	public CouchDbSetupMain(CouchDbInstance instance, PrintStream out, Prompt prompt) {
		this.instance = instance;
		this.out = out;
		this.prompt = prompt;
	}
	public static CouchDbSetupMain createFrom(CouchDbDatabaseSettings settings) {
		CouchDbInstance instance = CouchDbUtil.createInstance(settings.getCouchProperties(), settings.getOkHttpProperties());
		Scanner scanner = new Scanner(System.in, Charset.defaultCharset());
		PrintStream out = System.out;
		return new CouchDbSetupMain(instance, out, new ScannerPrompt(scanner));
	}

	private void createDatabase(String database) throws CouchDbException {
		if (instance.getDatabase(database).createIfNotExists()) {
			out.println("Created " + database);
		} else {
			out.println("Already exists: " + database);
		}
	}

	private void createUserIfNotExists(String username, SolarThingDatabaseType.UserType userType) throws CouchDbException {
		CouchDbDatabase usersDatabase = instance.getUsersDatabase();
		String documentId = "org.couchdb.user:" + username;

		DocumentData userDocumentData = null;
		try {
			userDocumentData = usersDatabase.getDocument(documentId);
		} catch (CouchDbNotFoundException ignored) {
		}
		final UserEntry user;
		if (userDocumentData != null) {
			try {
				user = CouchDbJacksonUtil.readValue(MAPPER, userDocumentData.getJsonData(), UserEntry.class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Could not parse user document data. Please report this bug", e);
			}
		} else {
			user = null;
		}
		if (user != null) {
			out.println("User: " + username + " exists! Continuing.");
		} else {
			out.println("Please enter a password for '" + username + "'.");
			String password = prompt.promptUserPassword(userType);
			final JsonData jsonData;
			try {
				jsonData = new StringJsonData(MAPPER.writeValueAsString(new UserEntry(username, password)));
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Couldn't serialize json! Report this!", e);
			}
			usersDatabase.createIfNotExists();
			usersDatabase.putDocument(documentId, jsonData);
		}

	}
	private void addPacketsDesign(SolarThingDatabaseType databaseType, CouchDbDatabase database) throws CouchDbException {
		out.println("Adding packets design to database " + databaseType.getName());
		MutablePacketsDesign design = new MutablePacketsDesign();
		if (databaseType.needsMillisView()) {
			out.println("This database will have the millisNull view");
			design.addMillisNullView();
		}
		if (databaseType.needsSimpleAllDocsView()) {
			out.println("This database will have the simpleAllDocs view");
			design.addSimpleAllDocsView();
		}
		if (databaseType.needsReadonlyValidateFunction()) {
			out.println("This database will be readonly");
			design.setReadonlyAuth();
		}
		final JsonData jsonData;
		try {
			jsonData = new StringJsonData(MAPPER.writeValueAsString(design));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Couldn't serialize json! Report this!", e);
		}
		try {
			database.putDocument("_design/packets", jsonData);
		} catch (CouchDbUpdateConflictException e) {
			String revision = database.getCurrentRevision("_design/packets");
			database.updateDocument("_design/packets", revision, jsonData);
			out.println("updated _design/packets document on database: " + databaseType.getName());
		}
	}

	private void addPacketFiltersDesign(SolarThingDatabaseType databaseType, CouchDbDatabase database) throws CouchDbException {
		MutablePacketsDesign packetFiltersDesign = new MutablePacketsDesign();
		packetFiltersDesign.addLast24HoursFilter();

		final JsonData jsonData;
		try {
			jsonData = new StringJsonData(MAPPER.writeValueAsString(packetFiltersDesign));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Couldn't serialize json! Report this!", e);
		}
		try {
			database.putDocument("_design/packetFilters", jsonData);
		} catch (CouchDbUpdateConflictException e) {
			String revision = database.getCurrentRevision("_design/packetFilters");
			database.updateDocument("_design/packets", revision, jsonData);
			out.println("updated _design/packetFilters document on database: " + databaseType.getName());
		}
	}

	public int doCouchDbSetupMain() throws CouchDbException {
		out.println("You will now setup your CouchDB instance! Some databases will be automatically created (enter)");
		prompt.promptContinue();

		for (SolarThingDatabaseType databaseType : SolarThingDatabaseType.values()) {
			createDatabase(databaseType.getName());
		}
		out.println("All necessary databases have been created.");
		out.println();
		out.println("Now views and security will be configured for each database. Please enter the name of the user to be added as an admin to each database.");
		out.println("This user is commonly named 'uploader'. (Leave blank to not configure)");
		out.print("Name of user: ");
		String uploaderUser = prompt.promptUserName(SolarThingDatabaseType.UserType.UPLOADER);
		if (uploaderUser == null) {
			out.println("No user will be added as an admin, but members will still be cleared. (Enter to confirm)");
		} else {
			out.println("User: " + uploaderUser + " will be used. (Enter to confirm)");
		}
		prompt.promptContinue();
		if (uploaderUser != null) {
			createUserIfNotExists(uploaderUser, SolarThingDatabaseType.UserType.UPLOADER);
		}

		out.println("You can also enter the name of the user to manage the solarthing_cache and solarthing_alter databases.");
		out.println("This user is commonly named 'manager'. (Leave blank to not configure)" + (uploaderUser == null ? "" : " (Use '" + uploaderUser + "' to use same user to manage the cache database)"));
		String managerUser = prompt.promptUserName(SolarThingDatabaseType.UserType.MANAGER);
		if (managerUser == null) {
			out.println("No user will be configured to manage the solarthing_cache and solarthing_alter database. (Enter to confirm)");
		} else {
			out.println("User: " + managerUser + " will be used to manage solarthing_cache and solarthing_alter. (Enter to confirm)");
		}
		prompt.promptContinue();
		if (managerUser != null && !managerUser.equals(uploaderUser)) {
			createUserIfNotExists(managerUser, SolarThingDatabaseType.UserType.MANAGER);
		}

		out.println();

		for (SolarThingDatabaseType databaseType : SolarThingDatabaseType.values()) {
			CouchDbDatabase database = instance.getDatabase(databaseType.getName());
			if (databaseType.needsAnyViews()) {
				addPacketsDesign(databaseType, database);
			}
			if (databaseType.needsMillisView()) {
				// if it needs millisNull, it must have a dateMillis field so we can add _design/packetFilters
				addPacketFiltersDesign(databaseType, database);
			}
			out.println("Configuring security for database " + databaseType.getName());
			DatabaseSecurity oldSecurity = database.getSecurity();
			SecurityGroup newAdmins = oldSecurity.getAdminsOrBlank(); // First initialize newAdmins to the old admins
			Set<SolarThingDatabaseType.UserType> usersWithWritePermission = databaseType.getUsersWithWritePermission();
			if (usersWithWritePermission.contains(SolarThingDatabaseType.UserType.MANAGER)) {
				newAdmins = newAdmins.withName(managerUser);
			}
			if (usersWithWritePermission.contains(SolarThingDatabaseType.UserType.UPLOADER)) {
				newAdmins = newAdmins.withName(uploaderUser);
			}
			database.setSecurity(new DatabaseSecurity(
					newAdmins, // update the list of admins
					databaseType.isPublic() ? SecurityGroup.BLANK : oldSecurity.getMembers() // if database is public, this has no members, if private, keep old members which should include an _admin role
			));
			out.println();
		}
		out.println("Completed successfully!");

		return 0;
	}
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class UserEntry {
		@JsonProperty
		private final String name;
		@SuppressWarnings("FieldCanBeStatic")
		@JsonProperty
		private final String type = "user";
		@JsonProperty
		private final String password;
		@JsonProperty
		private final List<String> roles = Collections.emptyList();

		@JsonCreator
		private UserEntry(@JsonProperty("name") String name) {
			this.name = name;
			this.password = null;
		}
		private UserEntry(String name, String password) {
			this.name = name;
			this.password = password;
		}
	}
	public interface Prompt {
		void promptContinue();
		@Nullable String promptUserName(SolarThingDatabaseType.UserType userType);
		@NotNull String promptUserPassword(SolarThingDatabaseType.UserType userType);
	}
	public static class ScannerPrompt implements Prompt {

		private final Scanner scanner;

		public ScannerPrompt(Scanner scanner) {
			this.scanner = scanner;
		}

		@Override
		public void promptContinue() {
			scanner.nextLine();
		}

		@Override
		public @Nullable String promptUserName(SolarThingDatabaseType.UserType userType) {
			String r = scanner.nextLine();
			if (r.isEmpty()) {
				return null;
			}
			return r;
		}

		@Override
		public @NotNull String promptUserPassword(SolarThingDatabaseType.UserType userType) {
			return scanner.nextLine();
		}
	}
}
