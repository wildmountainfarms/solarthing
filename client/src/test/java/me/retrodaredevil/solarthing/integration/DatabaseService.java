package me.retrodaredevil.solarthing.integration;

public enum DatabaseService {
	COUCHDB("couchdb"),
	;

	private final String serviceName;
	DatabaseService(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getHost() {
		String key = serviceName + ".host";
		String host = System.getProperty(key);
		if (host == null) {
			throw new IllegalStateException("System property does not exist for key: " + key);
		}
		return host;
	}
	public int getPort() {
		// The gradle compose plugin sets system properties for the services defined in the docker compose file:
		//   https://github.com/avast/gradle-docker-compose-plugin
		String key = serviceName + ".tcp.5984";
		String portString = System.getProperty(key);
		if (portString == null) {
			throw new IllegalStateException("System property does not exist for key: " + key);
		}
		return Integer.parseInt(portString);
	}
	public static DatabaseService[] couchOnly() {
		return new DatabaseService[]{COUCHDB};
	}
	public static DatabaseService[] all() {
		return values();
	}
}
