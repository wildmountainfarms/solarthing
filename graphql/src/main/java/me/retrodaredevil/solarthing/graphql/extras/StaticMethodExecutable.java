package me.retrodaredevil.solarthing.graphql.extras;

import io.leangen.graphql.metadata.execution.Executable;
import io.leangen.graphql.util.ClassUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class StaticMethodExecutable extends Executable<Method> {

	private final Method method;
	private final AnnotatedType enclosingType;
	private final AnnotatedType returnType;

	public StaticMethodExecutable(Method resolverMethod, AnnotatedType enclosingType) {
		this.method = resolverMethod;
		this.enclosingType = enclosingType;
		this.returnType = resolveReturnType(enclosingType);
	}

	@Override
	public Object execute(Object target, Object[] args) throws InvocationTargetException, IllegalAccessException {
		Object[] actualArgs = new Object[args.length + 1];
		actualArgs[0] = target;
		System.arraycopy(args, 0, actualArgs, 1, args.length);
		return method.invoke(null, actualArgs);
	}

	@Override
	public AnnotatedType getReturnType() {
		return returnType;
	}

	private AnnotatedType resolveReturnType(AnnotatedType enclosingType) {
		return ClassUtils.getReturnType(method, enclosingType);
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.reflect.Executable#getParameterCount
	 */
	@Override
	public int getParameterCount() {
		return method.getParameterCount() - 1;
	}

	@Override
	public AnnotatedType[] getAnnotatedParameterTypes() {
		AnnotatedType[] parameterTypes = ClassUtils.getParameterTypes(method, enclosingType);
		if(parameterTypes.length <= 1) {
			return new AnnotatedType[0];
		}
		return Arrays.copyOfRange(parameterTypes, 1, parameterTypes.length);
	}

	@Override
	public Parameter[] getParameters() {
		Parameter[] parameters = method.getParameters();
		if(parameters.length <= 1) {
			return new Parameter[0];
		}
		return Arrays.copyOfRange(parameters, 1, parameters.length);
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}

	/**
	 * Two {@code Executable}s are considered equal either if their wrapped fields/methods are equal
	 * or if one wraps a field and the other its corresponding getter/setter.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object that) {
		return this == that || (that instanceof StaticMethodExecutable && ((StaticMethodExecutable) that).method.equals(this.method));
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
