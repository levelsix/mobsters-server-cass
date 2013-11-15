package com.lvl6.aoc2.entitymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.lvl6.aoc2.cassandra.Cassandra;
import com.lvl6.aoc2.po.BasePersistentObject;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.EntityManager;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.SerializerTypeInferer;
import com.netflix.astyanax.serializers.StringSerializer;

abstract public class BaseEntityManager<Clas extends BasePersistentObject, Ky>  implements InitializingBean{

	
	private static final Logger log = LoggerFactory.getLogger(BaseEntityManager.class);
	
	@Autowired
	protected Cassandra cassandra;
	
	protected  ColumnFamily<Ky, String> columnFamily;// = new ColumnFamily<Ky, Col>("users", UUIDSerializer.get(), StringSerializer.get());;
	
	protected Class<Clas> type;
	protected Class<Ky> key;

    public BaseEntityManager(Class<Clas> type, Class<Ky> key) { 
    	super();
    	this.type = type; 
    	this.key = key;
    }
	
	
	protected EntityManager<Clas, Ky> em;
	

	@Override
	public void afterPropertiesSet() throws Exception {
		createColumnFamily();
		setupEntityManager();
	}

	
	protected Serializer<Ky> getSerializer(){
		return SerializerTypeInferer.getSerializer(key);
	}
	

	protected void createColumnFamily() throws ConnectionException {
		try {
			columnFamily = new ColumnFamily<Ky, String>(type.getSimpleName().toLowerCase(), getSerializer(), StringSerializer.get());
		//cassandra.getKeyspace().createColumnFamily(columnFamily, getIndexes());
		}catch(Exception e) {
			log.info("Column family {} already exists", columnFamily.getName());
		}
	}

	
	protected void setupEntityManager() throws ConnectionException{
		log.info("Building entity manager for {} and keyspace {}", type.getSimpleName(), getKeyspace().getKeyspaceName());
		em = new DefaultEntityManager.Builder<Clas, Ky>()
		.withEntityType(type)
		.withKeyspace(getKeyspace())
		.withColumnFamily(columnFamily)
		.withAutoCommit(true)
		.build();
		try {
			Clas cls = this.type.newInstance();
			if(getCassandra().getCreateTables().equals("create")) {
				log.info("Creating tables");
				createTable(cls);
				updateTable(cls);
				addOrRemoveIndexes(cls);
			}
		}catch(Exception e) {
			log.warn("Error creating storage for {}", type.getSimpleName(), e);
		}
	}
	
	
	protected void createTable(Clas cls) {
		try {
			log.info("Creating table: {}", cls.getTableCreateStatement());
			getKeyspace()
			    .prepareQuery(columnFamily)
			    .withCql(cls.getTableCreateStatement())
			    .execute();
		}catch(Exception e) {
			log.info("Could not create table for {} message: {}", type.getSimpleName(), e.getMessage());
		}
	}


	protected Keyspace getKeyspace() {
		return cassandra.getKeyspace();
	}
	
	protected void updateTable(Clas cls) {
		for(String update : cls.getTableUpdateStatements()) {
			try {
				getKeyspace()
				    .prepareQuery(columnFamily)
				    .withCql(update)
				    .execute();
			}catch(Exception e) {
				log.info("Could not update table {} message: {}", type.getSimpleName(), e.getMessage());
			}
		}
	}
	
	protected void addOrRemoveIndexes(Clas cls) {
		for(String index : cls.getIndexCreateStatements()) {
			try {
				log.info("Creating index: {}",index);
				getKeyspace()
				    .prepareQuery(columnFamily)
				    .withCql(index)
				    .execute();
			}catch(Exception e) {
				log.info("Could not create index for table {} message: {}", type.getSimpleName(), e.getMessage());
			}
		}
	}
	


	public EntityManager<Clas, Ky> getEm() {
		return em;
	}

	public void setEm(EntityManager<Clas, Ky> em) {
		this.em = em;
	}

	public EntityManager<Clas, Ky> get() {
		return getEm();
	}
	
	
	public Cassandra getCassandra() {
		return cassandra;
	}

	public void setCassandra(Cassandra cassandra) {
		this.cassandra = cassandra;
	}
	
	
	public ColumnFamily<Ky, String> getColumnFamily() {
		return columnFamily;
	}

	public void setColumnFamily(ColumnFamily<Ky, String> columnFamily) {
		this.columnFamily = columnFamily;
	}
	
	



}