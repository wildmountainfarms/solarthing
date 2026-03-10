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
    annotationProcessor(project(":process-annotations"))

    api("com.github.retrodaredevil:action-lib:v1.3.1")
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
}
