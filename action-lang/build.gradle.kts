plugins {
	id("buildlogic.java-common-conventions")
	`java-library`
}

version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
	api(project(":notation-script")) {
		// we do this because https://github.com/gradle/gradle/issues/820
		exclude(group = "org.antlr", module = "antlr-runtime")
		exclude(group = "org.antlr", module = "antlr4")
		// We exclude antlr4-runtime because we add it below as an api dependency rather than an implementation dependency
		exclude(group = "org.antlr", module = "antlr4-runtime")
		exclude(group = "org.antlr", module = "ST4")
	}
	api(project(":action-node"))
	annotationProcessor(project(":process-annotations"))

	// https://mvnrepository.com/artifact/org.antlr/antlr4-runtime
	api(libs.antlr4.runtime)
}
