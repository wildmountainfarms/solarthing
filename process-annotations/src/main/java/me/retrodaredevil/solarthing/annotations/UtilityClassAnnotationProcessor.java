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
import java.util.List;

@SupportedAnnotationTypes({"me.retrodaredevil.solarthing.annotations.UtilityClass"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class UtilityClassAnnotationProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
		for (TypeElement annotation : annotations) {
			for (Element classElement : roundEnvironment.getElementsAnnotatedWith(annotation)) {
				for (Element subElement : classElement.getEnclosedElements()) {
					if (subElement.getKind() == ElementKind.METHOD) {
						Set<Modifier> modifiers = subElement.getModifiers();
						boolean isStatic = modifiers.contains(Modifier.STATIC);
						if (!isStatic) {
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Non-static method declared! " + subElement.getSimpleName() + " in: " + classElement);
						}
					} else if (subElement.getKind() == ElementKind.CONSTRUCTOR) {
						boolean isPrivate = subElement.getModifiers().contains(Modifier.PRIVATE);
						if (!isPrivate) {
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Non-private constructor declared! " + subElement.getSimpleName() + " in: " + classElement);
						}
					} else if (subElement.getKind() == ElementKind.FIELD) {
						Set<Modifier> modifiers = subElement.getModifiers();
						boolean isStatic = modifiers.contains(Modifier.STATIC);
						boolean isFinal = modifiers.contains(Modifier.FINAL);
						if (!isStatic) {
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Non-static field declared! " + subElement.getSimpleName() + " in: " + classElement);
						}
						if (!isFinal) {
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Non-final field declared! " + subElement.getSimpleName() + " in: " + classElement);
						}
					}
				}
			}
		}
		return true;
	}
}
