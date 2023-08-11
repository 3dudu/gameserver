package com.xuegao.PayServer.util;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class KernelLang {

	/** NULL_OBJECT */
	public static final Object NULL_OBJECT = new Object();

	/** NULL_OBJECTS */
	public static final Object[] NULL_OBJECTS = new Object[] {};

	/** NULL_STRING */
	public static final String NULL_STRING = "";

	/** NULL_Strings */
	public static final String[] NULL_STRINGS = new String[] {};

	/**
	 * @param one
	 * @param two
	 * @param three
	 * @return
	 */
	public static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}


	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static interface CloneTemplate<T> extends Cloneable {

		/**
		 * @return
		 */
		public T clone();

	}

	/**
	 * @author absir
	 * 
	 * @param <T>
	 */
	public static interface CallbackTemplate<T> {

		/**
		 * @param template
		 */
		void doWith(T template);
	}

	

	/**
	 * @author absir
	 * 
	 * @param <T>
	 * @param <K>
	 */
	public static interface GetTemplate<T, K> {

		/**
		 * @param template
		 */
		K getWith(T template);
	}

	/**
	 * @author absir
	 * 
	 */
	public static enum MatcherType {

		/** NORMAL */
		NORMAL {

			@Override
			public boolean matchString(String match, String string) {
				return string.contains(match);
			}
		},

		/** LEFT */
		LEFT {

			@Override
			public boolean matchString(String match, String string) {
				return string.startsWith(match);
			}
		},

		/** RIGHT */
		RIGHT {

			@Override
			public boolean matchString(String match, String string) {
				return string.endsWith(match);
			}
		},

		/** CONTAINS */
		CONTAINS {

			@Override
			public boolean matchString(String match, String string) {
				return string.contains(match);
			}
		};

		public abstract boolean matchString(String match, String string);

		
		/**
		 * @param match
		 * @param entry
		 * @return
		 */
		public static boolean isMatch(String match, Entry<String, MatcherType> entry) {
			MatcherType matcherType = entry.getValue();
			if (matcherType == null) {
				return true;
			}

			if (matcherType.matchString(entry.getKey(), match)) {
				return true;
			}

			return false;
		}
	}

	/**
	 * 
	 * @author absir
	 * 
	 *         not safe in thread
	 */
	public static class PropertyFilter {

		/** group */
		private int group;

		/** includes */
		private Map<String, Entry<String, MatcherType>> includes;

		/** excludes */
		private Map<String, Entry<String, MatcherType>> excludes;

		/** propertyPath */
		private String propertyPath = "";

		/**
		 * @return
		 */
		public PropertyFilter newly() {
			PropertyFilter filter = new PropertyFilter();
			filter.group = group;
			filter.includes = includes;
			filter.excludes = excludes;
			return filter;
		}

		/**
		 * 
		 */
		public void begin() {
			propertyPath = "";
		}

		/**
		 * @param include
		 * @param exclude
		 * @return
		 */
		public boolean allow(int include, int exclude) {
			return group == 0 || ((exclude & group) == 0 && (include == 0 || (include & group) != 0));
		}

		/**
		 * @return the group
		 */
		public int getGroup() {
			return group;
		}

		/**
		 * @param group
		 *            the group to set
		 */
		public void setGroup(int group) {
			this.group = group;
		}

		/**
		 * @param property
		 * @return
		 */
		public PropertyFilter inlcude(String property) {
			if (includes == null) {
				includes = new HashMap<String, Entry<String, MatcherType>>();

			} else if (includes.containsKey(property)) {
				return this;
			}

			includes.put(property, null);
			return this;
		}

		/**
		 * @param property
		 * @return
		 */
		public void removeInclude(String property) {
			if (includes != null) {
				includes.remove(property);
			}
		}

		/**
		 * @param property
		 * @return
		 */
		public PropertyFilter exlcude(String property) {
			if (excludes == null) {
				excludes = new HashMap<String, Entry<String, MatcherType>>();

			} else if (excludes.containsKey(property)) {
				return this;
			}

			excludes.put(property, null);
			return this;
		}

		/**
		 * @param property
		 */
		public void removeExclude(String property) {
			if (excludes != null) {
				excludes.remove(property);
			}
		}

		/**
		 * @return
		 */
		public boolean isNonePath() {
			return includes == null && excludes == null;
		}

		
		/**
		 * @return the propertyPath
		 */
		public String getPropertyPath() {
			return propertyPath;
		}

		/**
		 * @param propertyPath
		 *            the propertyPath to set
		 */
		public void setPropertyPath(String propertyPath) {
			this.propertyPath = propertyPath;
		}

		/**
		 * @return the includes
		 */
		public Map<String, Entry<String, MatcherType>> getIncludes() {
			return includes;
		}

		/**
		 * @return the excludes
		 */
		public Map<String, Entry<String, MatcherType>> getExcludes() {
			return excludes;
		}
	}

	/** NULL_LIST_SET */
	public static final NullListSet NULL_LIST_SET = new NullListSet();

	/**
	 * @author absir
	 * 
	 */
	public static final class NullListSet implements List, Set {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#add(java.lang.Object)
		 */
		@Override
		public boolean add(Object e) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#add(int, java.lang.Object)
		 */
		@Override
		public void add(int index, Object element) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#addAll(java.util.Collection)
		 */
		@Override
		public boolean addAll(Collection c) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#addAll(int, java.util.Collection)
		 */
		@Override
		public boolean addAll(int index, Collection c) {
			return false;
		}

		@Override
		public void replaceAll(UnaryOperator operator) {
			List.super.replaceAll(operator);
		}

		@Override
		public void sort(Comparator c) {
			List.super.sort(c);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#clear()
		 */
		@Override
		public void clear() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#contains(java.lang.Object)
		 */
		@Override
		public boolean contains(Object o) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#containsAll(java.util.Collection)
		 */
		@Override
		public boolean containsAll(Collection c) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#get(int)
		 */
		@Override
		public Object get(int index) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#indexOf(java.lang.Object)
		 */
		@Override
		public int indexOf(Object o) {
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#isEmpty()
		 */
		@Override
		public boolean isEmpty() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#iterator()
		 */
		@Override
		public Iterator<Object> iterator() {
			return NULL_LIST_ITERATOR;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#lastIndexOf(java.lang.Object)
		 */
		@Override
		public int lastIndexOf(Object o) {
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#listIterator()
		 */
		@Override
		public ListIterator<Object> listIterator() {
			return NULL_LIST_ITERATOR;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#listIterator(int)
		 */
		@Override
		public ListIterator<Object> listIterator(int index) {
			return NULL_LIST_ITERATOR;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#remove(java.lang.Object)
		 */
		@Override
		public boolean remove(Object o) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#remove(int)
		 */
		@Override
		public Object remove(int index) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#removeAll(java.util.Collection)
		 */
		@Override
		public boolean removeAll(Collection c) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#retainAll(java.util.Collection)
		 */
		@Override
		public boolean retainAll(Collection c) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#set(int, java.lang.Object)
		 */
		@Override
		public Object set(int index, Object element) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#size()
		 */
		@Override
		public int size() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#subList(int, int)
		 */
		@Override
		public List<Object> subList(int fromIndex, int toIndex) {
			return NULL_LIST_SET;
		}

		@Override
		public Spliterator spliterator() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#toArray()
		 */
		@Override
		public Object[] toArray() {
			return NULL_OBJECTS;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.List#toArray(T[])
		 */
		@Override
		public Object[] toArray(Object[] a) {
			return NULL_OBJECTS;
		}
	}

	/** NULL_MAP */
	public static final Map<Object, Object> NULL_MAP = new Map() {

		@Override
		public void clear() {
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public Set<Entry<Object, Object>> entrySet() {
			return (Set<Entry<Object, Object>>) (Set) NULL_LIST_SET;
		}

		@Override
		public Object get(Object key) {
			return null;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public Set<Object> keySet() {
			return NULL_LIST_SET;
		}

		@Override
		public Object put(Object key, Object value) {
			return NULL_LIST_SET;
		}

		@Override
		public void putAll(Map m) {
		}

		@Override
		public Object remove(Object key) {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Collection<Object> values() {
			return NULL_LIST_SET;
		}
	};

	/** NULL_LIST_ITERATOR */
	public static final ListIterator NULL_LIST_ITERATOR = new ListIterator() {

		@Override
		public void add(Object e) {
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public Object next() {
			return null;
		}

		@Override
		public int nextIndex() {
			return -1;
		}

		@Override
		public Object previous() {
			return null;
		}

		@Override
		public int previousIndex() {
			return -1;
		}

		@Override
		public void remove() {
		}

		@Override
		public void set(Object e) {
		}
	};
}
