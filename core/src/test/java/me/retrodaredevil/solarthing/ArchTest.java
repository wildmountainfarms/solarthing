package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import me.retrodaredevil.solarthing.annotations.SerializeNameDefinedInBase;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.open.OpenSourcePacket;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Mode;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import me.retrodaredevil.solarthing.reason.ExecutionReason;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static org.junit.jupiter.api.Assertions.fail;

public class ArchTest {
	@Test
	void testArch() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("me.retrodaredevil.solarthing");

		for(ArchRule rule : new ArchRule[] {
				classes().that()
						.areNotInterfaces().and().areAssignableTo(DocumentedPacketType.class)
						.should().beEnums(),
				classes().that()
						.areNotInterfaces().and().areAssignableTo(Mode.class)
						.should().beEnums(),
				classes().that().areEnums().should().haveOnlyFinalFields(),

				noMethods().that().areNotStatic().should().beDeclaredInClassesThat().areAnnotatedWith(UtilityClass.class),
				noFields().that().areNotStatic().should().beDeclaredInClassesThat().areAnnotatedWith(UtilityClass.class),
				noConstructors().that().areNotPrivate().should().beDeclaredInClassesThat().areAnnotatedWith(UtilityClass.class),
				methods().that().areAnnotatedWith(SerializeNameDefinedInBase.class).should().notBePrivate(),
				methods().that()
						.haveNameMatching("getPacketType").and().haveRawReturnType(DocumentedPacketType.class).and().doNotHaveModifier(JavaModifier.ABSTRACT)
						.should().beDeclaredInClassesThat().areAnnotatedWith(JsonTypeName.class)
						.orShould().beDeclaredInClassesThat().areAnonymousClasses() // this may be done for testing
						.orShould().beDeclaredIn(TypedDocumentedPacket.class), // an exception that we need. For some reason the method declared here isn't "abstract"
				classes().that().areAssignableTo(Packet.class) // All implementations of Packet should be immutable implementations
						.should().haveOnlyFinalFields(),
				methods().that()
						.haveNameMatching("equals").and().areDeclaredInClassesThat().areAssignableTo(OpenSourcePacket.class).should()
						.notBeDeclaredIn(Object.class),
				classes().that().areInterfaces().and().areAnnotatedWith(JsonTypeName.class) // If JsonTypeName is present, it is intended to be deserialized
						.should().beAnnotatedWith(JsonDeserialize.class), // Jackson needs to know how to deserialize interfaces
		}) {
			rule.check(importedClasses);
		}
	}

	@Test
	void testClassesThatMustImplementEquals() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("me.retrodaredevil.solarthing");

		Collection<JavaClass> classesToCheck = new ArrayList<>();
		classesToCheck.addAll(importedClasses.get(ExecutionReason.class).getAllSubclasses()); // we want to be able to compare all execution reasons
		classesToCheck.addAll(importedClasses.get(OpenSourcePacket.class).getAllSubclasses()); // required for OpenSourceExecutionReason

		for (JavaClass javaClass : classesToCheck) {
			if (javaClass.isInterface()) {
				continue;
			}
			boolean doesClassOverrideEquals = javaClass.getMethods().stream().anyMatch(method -> "equals".equals(method.getName()));
			if (!doesClassOverrideEquals) {
				fail("equals is not implemented for " + javaClass);
			}
		}
	}

	@Test
	void classes_contain_both_equals_and_hash_code_or_contain_neither() {
		// TODO Note this only checks the method name, so someone could overload equals or hashCode and cause errors

		JavaClasses importedClasses = new ClassFileImporter().importPackages("me.retrodaredevil");
		for (JavaClass javaClass : importedClasses) {
			boolean overridesEquals = javaClass.getMethods().stream().anyMatch(method -> "equals".equals(method.getName()));
			boolean overridesHashCode = javaClass.getMethods().stream().anyMatch(method -> "hashCode".equals(method.getName()));
			if (overridesEquals != overridesHashCode) {
				if (overridesEquals) {
					fail(javaClass + " overrides equals but not hashCode()!");
				} else {
					fail(javaClass + " overrides hashCode() but not equals()!");
				}
			}
		}

	}
}
