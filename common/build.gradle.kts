plugins {
	id("buildlogic.java-common-conventions")
	`java-library`
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

version = "0.0.1-SNAPSHOT"

dependencies {
	api(project(":core"))
	api(project(":action-lang"))
	annotationProcessor(project(":process-annotations"))
	// https://mvnrepository.com/artifact/org.apache.commons/commons-text
	implementation("org.apache.commons:commons-text:1.13.1")
}
