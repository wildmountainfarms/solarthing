package me.retrodaredevil.solarthing.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({"java.lang.Override", "me.retrodaredevil.solarthing.annotations.DefaultFinal"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class DefaultFinalAnnotationProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
		Set<Element> defaultFinalMethodSet = new HashSet<>(); // set of all methods that have @DefaultFinal on them
		for (TypeElement annotation : annotations) {
			if (annotation.getQualifiedName().contentEquals("me.retrodaredevil.solarthing.annotations.DefaultFinal")) {
				defaultFinalMethodSet.addAll(roundEnvironment.getElementsAnnotatedWith(annotation));
			}
		}
		for (Element defaultMethodElement : defaultFinalMethodSet) {
			if (!defaultMethodElement.getModifiers().contains(Modifier.DEFAULT)) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You can only put this on default methods! element: " + defaultMethodElement);
			}
		}
		for (TypeElement annotation : annotations) {
			if (!annotation.getQualifiedName().contentEquals("java.lang.Override")) {
				continue;
			}
			for (Element methodElement : roundEnvironment.getElementsAnnotatedWith(annotation)) {
				ExecutableElement method = (ExecutableElement) methodElement;
				TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
				for (TypeMirror superInterfaceMirror : classElement.getInterfaces()) {
					TypeElement superInterface = (TypeElement) processingEnv.getTypeUtils().asElement(superInterfaceMirror);
					for (Element superMethodElement : superInterface.getEnclosedElements()) {
						if (superMethodElement.getKind() == ElementKind.METHOD) {
							ExecutableElement superMethod = (ExecutableElement) superMethodElement;
							if (processingEnv.getElementUtils().overrides(method, superMethod, classElement) && defaultFinalMethodSet.contains(superMethodElement)) {
								processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You cannot override this method! element: " + method);
							}
						}
					}
				}
//				Element interfaceElement = defaultMethodElement.getEnclosingElement();
//				((TypeElement) interfaceElement).asType().
			}
		}
		return true;
	}
}
