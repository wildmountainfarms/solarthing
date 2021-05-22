package me.retrodaredevil.solarthing.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@SupportedAnnotationTypes({"me.retrodaredevil.solarthing.annotations.SerializeNameDefinedInBase"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class SerializeNameDefinedInBaseAnnotationProcessor extends AbstractProcessor {

	private void processMethods(Set<Element> serializeNameDefinedInBaseMethodSet, ExecutableElement method, TypeElement classElement) {
		for (Element superMethodElement : classElement.getEnclosedElements()) {
			if (superMethodElement.getKind() == ElementKind.METHOD) {
				ExecutableElement superMethod = (ExecutableElement) superMethodElement;
				if (processingEnv.getElementUtils().overrides(method, superMethod, classElement)) {
					if (superMethod.getAnnotationMirrors().stream().anyMatch(annotationMirror -> annotationMirror.getAnnotationType().asElement().getSimpleName().contentEquals("JsonProperty"))) {
						serializeNameDefinedInBaseMethodSet.remove(method);
					}
				}
			}
		}
	}

	private void processMethodAndClass(Set<Element> serializeNameDefinedInBaseMethodSet, ExecutableElement method, TypeElement classElement) {
		for (TypeMirror superInterfaceMirror : classElement.getInterfaces()) {
			TypeElement superInterface = (TypeElement) processingEnv.getTypeUtils().asElement(superInterfaceMirror);
			processMethods(serializeNameDefinedInBaseMethodSet, method, superInterface);
			processMethodAndClass(serializeNameDefinedInBaseMethodSet, method, superInterface);
		}
		TypeMirror superClassMirror = classElement.getSuperclass();
		if (superClassMirror.getKind() != TypeKind.NONE) {
			TypeElement superClass = (TypeElement) processingEnv.getTypeUtils().asElement(superClassMirror);
			processMethods(serializeNameDefinedInBaseMethodSet, method, superClass);
			processMethodAndClass(serializeNameDefinedInBaseMethodSet, method, superClass);
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
		if (annotations.isEmpty()) {
			return false;
		}
		TypeElement annotation = annotations.stream().findFirst().orElseThrow(NoSuchElementException::new);

		// set of all methods that have @SerializeNameDefinedInBase on them
		//   This list will be emptied as we confirm each has a super method with JsonProperty defined on it
		Set<Element> serializeNameDefinedInBaseMethodSet = new HashSet<>(roundEnvironment.getElementsAnnotatedWith(annotation));

		for (Element methodElement : roundEnvironment.getElementsAnnotatedWith(annotation)) {
			ExecutableElement method = (ExecutableElement) methodElement;
			TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
			processMethodAndClass(serializeNameDefinedInBaseMethodSet, method, classElement);
		}

		for (Element methodElement : serializeNameDefinedInBaseMethodSet) {
			ExecutableElement method = (ExecutableElement) methodElement;
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Method: " + method + " does not have a JsonProperty defined in one of its supermethods!");
		}
		return true;
	}
}
