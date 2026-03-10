plugins {
	`maven-publish`
	`java-library`
}

repositories {
	maven { url = uri("https://jitpack.io") }
	maven { url = uri("https://repo.maven.apache.org/maven2") }
	mavenCentral()
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			// thanks https://stackoverflow.com/a/68515859/5434860
			groupId = group.toString()
			artifactId = project.name
			version = version.toString()

			from(components["java"])
		}
	}
}
