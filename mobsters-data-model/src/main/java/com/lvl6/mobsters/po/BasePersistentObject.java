package com.lvl6.mobsters.po;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;

import com.lvl6.mobsters.cassandra.CQL3Util;
import com.lvl6.mobsters.entitymanager.Index;

abstract public class BasePersistentObject {
	
	
	private static final Logger log = LoggerFactory.getLogger(BasePersistentObject.class);

	/**
	 * The CQL statement to create the table for this Object
	 * @return
	 */
	public String getTableCreateStatement() {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ");
		sb.append(tableName());
		sb.append(" ( ");
		addFieldsToTableCreateStatement(sb);
		sb.append("primary key (id)) ");
		sb.append(tableOptionsString());
		return sb.toString();
	}
	
	protected void addFieldsToTableCreateStatement(StringBuilder sb) {
		@SuppressWarnings("unchecked")
		List<Field> fields = Arrays.asList(this.getClass().getDeclaredFields());
		for(Field field : fields) {
			try {
			if(field.isAnnotationPresent(Index.class)) {
				Column clm = field.getAnnotation(Column.class);
				String fieldName = clm.name();
				sb.append(fieldName);
				sb.append(" ");
				sb.append(CQL3Util.getCql3Type(field.getType()));
				sb.append(", ");
			}else if(field.isAnnotationPresent(Id.class)) {
				String fieldName = field.getName();
				sb.append(fieldName);
				sb.append(" ");
				sb.append(CQL3Util.getCql3Type(field.getType()));
				sb.append(", ");
			}
			}catch(Exception e) {
				log.error("Error creating index create statement", e);
			}
		}
	}
	
	protected String tableOptionsString() {
		return " with compact storage;";
	}
	
	/**
	 * The CQL statements needed to add or remove columns from existing table for this object
	 * Use this if you cannot drop the table and recreate it
	 * @return
	 */
	abstract public Set<String> getTableUpdateStatements();
	
	/**
	 * The CQL statements needed to create or remove indexes for this object
	 * @return
	 */
	
	private Set<String> indexCreateStatements = new HashSet<String>();
	public Set<String> getIndexCreateStatements(){
		@SuppressWarnings("unchecked")
		List<Field> fields = Arrays.asList(this.getClass().getDeclaredFields());
		for(Field field : fields) {
			try {
			if(field.isAnnotationPresent(Index.class)) {
				Column clm = field.getAnnotation(Column.class);
				String fieldName = clm.name();
				indexCreateStatements.add(indexCreateStatement(field, fieldName));
			}
			}catch(Exception e) {
				log.error("Error creating index create statement", e);
			}
		}
		return indexCreateStatements;
	}
	
	protected String indexCreateStatement(Field field, String fieldName) {
		return "create index "+tableName()+"_"+fieldName+"_key on "+tableName()+" ("+fieldName+");";
	}
	
	
	public String tableName() {
		return this.getClass().getSimpleName().toLowerCase();
	}
	
	
	abstract public UUID getId(); 
	abstract public void setId(UUID id); 
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasePersistentObject other = (BasePersistentObject) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
	
}
