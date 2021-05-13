package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.CouchDbUtil;
import me.retrodaredevil.couchdb.design.DefaultPacketsDesign;
import me.retrodaredevil.couchdb.design.SimpleView;
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
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.util.JacksonUtil;

import java.util.*;

public class CouchDbSetupMain {
	private static final ObjectMapper MAPPER = JacksonUtil.defaultMapper();
	private static void createDatabase(CouchDbInstance instance, String database) throws CouchDbException {
		if (instance.getDatabase(database).createIfNotExists()) {
			System.out.println("Created " + database);
		} else {
			System.out.println("Already exists: " + database);
		}
	}
	public static int doCouchDbSetupMain(CouchDbDatabaseSettings settings) throws CouchDbException {
		System.out.println("You will now setup your CouchDB instance! Some databases will be automatically created (enter)");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();

		CouchDbInstance instance = CouchDbUtil.createInstance(settings.getCouchProperties(), settings.getOkHttpProperties());
		createDatabase(instance, "solarthing");
		createDatabase(instance, "solarthing_events");
		createDatabase(instance, "solarthing_closed");
		createDatabase(instance, "solarthing_open");
		System.out.println("All 4 necessary databases have been created.");
		System.out.println();
		System.out.println("Now views and security will be configured for each database. Please enter the name of the user to be added as an admin to each database.");
		System.out.println("This user is commonly named 'uploader' and a password for this user needs to be configured. (Leave blank to not configure)");
		System.out.print("Name of user: ");
		String username = scanner.nextLine();
		if (username.isEmpty()) {
			username = null;
			System.out.println("No user will be added as an admin, but members will still be cleared. (Enter to confirm)");
		} else {
			System.out.println("User: " + username + " will be used. (Enter to confirm)");
		}
		scanner.nextLine();
		if (username != null) {
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
				System.out.println("The specified user exists! Continuing");
			} else {
				System.out.println("Please enter a password for the new user to be created.");
				String password = scanner.nextLine();
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

		for (String databaseName : Arrays.asList("solarthing", "solarthing_events", "solarthing_closed", "solarthing_open")) {
			System.out.println("Adding packets design to database " + databaseName);
			CouchDbDatabase database = instance.getDatabase(databaseName);
			DefaultPacketsDesign design = new DefaultPacketsDesign();
			if (!"solarthing_open".equals(databaseName)) {
				System.out.println("This database will be readonly");
				String function = "function(newDoc, oldDoc, userCtx, secObj) {\n\n  secObj.admins = secObj.admins || {};\n  secObj.admins.names = secObj.admins.names || [];\n  secObj.admins.roles = secObj.admins.roles || [];\n\n  var isAdmin = false;\n  if(userCtx.roles.indexOf('_admin') !== -1) {\n    isAdmin = true;\n  }\n  if(secObj.admins.names.indexOf(userCtx.name) !== -1) {\n    isAdmin = true;\n  }\n  for(var i = 0; i < userCtx.roles; i++) {\n    if(secObj.admins.roles.indexOf(userCtx.roles[i]) !== -1) {\n      isAdmin = true;\n    }\n  }\n\n  if(!isAdmin) {\n    throw {'unauthorized':'This is read only when unauthorized'};\n  }\n}";
				design.getViews().put("readonly_auth", new SimpleView(function));
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
				System.out.println("_design/packets document already on database: " + databaseName + ". We will not try to update it. Hopefully it is correct.");
			}
			System.out.println("Configuring security for database " + databaseName);
			DatabaseSecurity oldSecurity = database.getSecurity();
			SecurityGroup oldAdmins = oldSecurity.getAdminsOrBlank();
			final SecurityGroup newAdmins;
			if (username != null && !oldAdmins.getNamesOrEmpty().contains(username)) {
				List<String> admins = new ArrayList<>(oldAdmins.getNamesOrEmpty());
				admins.add(username);
				newAdmins = new SecurityGroup(admins, oldAdmins.getRolesOrEmpty());
			} else {
				newAdmins = oldAdmins;
			}
			// This database's security has no members (public database)
			database.setSecurity(new DatabaseSecurity(newAdmins, SecurityGroup.BLANK));
			System.out.println();
		}
		System.out.println("Completed successfully!");

		return 0;
	}
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class UserEntry {
		@JsonProperty
		private final String name;
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
}
