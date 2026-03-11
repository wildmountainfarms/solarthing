plugins {
	// Apply the foojay-resolver plugin to allow automatic download of JDKs
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "solarthing"
include("core")
include("client")
include("serviceapi")
include("server")
include("common")
include("process-annotations")
include("action-node")
include("action-lang")
include("notation-script")
include("web")
