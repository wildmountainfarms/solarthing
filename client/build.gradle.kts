plugins {
	alias(libs.plugins.shadow)
	alias(libs.plugins.gradle.docker.compose)
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
	api(project(":common"))
	api(project(":serviceapi"))
	annotationProcessor(project(":process-annotations"))

	implementation(libs.io.lib.jserialcomm)
	// for InfluxDB 1.X
	implementation("org.influxdb:influxdb-java:2.23")
	// for InfluxDB 2.0 // https://github.com/influxdata/influxdb-client-java/releases
	implementation("com.influxdb:influxdb-client-java:6.10.0")

	implementation(libs.log4j.core) // used for our custom error handler, otherwise not a necessary dependency
	implementation(libs.log4j.jcl) // commons logging bridge.
	implementation(libs.log4j.slf4j2.impl)

	// https://github.com/slackapi/java-slack-sdk/releases
	implementation("com.slack.api:slack-api-client:1.45.3")
	// https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket
	implementation("org.java-websocket:Java-WebSocket:1.6.0")

	// TODO Remove jewelcli dependency and transition to commons-cli
	// https://mvnrepository.com/artifact/com.lexicalscope.jewelcli/jewelcli
	implementation("com.lexicalscope.jewelcli:jewelcli:0.8.9")

	// https://mvnrepository.com/artifact/commons-cli/commons-cli
	implementation("commons-cli:commons-cli:1.9.0")

	implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
	// https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-jul
	implementation(libs.log4j.jul)

	implementation("info.debatty:java-string-similarity:2.0.0")

	// TODO do we need this dependency?
	implementation("biz.paluch.logging:logstash-gelf:1.15.1")

	// https://mvnrepository.com/artifact/com.google.guava/guava // used for com.google.common.math
	implementation("com.google.guava:guava:33.4.8-jre")
}

tasks.shadowJar {
	mergeServiceFiles()
	manifest {
		attributes(
			mapOf(
				"Main-Class" to "me.retrodaredevil.solarthing.program.SolarMain",
				"Multi-Release" to "true"
			)
		)
	}
}

tasks.named<Test>("test") {
	useJUnitPlatform {
		excludeTags("integration")
	}
}
val integration = tasks.register<Test>("integration") {
	// In the future, we can use the Test Suite feature rather than doing this manually: https://docs.gradle.org/7.3.1/userguide/jvm_test_suite_plugin.html
	useJUnitPlatform {
		includeTags("integration")
	}
}

dockerCompose {
	useComposeFiles = listOf("../testing/couchdb-compose.yml")
	isRequiredBy(integration)
}
