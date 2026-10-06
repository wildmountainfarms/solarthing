plugins {
	id("buildlogic.java-common-conventions")
	`java-library`
}

java {
	// Staying on Java 11 to avoid accidentally using APIs incompatible with older Android versions
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

version = "0.0.1-SNAPSHOT"

dependencies {
	annotationProcessor(project(":process-annotations"))
	api(libs.io.lib.core)
	api(libs.couchdb.java)
	// Currently, this commented line isn't used. If we add more modules to couchdb-java in the future, we may have to do this.
	//   My guess is that Jitpack sees we only have a single module in couchdb-java and does something special with it.
//    api("com.github.retrodaredevil.couchdb-java:couchdb:$couchdbJavaVersion")

	// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
	api(libs.slf4j.api)

	api(libs.jackson.core)
	api(libs.jackson.annotations)
	api(libs.jackson.databind)

	api(libs.jackson.datatype.jsr310) // heartbeat data uses java time stuff and serviceapi uses this

	// https://mvnrepository.com/artifact/org.apache.commons/commons-text
	implementation("org.apache.commons:commons-text:1.15.0")

	testImplementation(libs.archunit.junit5)
}
