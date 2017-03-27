package edu.ycp.cs.pygmymarmoset.model.persist;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import edu.ycp.cs.pygmymarmoset.app.model.Course;
import edu.ycp.cs.pygmymarmoset.app.model.Desc;
import edu.ycp.cs.pygmymarmoset.app.model.Project;
import edu.ycp.cs.pygmymarmoset.app.model.Role;
import edu.ycp.cs.pygmymarmoset.app.model.Submission;
import edu.ycp.cs.pygmymarmoset.app.model.User;

public class Introspect<E> {
	private Class<E> cls;
	private String name;
	private List<DBField> fields;

	private Introspect(Class<E> cls) throws IntrospectionException, NoSuchFieldException, SecurityException {
		this.cls = cls;
		analyze();
	}
	
	private void analyze() throws IntrospectionException, NoSuchFieldException, SecurityException {
		name = cls.getSimpleName();
		fields = new ArrayList<>();
		BeanInfo beanInfo = Introspector.getBeanInfo(cls);
		PropertyDescriptor[] fieldDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor propertyDesc : fieldDescriptors) {
			if (!propertyDesc.getName().equals("class")) {
				DBField dbField = new DBField();
				dbField.setName(propertyDesc.getName().toLowerCase());
				dbField.setJavaType(propertyDesc.getPropertyType());
				// Note that we require a declared field of the name
				// indicated by the bean property descriptor.
				// This is used to look up the @Desc annotation for
				// the field, if there is one.
				Field f = cls.getDeclaredField(propertyDesc.getName());
				if (f != null) {
					Desc desc = f.getAnnotation(Desc.class);
					if (desc != null) {
						dbField.setSize(desc.size());
						dbField.setFixed(desc.fixed());
					}
				}
				fields.add(dbField);
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getTableName() {
		return pluralize(name.toLowerCase());
	}
	
	public List<DBField> getFields() {
		return fields;
	}

	private static String pluralize(String name) {
		if (name.endsWith("y")) {
			return name.substring(0, name.length() - 1) + "ies";
		} else {
			return name + "s";
		}
	}
	
	public static void main(String[] args) throws Exception {
		doIntrospect(Course.class);
		doIntrospect(Project.class);
		doIntrospect(Role.class);
		doIntrospect(Submission.class);
		doIntrospect(User.class);
	}
	
	private static<E> void doIntrospect(Class<E> cls) throws Exception {
		Introspect<E> info = getIntrospect(cls);
		System.out.println("name=" + info.getName());
		System.out.println("tableName=" + info.getTableName());
		for (DBField f : info.getFields()) {
			System.out.printf("  Field %s, type=%s, sqltype=%s, size=%d, fixed=%s\n",
					f.getName(),
					f.getJavaType().getSimpleName(),
					f.getSqlType(),
					f.getSize(),
					f.isFixed());
		}
	}
	
	private static final ConcurrentHashMap<Class<?>, Introspect<?>> cache = new ConcurrentHashMap<>();
	
	/**
	 * Get an {@link Introspect} object for given model class.
	 * A cached object is returned if possible.
	 * 
	 * @param cls a model class
	 * @return the {@link Introspect} object for the model class
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IntrospectionException
	 */
	@SuppressWarnings("unchecked")
	public static<E> Introspect<E> getIntrospect(Class<E> cls) throws NoSuchFieldException, SecurityException, IntrospectionException {
		Introspect<E> info = (Introspect<E>) cache.get(cls);
		if (info == null) {
			info = new Introspect<>(cls);
			Introspect<E> prev = (Introspect<E>) cache.putIfAbsent(cls, info);
			if (prev != null) {
				info = prev;
			}
		}
		return info;
	}
}
