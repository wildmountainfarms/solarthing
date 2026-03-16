import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.attributes.java.TargetJvmVersion

plugins {
	id("buildlogic.common-conventions")
	// Apply the java Plugin to add support for Java.
	java

	// NOTE: Due to limitations with convention plugins, you cannot use version catalogs here.
	//   More information: https://discuss.gradle.org/t/using-version-catalog-from-buildsrc-buildlogic-xyz-common-conventions-scripts/48534/3

	id("net.ltgt.errorprone")
	// https://docs.openrewrite.org/running-recipes/getting-started#step-2-add-rewrite-maven-plugin-or-rewrite-gradle-plugin-to-your-project
	id("org.openrewrite.rewrite")
}

dependencies {
//	implementation("org.jetbrains:annotations:26.0.2") // https://github.com/JetBrains/java-annotations/releases
	implementation("org.jspecify:jspecify:1.0.0")
	// Add checker-qual dependency so we can use @RequiresNonNull.
	//   More info: https://github.com/uber/NullAway/wiki/Supported-Annotations#ensuresnonnullif
	// https://mvnrepository.com/artifact/org.checkerframework/checker-qual
	implementation("org.checkerframework:checker-qual:3.54.0")

	// https://github.com/google/error-prone/releases
	val errorProneVersion = "2.48.0"
	errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
	// https://github.com/uber/NullAway/releases
	errorprone("com.uber.nullaway:nullaway:0.13.1")

	// https://mvnrepository.com/artifact/com.google.errorprone/error_prone_annotations
	compileOnly("com.google.errorprone:error_prone_annotations:$errorProneVersion")

	// TODO update rewrite dependencies - was getting `Caused by: java.lang.IllegalArgumentException: 'other' is different type of Path` with updated dependencies
	// https://docs.openrewrite.org/recipes/java/spring
	// https://github.com/openrewrite/rewrite-spring/releases
	rewrite("org.openrewrite.recipe:rewrite-spring:6.23.1")
	// https://github.com/openrewrite/rewrite-migrate-java/releases
	rewrite("org.openrewrite.recipe:rewrite-migrate-java:3.26.0")
	// https://github.com/openrewrite/rewrite-static-analysis/releases
	rewrite("org.openrewrite.recipe:rewrite-static-analysis:2.26.0")
	// https://github.com/openrewrite/rewrite-testing-frameworks/releases
	rewrite("org.openrewrite.recipe:rewrite-testing-frameworks:3.26.0")
	// https://github.com/openrewrite/rewrite-logging-frameworks/releases
	rewrite("org.openrewrite.recipe:rewrite-logging-frameworks:3.22.0")
	// https://github.com/openrewrite/rewrite-hibernate/releases
	rewrite("org.openrewrite.recipe:rewrite-hibernate:2.17.1")

	// Use JUnit Jupiter for testing. https://junit.org/junit5/
	testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

// Run tests on a newer JVM so JUnit 6 works, while keeping production bytecode compatible
// with the project's source/target compatibility settings (e.g., Java 11).
tasks.withType<Test>().configureEach {
	javaLauncher.set(
		javaToolchains.launcherFor {
			languageVersion.set(JavaLanguageVersion.of(25))
		}
	)
}

// Ensure test configurations resolve dependencies compatible with the newer test JVM,
// without changing main source/target compatibility.
configurations.named("testCompileClasspath") {
	attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}
configurations.named("testRuntimeClasspath") {
	attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

tasks.named<Test>("test") {
	// Use JUnit Platform for unit tests.
	useJUnitPlatform()
}

spotless {
	format("javaBasic", com.diffplug.gradle.spotless.JavaExtension::class.java) {
		target(fileTree(".") {
			include("**/*.java")
			exclude("**/build/**", "**/build-*/**")
		})
		toggleOffOn()
		removeUnusedImports()
		endWithNewline()
		trimTrailingWhitespace()
	}
	format("javaIndent", com.diffplug.gradle.spotless.JavaExtension::class.java) {
		target(fileTree(".") {
			include("**/*.java")
			exclude("**/build/**", "**/build-*/**")
		})
		leadingSpacesToTabs()
//        toggleOffOn()
//        toggleOffOn("/*", "*/") // It doesn't like tabs then a space, which are present in Javadocs, so just disable
		// NOTE: Only one toggleOffOn is supported
		toggleOffOn("\"\"\"", "\"\"\"")
	}
}

tasks.withType<JavaCompile>().configureEach {
	val isTest = name.contains("test", ignoreCase = true)
	options.errorprone {
		disableWarningsInGeneratedCode = true

		excludedPaths = ".*/build/generated.*/.*"
		check("NullAway", CheckSeverity.ERROR)
		option("NullAway:OnlyNullMarked", "true") // @NullMarked annotation required for anything to happen
		// https://github.com/uber/NullAway/wiki/JSpecify-Support#jspecify-mode
		option("NullAway:JSpecifyMode", "true")
		option("NullAway:AcknowledgeRestrictiveAnnotations", "true") // annotations in non-NullMarked code are used by NullAway
		// https://github.com/uber/NullAway/wiki/Configuration#excluded-field-annotations
		option("NullAway:ExcludedFieldAnnotations", listOf(
			"org.springframework.beans.factory.annotation.Value",
		).joinToString(","))
		// https://github.com/uber/NullAway/wiki/Configuration#custom-initializer-annotations
		option("NullAway:CustomInitializerAnnotations", listOf(
			"jakarta.annotation.PostConstruct",
		).joinToString(","))
		// https://github.com/uber/NullAway/wiki/JSpecify-Support#requireexplicitnullmarking-checker
		error("RequireExplicitNullMarking")
		warn("AnnotationPosition") // keep as warn so that rewriteRun can fix this issue

		// TODO update errors

		disable("MissingSummary") // Our JavaDocs don't need to be perfect
		disable("StringSplitter") // Yes String.split(String) has surprising behavior, but we know how to be careful.
		disable("EqualsGetClass") // Most of the time we prefer that equality checks don't evaluate to true for subclasses
		disable("InvalidLink") // So many times this is wrong. #equals(Object) is a valid link!
		disable("ComparableType") // different Identifiers need to be able to be compared to each other, so we disable this for now.
		disable("DoNotClaimAnnotations") // TODO figure how how to make our annotation processor more "correct"
		disable("InlineMeSuggester") // Wrong when annotated by annotations, and I don't need this feature
		disable("EmptyCatch") // This is a great warning, but IntelliJ wants ignored exceptions to be named "ignored" and ErrorProne wants "ok"...
		disable("FutureReturnValueIgnored") // For "send and forget" stuff, this is really annoying
		disable("UnusedVariable") // We are OK with this most of the time
		disable("CanonicalDuration") // I will specify my durations in whatever units I please
		disable("PatternMatchingInstanceof") // TODO reenable and use pattern matching instanceof
		disable("EffectivelyPrivate") // a warning by default, but I often like to make things public even when they're apart of a private class


		error("MissingOverride")
		error("AssignmentExpression")
		error("NullableOnContainingClass")
		warn("UnusedMethod") // Note: https://github.com/google/error-prone/issues/3144 // issue is now "completed", but annotations not customizable

		// Experimental Errors
		enable("ClassName")
		warn("DepAnn")
		warn("EmptyIf")
		warn("InsecureCryptoUsage")
		// Experimental Warnings
		warn("ConstantPatternCompile")
		warn("DifferentNameButSame")
		error("EqualsBrokenForNull")
		warn("InconsistentOverloads")
		warn("InitializeInline")
		warn("InterfaceWithOnlyStatics")
		warn("InterruptedExceptionSwallowed")
		warn("Interruption")
//            warn("MemberName") // useful, but stuff like _myVariable does not work, and that can be useful *sometimes*
		warn("NonCanonicalStaticMemberImport")
//            warn("PreferJavaTimeOverload") // SolarThing uses a lot of dateMillis, and many times it does not make sense to use java.time
		warn("RedundantOverride")
		warn("RedundantThrows")
//            warn("ThrowSpecificExceptions") // TODO Something we need to work on in the SolarThing codebase
		warn("TransientMisuse")
		warn("TryWithResourcesVariable")
		warn("UnnecessarilyFullyQualified")
		warn("UnnecessaryAnonymousClass") // suggest
		warn("UnusedException")


		error("FieldMissingNullable")
		error("ParameterMissingNullable")
		error("ReturnMissingNullable")

		// Experimental Suggestions
		warn("ConstantField")
//            warn("FieldCanBeFinal") // Useful on everything but :client module, because of the usages of @JsonProperty on mutable fields.
		warn("FieldCanBeLocal")
		warn("FieldCanBeStatic")
		warn("ForEachIterable")
		warn("LambdaFunctionalInterface")
		if (!isTest) {
			warn("MethodCanBeStatic")
		}
//            error("MissingBraces") // I would love to enforce this in all places except equals(), so disable for now
		error("MixedArrayDimensions")
//            error("MultiVariableDeclaration") // While frowned upon many times, we use this a lot and it is helpful somtimes
		error("MultipleTopLevelClasses")
		error("PackageLocation")
		error("PrivateConstructorForUtilityClass") // not completely ideal for spring classes with static initialization
		warn("RemoveUnusedImports") // Only a warning because https://github.com/antlr/antlr4/issues/2568 and https://github.com/google/error-prone/issues/463
		error("ReturnsNullCollection")
		warn("SwitchDefault")
		error("SymbolToString")
		warn("ThrowsUncheckedException") // we should be unchecked exceptions in JavaDoc instead
		warn("TryFailRefactoring")
		error("TypeToString")
		warn("UnnecessaryBoxedAssignment")
		warn("UnnecessaryBoxedVariable")
		error("UnnecessaryStaticImport")
	}
}

rewrite {
	failOnDryRunResults = true
	isExportDatatables = true
	configFile = project.rootProject.file("rewrite.yml")

	// https://docs.openrewrite.org/recipes/java/removeobjectsisnull
	activeRecipe("org.openrewrite.java.RemoveObjectsIsNull")

//	activeRecipe("me.retrodaredevil.rewrite.SolarThingNullabilityAnnotations")

	activeRecipe("org.openrewrite.staticanalysis.NullableOnMethodReturnType")

	// https://docs.openrewrite.org/recipes/java/removeunusedimports
	// We decide against RemoveUnusedImports, because it does not check imports exclusively used within Javadocs
	// activeRecipe("org.openrewrite.java.RemoveUnusedImports")

	// https://docs.openrewrite.org/recipes/java/migrate/lang/switchcasereturnstoswitchexpression
	activeRecipe("org.openrewrite.java.jspecify.MigrateToJSpecify")
	// https://docs.openrewrite.org/recipes/java/shortenfullyqualifiedtypereferences
	activeRecipe("org.openrewrite.java.ShortenFullyQualifiedTypeReferences")

//	activeRecipe("org.openrewrite.java.migrate.util.SequencedCollection") // TODO this was enabled, but it won't compile for all language versions
	activeRecipe("org.openrewrite.java.migrate.lang.SwitchCaseAssignmentsToSwitchExpression")
	activeRecipe("org.openrewrite.java.migrate.lang.SwitchCaseReturnsToSwitchExpression")
	activeRecipe("org.openrewrite.java.jspecify.MigrateToJSpecify") // probably not necessary, but will point out "wrong" nullability imports
	activeRecipe("org.openrewrite.java.ShortenFullyQualifiedTypeReferences")

	// Lots of static analysis
	activeRecipe("org.openrewrite.staticanalysis.RemoveExtraSemicolons")
	activeRecipe("org.openrewrite.staticanalysis.AnnotateNullableParameters") // if you check for null, it should be @Nullable
	activeRecipe("org.openrewrite.staticanalysis.CatchClauseOnlyRethrows")
	activeRecipe("org.openrewrite.staticanalysis.CompareEnumsWithEqualityOperator")
//	activeRecipe("org.openrewrite.staticanalysis.FinalClass") // maybe consider in the future
	activeRecipe("org.openrewrite.staticanalysis.ForLoopIncrementInUpdate")
//	activeRecipe("org.openrewrite.staticanalysis.UpperCaseLiteralSuffixes") // eventually maybe we can consider this
//	activeRecipe("org.openrewrite.staticanalysis.UseDiamondOperator") // TODO see what this does independently
	activeRecipe("org.openrewrite.staticanalysis.BooleanChecksNotInverted")
//	activeRecipe("org.openrewrite.staticanalysis.NeedBraces") // consider in the future
//	activeRecipe("org.openrewrite.staticanalysis.HideUtilityClassConstructor") // tries to make spring application class have private constructor
//	activeRecipe("org.openrewrite.staticanalysis.JavaApiBestPractices") // TODO enable
	activeRecipe("org.openrewrite.staticanalysis.ModifierOrder")
	activeRecipe("org.openrewrite.staticanalysis.NestedEnumsAreNotStatic")
	activeRecipe("org.openrewrite.staticanalysis.UseJavaStyleArrayDeclarations")
	activeRecipe("org.openrewrite.staticanalysis.NoDoubleBraceInitialization")
//	activeRecipe("org.openrewrite.staticanalysis.MultipleVariableDeclarations") // maybe consider in the future
//	activeRecipe("org.openrewrite.staticanalysis.OperatorWrap") // TODO enable - this is a good thing, but results in many many changes
	activeRecipe("org.openrewrite.staticanalysis.PreferSystemGetPropertyOverGetenv")
	activeRecipe("org.openrewrite.staticanalysis.PreferEqualityComparisonOverDifferenceCheck")
	activeRecipe("org.openrewrite.staticanalysis.PreferIncrementOperator")
//	activeRecipe("org.openrewrite.staticanalysis.CommonDeclarationSiteTypeVariances") // Could be useful, but honestly the `? super asdf` and `? extends asdf` gets old when it's unnecessary
	activeRecipe("org.openrewrite.staticanalysis.UnnecessaryPrimitiveAnnotations")
	activeRecipe("org.openrewrite.staticanalysis.UnnecessaryCatch")
	activeRecipe("org.openrewrite.staticanalysis.EmptyBlock")
//	activeRecipe("org.openrewrite.staticanalysis.RemoveRedundantTypeCast") // bug where it will sometimes remove (T) in unchecked code
	activeRecipe("org.openrewrite.staticanalysis.RemoveRedundantNullCheckBeforeInstanceof")
	activeRecipe("org.openrewrite.staticanalysis.RemoveRedundantNullCheckBeforeLiteralEquals")
	activeRecipe($$"org.openrewrite.staticanalysis.SimplifyTernaryRecipes$SimplifyTernaryFalseTrueRecipe")
	activeRecipe($$"org.openrewrite.staticanalysis.SimplifyTernaryRecipes$SimplifyTernaryTrueFalseRecipe")
	activeRecipe("org.openrewrite.staticanalysis.UseListSort")
	activeRecipe("org.openrewrite.staticanalysis.SimplifyBooleanExpression")
	activeRecipe("org.openrewrite.staticanalysis.SimplifyBooleanReturn")
	activeRecipe("org.openrewrite.staticanalysis.StaticMethodNotFinal")
	activeRecipe("org.openrewrite.staticanalysis.NoToStringOnStringType")
	activeRecipe("org.openrewrite.staticanalysis.UnnecessaryCloseInTryWithResources")
	activeRecipe("org.openrewrite.staticanalysis.UnnecessaryExplicitTypeArguments")
	activeRecipe("org.openrewrite.staticanalysis.UnnecessaryReturnAsLastStatement")
	activeRecipe("org.openrewrite.staticanalysis.UnnecessaryThrows")
	activeRecipe("org.openrewrite.staticanalysis.UnwrapRepeatableAnnotations")
//	activeRecipe("org.openrewrite.staticanalysis.UnwrapElseAfterReturn") // kinda annoying when I want to pick the style on a per if-else statement basis
	activeRecipe("org.openrewrite.staticanalysis.UsePortableNewlines")
//	activeRecipe("org.openrewrite.staticanalysis.UseCollectionInterfaces") // stops people from using something like an ArrayList in a return type // TODO enable
//	activeRecipe("org.openrewrite.staticanalysis.IsEmptyCallOnCollections") // eventually enable this, but first update some tests
	activeRecipe("org.openrewrite.staticanalysis.NoEmptyCollectionWithRawType")
	activeRecipe("org.openrewrite.staticanalysis.ReplaceCollectionToArrayArgWithEmptyArray")
	activeRecipe("org.openrewrite.staticanalysis.UseMapContainsKey")
	activeRecipe("org.openrewrite.staticanalysis.UseStandardCharset")
	activeRecipe("org.openrewrite.staticanalysis.EqualsToContentEquals")
	activeRecipe("org.openrewrite.staticanalysis.StringLiteralEquality")
	activeRecipe("org.openrewrite.staticanalysis.UseStringReplace")
	activeRecipe("org.openrewrite.staticanalysis.UseSystemLineSeparator")
	activeRecipe("org.openrewrite.staticanalysis.NoEqualityInForCondition")
	// consider https://docs.openrewrite.org/recipes/staticanalysis/explicitlambdaargumenttypes later
	activeRecipe("org.openrewrite.staticanalysis.IndexOfChecksShouldUseAStartPosition")
	activeRecipe("org.openrewrite.staticanalysis.PrimitiveWrapperClassConstructorToValueOf")
	activeRecipe("org.openrewrite.staticanalysis.RemoveHashCodeCallsFromArrayInstances")
	activeRecipe("org.openrewrite.staticanalysis.IndexOfReplaceableByContains")
	activeRecipe("org.openrewrite.staticanalysis.BigDecimalDoubleConstructorRecipe")

}
