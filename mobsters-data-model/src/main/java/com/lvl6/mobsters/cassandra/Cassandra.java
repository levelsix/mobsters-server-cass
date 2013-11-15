package com.lvl6.mobsters.cassandra;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.Slf4jConnectionPoolMonitorImpl;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

@Component
public class Cassandra implements InitializingBean {

	
	
	private static final Logger log = LoggerFactory.getLogger(Cassandra.class);
	
	protected String keyspaceName;
	protected String cqlVersion;
	protected String targetCassandraVersion;
	protected String connectionPoolName;
	
	protected String clusterName = "";
	protected String seeds = "";
	
	protected String createTables = "off";

	protected Integer connectionsPerHost = 1;
	protected Integer port = 9160;

	protected AstyanaxContext<Keyspace> context;
	protected Keyspace keyspace;

	@Override
	public void afterPropertiesSet() throws Exception {
		setupContext();
	}

	protected void setupContext() {
		log.info("Creating cassandra context: {}", this);
		try {
		    if (context == null) {
		        context = new AstyanaxContext.Builder()
		           .forCluster(getClusterName())
		           .forKeyspace(getKeyspaceName())    
		           .withConnectionPoolConfiguration(
		               new ConnectionPoolConfigurationImpl(getConnectionPoolName())
		               .setPort(getPort())
		               .setMaxConnsPerHost(getConnectionsPerHost())
		               .setSeeds(getSeeds()))
		           .withConnectionPoolMonitor(new Slf4jConnectionPoolMonitorImpl())
		           .withAstyanaxConfiguration(
		               new AstyanaxConfigurationImpl()
		               .setCqlVersion(getCqlVersion())
		               .setTargetCassandraVersion(getTargetCassandraVersion())
		 	      .setDiscoveryType(NodeDiscoveryType.NONE))
		           .buildKeyspace(ThriftFamilyFactory.getInstance());
		       context.start();
		       keyspace = context.getClient();
		       setupKeyspace();
		     }
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	//TODO: setup replication
	protected void setupKeyspace() throws ConnectionException {
		try {
		getKeyspace().createKeyspace(ImmutableMap
			.<String, Object> builder()
			.put("strategy_options",
				ImmutableMap.<String, Object> builder().put("replication_factor", "1")
				.build())
			.put("strategy_class", "SimpleStrategy")
			.build());
		}catch(Exception e) {
			Log.info("Keyspace {} already exists", getKeyspaceName());
		}
	}

	
	
	
	
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getKeyspaceName() {
		return keyspaceName;
	}

	public void setKeyspaceName(String keyspaceName) {
		this.keyspaceName = keyspaceName;
	}

	public String getCqlVersion() {
		return cqlVersion;
	}

	public void setCqlVersion(String cqlVersion) {
		this.cqlVersion = cqlVersion;
	}

	public String getTargetCassandraVersion() {
		return targetCassandraVersion;
	}

	public void setTargetCassandraVersion(String targetCassandraVersion) {
		this.targetCassandraVersion = targetCassandraVersion;
	}

	public String getConnectionPoolName() {
		return connectionPoolName;
	}

	public void setConnectionPoolName(String connectionPoolName) {
		this.connectionPoolName = connectionPoolName;
	}

	public String getSeeds() {
		return seeds;
	}

	public void setSeeds(String seeds) {
		this.seeds = seeds;
	}

	public Integer getConnectionsPerHost() {
		return connectionsPerHost;
	}

	public void setConnectionsPerHost(Integer connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public AstyanaxContext<Keyspace> getContext() {
		if(context == null) {
			setupContext();
		}
		return context;
	}

	public void setContext(AstyanaxContext<Keyspace> context) {
		this.context = context;
	}

	public Keyspace getKeyspace() {
		if(keyspace == null) {
			setupContext();
		}
		return keyspace;
	}

	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}

	
	public String getCreateTables() {
		return createTables;
	}

	public void setCreateTables(String createTables) {
		this.createTables = createTables;
	}

	
	
	@Override
	public String toString() {
		return "Cassandra [keyspaceName=" + keyspaceName + ", cqlVersion=" + cqlVersion
				+ ", targetCassandraVersion=" + targetCassandraVersion + ", connectionPoolName="
				+ connectionPoolName + ", clusterName=" + clusterName + ", seeds=" + seeds
				+ ", connectionsPerHost=" + connectionsPerHost + ", port=" + port + "]";
	}
	
	
	
	
	

}
