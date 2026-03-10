plugins {
	id("buildlogic.java-common-conventions")
	`java-library`
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

version "0.0.1-SNAPSHOT"

dependencies {
	api(project(":core"))
	annotationProcessor(project(":process-annotations"))

	// https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
	api(libs.okhttp)
	api(libs.retrofit)
	api(libs.retrofit.converter.jackson)
	implementation(libs.retrofit.converter.scalars)
	implementation(libs.retrofit.converter.jackson)
	implementation(libs.okhttp.logging.interceptor)
}
