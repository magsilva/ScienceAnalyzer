package net.sf.sysrev.introspector;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassMetadata
{
	private Class<?> clazz;

	private Type[] genericTypes;

	private Class<?> getClass(Type type)
	{
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			}
		}
		
		return null;
	}
	
	public ClassMetadata(Class<?> clazz)
	{
		this.clazz = clazz;
	}

	public ClassMetadata(Type type)
	{
		this.clazz = getClass(type);
		if (this.clazz == null) {
			throw new IllegalArgumentException("The type is not a class");
		}
	}

	public boolean isParameterized()
	{
		Type type = clazz.getGenericSuperclass();
		if (type != null) {
			return true;
		}

		return false;
	}

	public Type[] getGenericTypes()
	{
		if (genericTypes == null) {
			discoverGenericTypes();
		}

		return genericTypes;
	}

	// class, interface, primitive type or void.
	// If this Class represents either the Object class, an interface, a primitive type, or void,
	// then null is returned. If this object represents an array class then the Class object
	// representing the Object class is returned.
	private void discoverGenericTypes()
	{
		if (!isParameterized()) {
			return;
		}

		/**
		 * Use reflection to get the entity class, as describe at
		 * http://blog.xebia.com/2009/02/07/acessing-generic-types-at-runtime-in-java/
		 */
		ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();

		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			Type[] types = (ParameterizedType[]) type.getActualTypeArguments();
			genericTypes = new Type[types.length];
			for (int i = 0; i < types.length; i++) {
				if (types[i] instanceof ParameterizedType) {
					genericTypes[i] = ((ParameterizedType) types[i]).getRawType();
				} else {
					genericTypes[i] = types[i];
				}
			}
		}
	}
	
	
	
	/**
	   * Get the actual type arguments a child class has used to extend a generic base class.
	   *
	   * @param baseClass the base class
	   * @param childClass the child class
	   * @return a list of the raw classes for the actual type arguments.
	   */
	  public static <T> List<Class<?>> getTypeArguments(
	    Class<T> baseClass, Class<? extends T> childClass) {
	    Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
	    Type type = childClass;
	    // start walking up the inheritance hierarchy until we hit baseClass
	    while (! getClass(type).equals(baseClass)) {
	      if (type instanceof Class) {
	        // there is no useful information for us in raw types, so just keep going.
	        type = ((Class) type).getGenericSuperclass();
	      }
	      else {
	        ParameterizedType parameterizedType = (ParameterizedType) type;
	        Class<?> rawType = (Class) parameterizedType.getRawType();
	  
	        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
	        TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
	        for (int i = 0; i < actualTypeArguments.length; i++) {
	          resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
	        }
	  
	        if (!rawType.equals(baseClass)) {
	          type = rawType.getGenericSuperclass();
	        }
	      }
	    }
	  
	    // finally, for each actual type argument provided to baseClass, determine (if possible)
	    // the raw class for that type argument.
	    Type[] actualTypeArguments;
	    if (type instanceof Class) {
	      actualTypeArguments = ((Class) type).getTypeParameters();
	    }
	    else {
	      actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
	    }
	    List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
	    // resolve types by chasing down type variables.
	    for (Type baseType: actualTypeArguments) {
	      while (resolvedTypes.containsKey(baseType)) {
	        baseType = resolvedTypes.get(baseType);
	      }
	      typeArgumentsAsClasses.add(getClass(baseType));
	    }
	    return typeArgumentsAsClasses;
	  }
}
