package me.retrodaredevil.solarthing;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import me.retrodaredevil.solarthing.annotations.SerializeNameDefinedInBase;
import me.retrodaredevil.solarthing.annotations.UtilityClass;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import me.retrodaredevil.solarthing.packets.Mode;
import me.retrodaredevil.solarthing.packets.TypedDocumentedPacket;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

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
						.orShould().beDeclaredIn(TypedDocumentedPacket.class) // an exception that we need. For some reason the method declared here isn't "abstract"
		}) {
			rule.check(importedClasses);
		}
	}
}
