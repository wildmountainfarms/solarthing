plugins {
	id("com.diffplug.spotless")
}
repositories {
	mavenLocal()
	mavenCentral()
	maven { url = uri("https://plugins.gradle.org/m2/") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
	maven { url = uri("https://jitpack.io") }
	google()
}

spotless {
	isEnforceCheck = false // doesn't have to be formatted correctly for ./gradlew build to work

	kotlinGradle {
		leadingSpacesToTabs()
		encoding = Charsets.UTF_8
	}
}
