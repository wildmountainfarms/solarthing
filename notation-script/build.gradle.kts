plugins {
    id("antlr")
    id("buildlogic.java-common-conventions")
}

version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    annotationProcessor(project(":process-annotations"))

    // https://github.com/antlr/antlr4/releases
    antlr(libs.antlr4)

    api(libs.jackson.annotations)
    api(libs.jackson.databind)
}
//compileJava {
//    options.compilerArgs << "-XepExcludedPaths:.*/build/generated-src/.*" // for antlr generated folder to remove errorprone warnings from ANTLR
//}
tasks.generateGrammarSource {
    maxHeapSize = "64m"
	arguments.addAll(listOf(
		"-visitor", "-long-messages",
	))
}
