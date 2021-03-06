package cn.alpha.reflect;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数组工具类
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class KernelArray {

	/**
	 * 获取数组array中的某一个
	 * 
	 * @param array
	 * @param index
	 * @return T
	 */
	public static <T> T get(T[] array, int index) {
		if (array != null && index >= 0 && index < array.length) {
			return array[index];
		}
		return null;
	}

	/**
	 * 设置数组array中的某一个值为element
	 * 
	 * 
	 * @param array
	 * @param element
	 * @return void
	 */
	public static <T> void set(T[] array, T element) {
		set(array, element, array.length);
	}

	/**
	 * 设置数组array中的某一个区间值为element
	 * 
	 * @param array
	 * @param element
	 * @param length
	 * @return void
	 */
	public static <T> void set(T[] array, T element, int length) {
		set(array, element, 0, length);
	}

	/**
	 * 设置数组array中的某一个区间值为element
	 * 
	 * @param array
	 * @param element
	 * @param beginIndex
	 * @param endIndex
	 * @return void
	 */
	public static <T> void set(T[] array, T element, int beginIndex, int endIndex) {
		for (int i = beginIndex; i < endIndex; i++) {
			array[i] = element;
		}
	}

	/**
	 * 创建一个长度为length的数组 里面的全部重复element
	 * 
	 * @param element
	 * @param length
	 * @return T[]
	 */
	public static <T> T[] repeat(T element, int length) {
		T[] array = (T[]) forComponentType(element.getClass()).newInstance(length);
		set(array, element, length);
		return array;
	}

	/**
	 * 创建一个长度为length类型为componentType的数组 里面的全部重复element
	 * 
	 * @param element
	 * @param length
	 * @param componentType
	 * @return T[]
	 */
	public static <T> T[] repeat(T element, int length, Class<T> componentType) {
		T[] array = (T[]) Array.newInstance(componentType, length);
		set(array, element, length);
		return array;
	}

	/**
	 * 判断数组中是否含有element
	 * 
	 * @param array
	 * @param element
	 * @return boolean
	 */
	public static <T> boolean contain(T[] array, T element) {
		for (T value : array) {
			if (value.equals(element)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断数组是否含有elements
	 * 
	 * @param array
	 * @param elements
	 * @return boolean
	 */
	public static <T> boolean contains(T[] array, T... elements) {
		for (T element : elements) {
			if (!contain(array, element)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 判断两个数组的内容是否相等 用于==的比较
	 * 
	 * @param array
	 * @param other
	 * @return boolean
	 */
	public static boolean equal(Object[] array, Object[] other) {
		int length = array.length;
		if (length != other.length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (array[i] != other[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断两个数组的内容是否相等 用于equals的比较
	 * 
	 * @param array
	 * @param other
	 * @return boolean
	 */
	public static boolean equals(Object[] array, Object[] other) {
		int length = array.length;
		if (length != other.length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (!KernelObject.equals(array[i], other[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 数组合并
	 * 
	 * @param array
	 * @param other
	 * @return T[]
	 */
	public static <T> T[] concat(T[] array, T[] other) {
		T[] concatArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + other.length);
		System.arraycopy(array, 0, concatArray, 0, array.length);
		try {
			System.arraycopy(other, 0, concatArray, array.length, other.length);
		} catch (ArrayStoreException ase) {
			return array;
		}
		return concatArray;
	}

	/**
	 * 把一个数组拷贝到另一个数组中去
	 * 
	 * @param array
	 * @param to
	 * @return void
	 */
	public static <T> void copy(T[] array, T[] to) {
		int length = array.length;
		for (int i = 0; i < length; i++) {
			to[i] = array[i];
		}
	}

	/**
	 * 把一个数组拷贝到集合中去
	 * 
	 * @param array
	 * @param collection
	 * @return void
	 */
	public static <T> void copy(T[] array, Collection<T> collection) {
		for (T value : array) {
			collection.add(value);
		}
	}

	/**
	 * 数组转为List
	 * 
	 * @param array
	 * @return List<T>
	 */
	public static <T> List<T> toList(T[] array) {
		List<T> list = new ArrayList<T>(array.length);
		copy(array, list);
		return list;
	}

	/**
	 * 数组转换为Set
	 * 
	 * @param array
	 * @return Set<T>
	 */
	public static <T> Set<T> toSet(T[] array) {
		Set<T> set = new HashSet<T>(array.length);
		copy(array, set);

		return set;
	}

	/**
	 * 数组访问器
	 * 
	 */
	public static interface ArrayAccessor {
		public Object newInstance(int length);

		public Object get(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

		public void set(Object array, int index, Object value)
				throws IllegalArgumentException, ArrayIndexOutOfBoundsException;
	}

	/**
	 * 枚举
	 * 
	 */
	public static enum EnumArrayAccessor implements ArrayAccessor {

		Byte {
			@Override
			public java.lang.Object newInstance(int length) {
				return new byte[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getByte(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.setByte(array, index, (java.lang.Byte) value);
			}
		},

		Short {
			@Override
			public java.lang.Object newInstance(int length) {
				return new short[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getShort(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.setShort(array, index, (java.lang.Short) value);
			}
		},

		Integer {
			@Override
			public java.lang.Object newInstance(int length) {
				return new int[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getByte(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.setInt(array, index, (java.lang.Integer) value);
			}
		},

		Long {
			@Override
			public java.lang.Object newInstance(int length) {
				return new long[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getLong(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.setLong(array, index, (java.lang.Long) value);
			}
		},

		Float {
			@Override
			public java.lang.Object newInstance(int length) {
				return new float[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getFloat(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object object) {
				Array.setFloat(array, index, (java.lang.Float) object);
			}
		},

		Double {
			@Override
			public java.lang.Object newInstance(int length) {
				return new double[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getDouble(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.setDouble(array, index, (java.lang.Double) value);
			}
		},

		Boolean {
			@Override
			public java.lang.Object newInstance(int length) {
				return new boolean[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getBoolean(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.setBoolean(array, index, (java.lang.Boolean) value);
			}
		},

		Character {
			@Override
			public java.lang.Object newInstance(int length) {
				return new char[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.getChar(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.setChar(array, index, (java.lang.Character) value);
			}
		},

		Object {
			@Override
			public java.lang.Object newInstance(int length) {
				return new Object[length];
			}

			@Override
			public java.lang.Object get(java.lang.Object array, int index) {
				return Array.get(array, index);
			}

			@Override
			public void set(java.lang.Object array, int index, java.lang.Object value) {
				Array.set(array, index, value);
			}
		};
	}

	/**
	 * @author absir
	 * 
	 */
	public static class ComponentArrayAsscessor implements ArrayAccessor {

		/** componentType */
		private Class componentType;

		/**
		 * @param componentType
		 */
		public ComponentArrayAsscessor(Class componentType) {
			this.componentType = componentType;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.appserv.kernel.KernelArray.Interface#newInstance(int)
		 */
		@Override
		public Object newInstance(int length) {
			return Array.newInstance(componentType, length);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.appserv.kernel.KernelArray.Interface#get(java.lang.Object,
		 * int)
		 */
		@Override
		public Object get(Object array, int index) {
			return Array.get(array, index);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.appserv.kernel.KernelArray.Interface#set(java.lang.Object,
		 * int, java.lang.Object)
		 */
		@Override
		public void set(Object array, int index, Object value) {
			Array.set(array, index, value);
		}
	}

	/**
	 * @param cls
	 * @return
	 */
	public static ArrayAccessor forClass(Class cls) {
		if (cls.isArray()) {
			return forComponentType(cls.getComponentType());
		}

		return null;
	}

	/**
	 * @param componentType
	 * @return
	 */
	public static ArrayAccessor forComponentType(Class componentType) {
		if (componentType == byte.class) {
			return EnumArrayAccessor.Byte;

		} else if (componentType == short.class) {
			return EnumArrayAccessor.Short;

		} else if (componentType == int.class) {
			return EnumArrayAccessor.Integer;

		} else if (componentType == long.class) {
			return EnumArrayAccessor.Long;

		} else if (componentType == float.class) {
			return EnumArrayAccessor.Float;

		} else if (componentType == double.class) {
			return EnumArrayAccessor.Double;

		} else if (componentType == boolean.class) {
			return EnumArrayAccessor.Boolean;

		} else if (componentType == char.class) {
			return EnumArrayAccessor.Character;

		} else if (componentType == Object.class) {
			return EnumArrayAccessor.Object;
		}

		return new ComponentArrayAsscessor(componentType);
	}

	/**
	 * 数组拷贝
	 * 
	 * @param array
	 * @param to
	 * @return void
	 */
	public static <T> void copy(Object array, Object to) {
		if (array.getClass().isArray() && to.getClass().isArray()
				&& array.getClass().getComponentType().isAssignableFrom(to.getClass().getComponentType())) {
			ArrayAccessor interfaceArray = forClass(array.getClass());
			int length = Array.getLength(array);
			for (int i = 0; i < length; i++) {
				interfaceArray.set(to, i, interfaceArray.get(array, i));
			}
		}
	}

	/**
	 * 数组克隆
	 * 
	 * @param array
	 * @return T
	 */
	public static <T> T clone(T array) {
		ArrayAccessor interfaceArray = forClass(array.getClass());
		if (interfaceArray == null) {
			return null;
		}

		int length = Array.getLength(array);
		T clone = (T) interfaceArray.newInstance(length);
		for (int i = 0; i < length; i++) {
			interfaceArray.set(clone, i, interfaceArray.get(array, i));
		}

		return clone;
	}
}
