package edu.ycp.cs.pygmymarmoset.model.persist;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import edu.ycp.cs.pygmymarmoset.app.model.PersistenceException;
import edu.ycp.cs.pygmymarmoset.app.model.User;
import edu.ycp.cs.pygmymarmoset.app.model.introspect.DBField;
import edu.ycp.cs.pygmymarmoset.app.model.introspect.Introspect;

public class Query {
	public static<E> String getCreateTableStatement(Class<E> modelCls) {
		Introspect<E> info = Introspect.getIntrospect(modelCls);
		
		StringBuilder buf = new StringBuilder();
		
		buf.append("create table ");
		buf.append(info.getTableName());
		buf.append(" (");
		
		DBField pk = null;
		int count = 0;
		for (DBField f : info.getFields()) {
			if (count > 0) {
				buf.append(", ");
			}
			buf.append(f.getName());
			buf.append(" ");
			buf.append(f.getSqlType());
			if (!f.isAllowNull()) {
				buf.append(" not null");
			}
			if (f.isPrimaryKey()) {
				buf.append(" auto_increment");
				pk = f;
			}
			count++;
		}
		if (pk != null) {
			buf.append(", primary key (");
			buf.append(pk.getName());
			buf.append(")");
		}
		
		// Create other keys
		for (DBField f : info.getFields()) {
			if (f.isUnique()) {
				List<String> combo = new ArrayList<>();
				combo.add(f.getName());
				if (!f.getUniqueWith().equals("")) {
					for (String other : f.getUniqueWith().split(",")) {
						DBField otherField = info.getFieldForPropertyName(other.trim());
						combo.add(otherField.getName());
					}
				}
				buf.append(", unique key (");
				buf.append(String.join(", ", combo));
				buf.append(")");
			}
			// TODO: allow non-unique index
		}
		
		buf.append(")");
		buf.append(" character set 'utf8' collate 'utf8_general_ci'");
		
		return buf.toString();
	}

	public static<E> String getInsertStatement(Class<E> modelCls) {
		Introspect<E> info = Introspect.getIntrospect(modelCls);
		StringBuilder buf = new StringBuilder();
		
		buf.append("insert into ");
		buf.append(info.getTableName());
		buf.append(" (");
		int count = 0;
		for (DBField field : info.getFields()) {
			if (field.isPrimaryKey()) {
				continue; // these are auto-generated
			}
			if (count > 0) {
				buf.append(", ");
			}
			buf.append(field.getName());
			count++;
		}
		buf.append(") values (");
		for (int i = 0; i < count; i++) {
			if (i > 0) {
				buf.append(", ");
			}
			buf.append("?");
		}
		buf.append(")");
		
		return buf.toString();
	}

	public static<E> String getUpdateStatement(Class<E> modelCls) {
		Introspect<E> info = Introspect.getIntrospect(modelCls);
		StringBuilder buf = new StringBuilder();

		buf.append("update ");
		buf.append(info.getTableName());
		buf.append(" set ");
		int count = 0;
		DBField pk = null;
		for (DBField field : info.getFields()) {
			if (field.isPrimaryKey()) {
				pk = field;
				continue; // will be used to identify object 
			}
			if (count > 0) {
				buf.append(", ");
			}
			buf.append(field.getName());
			buf.append(" = ?");
			count++;
		}
		if (pk == null) {
			throw new IllegalStateException("No primary key field found for " + modelCls.getSimpleName());
		}
		buf.append(" where ");
		buf.append(pk.getName());
		buf.append(" = ?");
		
		return buf.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getCreateTableStatement(User.class));
	}

	public static<E> int loadFields(E modelObj, ResultSet resultSet) {
		return loadFields(modelObj, resultSet, 1);
	}
	
	public static<E> int loadFields(E modelObj, ResultSet resultSet, int index) {
		try {
			return doLoadFields(modelObj, resultSet, index);
		} catch (Exception e) {
			throw new PersistenceException("Could not load model object fields from result set", e);
		}
	}
	
	private static<E> int doLoadFields(E modelObj, ResultSet resultSet, int index) throws Exception {
		@SuppressWarnings("unchecked")
		Class<E> cls = (Class<E>) modelObj.getClass();
		Introspect<E> info = Introspect.getIntrospect(cls);
		for (DBField field : info.getFields()) {
			Object value = resultSet.getObject(index);
			if (field.isEnum()) {
				// Enum values are persisted as ordinals,
				// so turn them back into enum values.
				value = field.getJavaType().getEnumConstants()[(Integer)value];
			}
			BeanUtils.setProperty(modelObj, field.getPropertyName(), value);
			index++;
		}
		return index;
	}

	public static<E> void storeFieldsNoId(E modelObj, PreparedStatement stmt) {
		try {
			doStoreFieldsNoId(modelObj, stmt);
		} catch (Exception e) {
			throw new PersistenceException("Could not store model object field values in prepared statement", e);
		}
	}
	
	private static<E> void doStoreFieldsNoId(E modelObj, PreparedStatement stmt) throws Exception {
		@SuppressWarnings("unchecked")
		Class<E> cls = (Class<E>) modelObj.getClass();
		Introspect<E> info = Introspect.getIntrospect(cls);
		int index = 1;
		for (DBField field : info.getFields()) {
			if (field.isPrimaryKey()) {
				continue; // skip id
			}
			storeFieldValue(modelObj, stmt, index, field);
			//System.out.println("Set field at index " + index);
			index++;
		}
	}
	
	public static<E> void storeFieldsForUpdate(E modelObj, PreparedStatement stmt) {
		try {
			doStoreFieldsForUpdate(modelObj, stmt);
		} catch (Exception e) {
			throw new PersistenceException("Could not store model object field values in prepared statement", e);
		}
	}

	private static <E> void doStoreFieldsForUpdate(E modelObj, PreparedStatement stmt)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		@SuppressWarnings("unchecked")
		Class<E> cls = (Class<E>) modelObj.getClass();
		Introspect<E> info = Introspect.getIntrospect(cls);
		DBField pk = null;
		// store non-primary key values
		int index = 1;
		for (DBField field : info.getFields()) {
			if (field.isPrimaryKey()) {
				pk = field;
				continue;
			}
			//System.out.println(" store " + field.getName() + " as " + BeanUtils.getProperty(modelObj, field.getPropertyName()));
			storeFieldValue(modelObj, stmt, index, field);
			index++;
		}
		// store primary key value
		//System.out.println(" store " + pk.getName() + " as " + BeanUtils.getProperty(modelObj, pk.getPropertyName()));
		storeFieldValue(modelObj, stmt, index, pk);
	}

	private static <E> void storeFieldValue(E modelObj, PreparedStatement stmt, int index, DBField field)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		Object value = PropertyUtils.getProperty(modelObj, field.getPropertyName());
		if (field.isEnum()) {
			// Enum values are persisted as the integer ordinal value
			value = ((Enum<?>)value).ordinal();
		} else if (field.isBoolean()) {
			value = ((Boolean)value) ? 1 : 0;
		}
		stmt.setObject(index, value);
	}
}
