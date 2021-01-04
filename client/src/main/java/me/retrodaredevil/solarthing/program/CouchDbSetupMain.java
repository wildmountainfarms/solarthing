package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdb.DocumentWrapper;
import me.retrodaredevil.couchdb.EktorpUtil;
import me.retrodaredevil.couchdb.design.DefaultPacketsDesign;
import me.retrodaredevil.couchdb.design.SimpleView;
import me.retrodaredevil.solarthing.config.databases.implementations.CouchDbDatabaseSettings;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.ektorp.*;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.impl.StdObjectMapperFactory;

import java.util.*;

public class CouchDbSetupMain {
	private static void createDatabase(CouchDbInstance instance, String database) {
		if (instance.createDatabaseIfNotExists(database)) {
			System.out.println("Created " + database);
		} else {
			System.out.println("Already exists: " + database);
		}
	}
	public static int doCouchDbSetupMain(CouchDbDatabaseSettings settings) {
		System.out.println("You will now setup your CouchDB instance! Some databases will be automatically created (enter)");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();

		HttpClient httpClient = EktorpUtil.createHttpClient(settings.getCouchProperties());
		CouchDbInstance instance = new StdCouchDbInstance(httpClient);
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
			CouchDbConnector usersClient = new StdCouchDbConnector("_users", instance, new StdObjectMapperFactory() {
				@Override
				protected void applyDefaultConfiguration(ObjectMapper om) {
					JacksonUtil.defaultMapper(om);
				}
			});

			UserEntry user = null;
			try {
				user = usersClient.get(UserEntry.class, "org.couchdb.user:" + username);
			} catch (DocumentNotFoundException ignored) {
			}
			if (user != null) {
				System.out.println("The specified user exists! Continuing");
			} else {
				DocumentWrapper wrapper = new DocumentWrapper("org.couchdb.user:" + username);
				System.out.println("Please enter a password for the new user to be created.");
				String password = scanner.nextLine();
				wrapper.setObject(new UserEntry(username, password));
				usersClient.update(wrapper);
			}
		}

		for (String database : Arrays.asList("solarthing", "solarthing_events", "solarthing_closed", "solarthing_open")) {
			System.out.println("Adding packets design to database " + database);
			CouchDbConnector client = new StdCouchDbConnector(database, instance, new StdObjectMapperFactory(){
				@Override
				protected void applyDefaultConfiguration(ObjectMapper om) {
					JacksonUtil.defaultMapper(om);
				}
			});
			DocumentWrapper documentWrapper = new DocumentWrapper("_design/packets");
			DefaultPacketsDesign design = new DefaultPacketsDesign();
			if (!"solarthing_open".equals(database)) {
				System.out.println("This database will be readonly");
				String function = "function(newDoc, oldDoc, userCtx, secObj) {\n\n  secObj.admins = secObj.admins || {};\n  secObj.admins.names = secObj.admins.names || [];\n  secObj.admins.roles = secObj.admins.roles || [];\n\n  var isAdmin = false;\n  if(userCtx.roles.indexOf('_admin') !== -1) {\n    isAdmin = true;\n  }\n  if(secObj.admins.names.indexOf(userCtx.name) !== -1) {\n    isAdmin = true;\n  }\n  for(var i = 0; i < userCtx.roles; i++) {\n    if(secObj.admins.roles.indexOf(userCtx.roles[i]) !== -1) {\n      isAdmin = true;\n    }\n  }\n\n  if(!isAdmin) {\n    throw {'unauthorized':'This is read only when unauthorized'};\n  }\n}";
				design.getViews().put("readonly_auth", new SimpleView(function));
			}
			documentWrapper.setObject(design);
			try {
				client.create(documentWrapper);
			} catch (UpdateConflictException e) {
				System.out.println("_design/packets document already on database: " + database + ". We will not try to update it. Hopefully it is correct.");
			}
			System.out.println("Configuring security for database " + database);
			Security oldSecurity = client.getSecurity();
			SecurityGroup members = oldSecurity.getMembers();
			SecurityGroup admins = oldSecurity.getAdmins();
			if (members == null) {
				members = new SecurityGroup();
			} else {
				members = new SecurityGroup(
						members.getNames() == null ? new ArrayList<>() : members.getNames(),
						members.getRoles() == null ? new ArrayList<>() : members.getRoles()
				);
			}
			if (admins == null) {
				admins = new SecurityGroup();
			} else {
				admins = new SecurityGroup(
						admins.getNames() == null ? new ArrayList<>() : admins.getNames(),
						admins.getRoles() == null ? new ArrayList<>() : admins.getRoles()
				);
			}
			members.getNames().clear();
			members.getRoles().clear();
			if (username != null) {
				if (!admins.getNames().contains(username)) {
					admins.getNames().add(username);
				}
			}
			client.updateSecurity(new Security(admins, members));
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
