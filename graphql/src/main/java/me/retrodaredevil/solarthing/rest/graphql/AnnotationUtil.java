package me.retrodaredevil.solarthing.rest.graphql;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import io.leangen.graphql.metadata.TypedElement;
import me.retrodaredevil.solarthing.annotations.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * A utility to get annotations from methods and fields. This puts a layer of abstraction around the retrieval
 * of method annotations which is useful to get annotations from inherited interfaces.
 */
@UtilityClass
public final class AnnotationUtil {
	private AnnotationUtil() { throw new UnsupportedOperationException(); }

	public static <T extends Annotation> T getAnnotation(Class<T> annotationClass, Method method, Class<?> declaringClass) {
		for (Class<?> clazz : declaringClass.getInterfaces()) {
			Method superMethod = null;
			try {
				superMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
			} catch (NoSuchMethodException ignored) {
			}
			if (superMethod != null) {
				T annotation = superMethod.getAnnotation(annotationClass);
				if (annotation != null) {
					return annotation;
				}
			}
		}
		for (Class<?> clazz : declaringClass.getInterfaces()) {
			T annotation = getAnnotation(annotationClass, method, clazz);
			if (annotation != null) {
				return annotation;
			}
		}
		return null;
	}

	public static <T extends Annotation> T getAnnotation(Class<T> annotationClass, Method method) {
		T annotation = method.getAnnotation(annotationClass);
		if (annotation != null) {
			return annotation;
		}
		return getAnnotation(annotationClass, method, method.getDeclaringClass());
	}
	public static <T extends Annotation> T getAnnotation(Class<T> annotationClass, Member member) {
		if (member instanceof Method) {
			return getAnnotation(annotationClass, (Method) member);
		}
		if (member instanceof Field) {
			Field field = (Field) member;
			return field.getAnnotation(annotationClass);
		}
		throw new UnsupportedOperationException("Unsupported type: " + member.getClass());
	}
	public static <T extends Annotation> T getAnnotation(Class<T> annotationClass, AnnotatedMember annotatedMember) {
		return getAnnotation(annotationClass, annotatedMember.getMember());
	}
	public static <T extends Annotation> T getAnnotation(Class<T> annotationClass, TypedElement typedElement) {
		T annotation = typedElement.getAnnotation(annotationClass);
		if (annotation != null) {
			return annotation;
		}
		for (AnnotatedElement element : typedElement.getElements()) {
			if (element instanceof Method) {
				T elementAnnotation = getAnnotation(annotationClass, (Method) element);
				if (elementAnnotation != null) {
					return elementAnnotation;
				}
			}
		}
		return null;
	}
	public static boolean hasAnnotation(Class<? extends Annotation> annotationClass, Method method) {
		return getAnnotation(annotationClass, method) != null;
	}
	public static boolean hasAnnotation(Class<? extends Annotation> annotationClass, Member member) {
		return getAnnotation(annotationClass, member) != null;
	}
	public static boolean hasAnnotation(Class<? extends Annotation> annotationClass, AnnotatedMember annotatedMember) {
		return hasAnnotation(annotationClass, annotatedMember.getMember());
	}
}
