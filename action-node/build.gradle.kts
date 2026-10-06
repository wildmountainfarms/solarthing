plugins {
	id("buildlogic.java-common-conventions")
	`java-library`
}

version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
	annotationProcessor(project(":process-annotations"))

	api("com.github.retrodaredevil:action-lib:v1.3.1")
	api(libs.jackson.annotations)
	api(libs.jackson.databind)
}
