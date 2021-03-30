package me.retrodaredevil.solarthing;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import me.retrodaredevil.solarthing.packets.DocumentedPacketType;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchTest {
	@Test
	void testArch() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("me.retrodaredevil.solarthing");

		ArchRule rule = classes().that()
				.areNotInterfaces().and().areAssignableTo(DocumentedPacketType.class)
				.should().beEnums();

		rule.check(importedClasses);
	}
}
