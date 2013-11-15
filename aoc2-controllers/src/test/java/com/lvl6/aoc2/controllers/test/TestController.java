package com.lvl6.aoc2.controllers.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvl6.aoc2.cassandra.Cassandra;
import com.lvl6.aoc2.entitymanager.UserEntityManager;
import com.lvl6.aoc2.po.User;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-services.xml")
public class TestController {

	
	
	private static final Logger log = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	Cassandra cassandra;
	
	public Cassandra getCassandra() {
		return cassandra;
	}

	public void setCassandra(Cassandra cassandra) {
		this.cassandra = cassandra;
	}

	@Autowired
	protected UserEntityManager um;
	
	public UserEntityManager getUm() {
		return um;
	}

	public void setUm(UserEntityManager um) {
		this.um = um;
	}

	@Test
	public void testCreatingUser() throws ConnectionException{
		User user = new User();
		//user.setEmail("anyone@anyserver.com");
		user.setName("someUser");
		log.info("Saving user: {}", user);
		um.get().put(user);
		ColumnList<String> cl = cassandra.getKeyspace().prepareQuery(um.getColumnFamily()).getKey(user.getId()).execute().getResult();
		Assert.assertNotSame(0, cl.size());
		for (Column<String> c : cl) {
			Log.info("Got column : " + c.getName());
		}
		User user2 = um.get().get(user.getId());
		assertNotNull(user2);
		assertTrue("Usernames equal", user.getName().equals(user2.getName()));
		//assertTrue("Emails equal", user.getEmail().equals(user2.getEmail()));
		
		
		//checking to see if this is how to update an object
		//update name
		user.setName("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
		um.get().put(user);
		
		user2 = um.get().get(user2.getId());
		assertTrue("Usernames equal", user.getName().equals(user2.getName()));
	}
	
	public void deleteAllDataFromAllColumnFamilies() {
		
	}
}
