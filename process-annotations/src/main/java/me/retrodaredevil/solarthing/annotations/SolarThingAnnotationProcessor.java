package me.retrodaredevil.solarthing.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"me.retrodaredevil.solarthing.annotations.UtilityClass"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class SolarThingAnnotationProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
		for (TypeElement annotation : annotations) {
			for (Element classElement : roundEnvironment.getElementsAnnotatedWith(annotation)) {
				for (Element subElement : classElement.getEnclosedElements()) {
					if (subElement.getKind() == ElementKind.METHOD) {
						Set<Modifier> modifiers = subElement.getModifiers();
						boolean isStatic = modifiers.contains(Modifier.STATIC);
						boolean isConstructor = subElement.getSimpleName().contentEquals("<init>");
						if (!isStatic && !isConstructor) {
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Non-static method declared! " + subElement.getSimpleName() + " in: " + classElement);
						}
					}
				}
			}
		}
		return true;
	}
}
