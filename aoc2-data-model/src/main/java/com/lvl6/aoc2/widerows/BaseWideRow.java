package com.lvl6.aoc2.widerows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import scala.collection.Iterable;

import com.lvl6.aoc2.cassandra.Cassandra;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.SerializerTypeInferer;

abstract public class BaseWideRow<Ky, Col, Val> implements InitializingBean {

	
	private static final Logger log = LoggerFactory.getLogger(BaseWideRow.class);
	
	
	@Autowired
	protected Cassandra cassandra;
	
	



	protected ColumnFamily<Ky, Col> columnFamily;
	
	
	protected Class<Col> column;
	protected Class<Ky> key;
	protected Class<Val> value;
	
	protected Serializer<Ky> getKeySerializer(){
		return SerializerTypeInferer.getSerializer(key);
	}
	
	protected Serializer<Col> getColumnSerializer(){
		return SerializerTypeInferer.getSerializer(column);
	}
	
	protected Serializer<Val> getValueSerializer(){
		return SerializerTypeInferer.getSerializer(value);
	}
	
	protected String getColumnFamilyName() {
		return this.getClass().getSimpleName().toLowerCase();
	}
	
	public BaseWideRow(Class<Ky> key,Class<Col> column,Class<Val> value) {
		super();
		this.key = key;
		this.column = column;
		this.value = value;
	}
	
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		createColumnFamily();
		createTable();
	}
	
	
	public WideRowValue<Ky, Col, Val> getSingleColumn(Ky rowKey, Col column) throws ConnectionException{
		WideRowValue<Ky, Col, Val> wrv = new WideRowValue<Ky, Col, Val>().setKey(rowKey).setColumn(column);
		Column<Col> result = getKeyspace().prepareQuery(getColumnFamily())
			    .getKey(rowKey)
			    .getColumn(column)
			    .execute().getResult();
		wrv.setValue(result.getValue(getValueSerializer()));
		return wrv;
	}

	protected Keyspace getKeyspace() {
		return cassandra.getKeyspace();
	}
	
	
	public Collection<WideRowValue<Ky, Col, Val>> getEntireRow(Ky key) throws ConnectionException{
		log.info("Getting row for key: {}", key);
		Collection<WideRowValue<Ky, Col, Val>> values = new ArrayList<WideRowValue<Ky, Col, Val>>();
		ColumnList<Col> result = getKeyspace().prepareQuery(getColumnFamily())
			    .getKey(key)
			    .execute().getResult();
		if (!result.isEmpty()) {
			Iterator<Column<Col>> it = result.iterator();
			while(it.hasNext()) {
				Column<Col> column = it.next();
				WideRowValue<Ky, Col, Val> wrv = new WideRowValue<Ky, Col, Val>()
					.setKey(key)
					.setColumn(column.getName())
					.setValue(column.getValue(getValueSerializer()));
				values.add(wrv);
			}
		}
		return values;
	}
	
	
	public void saveValue(WideRowValue<Ky, Col, Val> wrv) throws ConnectionException {
		getKeyspace().prepareColumnMutation(getColumnFamily(), wrv.getKey(), wrv.getColumn())
		.putValue(wrv.getValue(), getValueSerializer(), null)
	    .execute();
	}

	
	public void saveValues(Collection<WideRowValue<Ky, Col, Val>> values) throws ConnectionException {
		Map<Ky, Iterable<WideRowValue<Ky, Col, Val>>> grouped = WideRowUtil.groupWideRowValuesByKey(values);
		MutationBatch m = getKeyspace().prepareMutationBatch();
		for(Ky kee: grouped.keySet()) {
			Iterable<WideRowValue<Ky, Col, Val>> itb = grouped.get(kee);
			ColumnListMutation<Col> clm = m.withRow(getColumnFamily(), kee);
			scala.collection.Iterator<WideRowValue<Ky, Col, Val>> it = itb.iterator();
			while(it.hasNext()) {
				WideRowValue<Ky, Col, Val> value = it.next();
				putColumn(clm, value);
			}
		}
		m.execute();
	}
	
	
	

	protected void putColumn(ColumnListMutation<Col> clm,	WideRowValue<Ky, Col, Val> value) {
		clm.putColumn(value.getColumn(), value.getValue(), getValueSerializer(), null);
	}
	
	
	public void saveValuesWithUniqueKeys(Map<Ky, WideRowValue<Ky, Col, Val>> values)throws ConnectionException {
		MutationBatch m = getKeyspace().prepareMutationBatch();
		for(Ky kee: values.keySet()) {
			WideRowValue<Ky, Col, Val> value = values.get(kee);
			ColumnListMutation<Col> clm = m.withRow(getColumnFamily(), kee);
			putColumn(clm, value);
		}
		m.execute();
	}
	
	
		
	protected void createColumnFamily() {
		try {
			columnFamily = new ColumnFamily<Ky, Col>(getColumnFamilyName(), getKeySerializer(), getColumnSerializer());
			//cassandra.getKeyspace().createColumnFamily(columnFamily, getIndexes());
		}catch(Exception e) {
			log.info("Column family {} already exists", columnFamily.getName());
		}
	}
	
	
	protected void createTable() {
		try {
			getKeyspace().createColumnFamily(getColumnFamily(), null);
		}catch(Exception e) {
			log.info("Could not create wide row table for {} message: {}", getColumnFamilyName(), e.getMessage());
		}
	}
	
	public Cassandra getCassandra() {
		return cassandra;
	}

	public void setCassandra(Cassandra cassandra) {
		this.cassandra = cassandra;
	}

	public ColumnFamily<Ky, Col> getColumnFamily() {
		return columnFamily;
	}

	public void setColumnFamily(ColumnFamily<Ky, Col> columnFamily) {
		this.columnFamily = columnFamily;
	}

	public Class<Col> getColumn() {
		return column;
	}

	public void setColumn(Class<Col> column) {
		this.column = column;
	}

	public Class<Ky> getKey() {
		return key;
	}

	public void setKey(Class<Ky> key) {
		this.key = key;
	}

	public Class<Val> getValue() {
		return value;
	}

	public void setValue(Class<Val> value) {
		this.value = value;
	}
}
