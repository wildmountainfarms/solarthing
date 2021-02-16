package me.retrodaredevil.solarthing.annotations;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Symbol;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
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
			for (Element element : roundEnvironment.getElementsAnnotatedWith(annotation)) {
				Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) element;
				for (Symbol symbol : classSymbol.getEnclosedElements()) {
					if (symbol instanceof Symbol.MethodSymbol) {
						Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) symbol;
						Set<Modifier> modifiers = methodSymbol.getModifiers();
						boolean isStatic = modifiers.contains(Modifier.STATIC);
						boolean isConstructor = methodSymbol.getSimpleName().contentEquals("<init>");
						if (!isStatic && !isConstructor) {
							processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Non-static method declared! " + methodSymbol.getQualifiedName() + " in: " + classSymbol);
						}
					}
				}
			}
		}
		return true;
	}
}
