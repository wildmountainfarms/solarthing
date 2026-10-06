plugins {
	// Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
	// consider not using this in the future: https://mbonnin.net/2025-07-10_the_case_against_kotlin_dsl/
	`kotlin-dsl`
}

repositories {
	// Use the plugin portal to apply community plugins in convention plugins.
	gradlePluginPortal()
}


dependencies {
	// https://github.com/tbroyer/gradle-errorprone-plugin/releases
	implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:5.1.1")

	// https://mvnrepository.com/artifact/com.diffplug.spotless/spotless-plugin-gradle
	implementation("com.diffplug.spotless:com.diffplug.spotless.gradle.plugin:8.10.3")

	// TODO upgrade to a newer openrewrite version - staying on 7.41.0 since that's the last version on Gradle Plugin Portal
	// https://github.com/openrewrite/rewrite-gradle-plugin/releases
	implementation("org.openrewrite.rewrite:org.openrewrite.rewrite.gradle.plugin:7.25.0")
}
