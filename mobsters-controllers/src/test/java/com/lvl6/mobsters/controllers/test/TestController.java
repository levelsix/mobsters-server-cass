package com.lvl6.mobsters.controllers.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dekayd.astyanax.cassandra.Cassandra;
import com.lvl6.mobsters.entitymanager.nonstaticdata.UserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.User;
import com.lvl6.mobsters.services.user.UserService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


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
	
	@Autowired
	protected UserService userService;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	

	@Test
	public void testRetrieveUser() throws ConnectionException {
		String allUsersQuery = "select * from user;";
		List<User> allUsers = getUm().get().find(allUsersQuery);
		log.info("***************checking to see if ids are populated. users=" + allUsers);
		
		
		//there is already a user with facebook_id and udid = boo
		try {
			log.info("retrieving user by facebook_id, with allow filtering absent" +
					" and facebook_id does not have index");
			List<User> fbUsers = getUserService().getUserByUDIDorFbId(null, "boo");
			log.info("************************************facebook users=" + fbUsers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("1could not retrieve by udid, with allow filtering absent", e);
		}
		
		try {
			String query = "select * from user where facebook_id=boo allow filtering;";
			log.info("retrieving user by facebook_id, with allow filtering inserted" +
					" and facebook_id does not have index");
			log.info("query=" + query);
			List<User> fbUsers = getUm().get().find(query);
			log.info("************************************facebook users=" + fbUsers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("2could not retrieve by udid, with allow filtering inserted", e);
		}
		
		
		try {
			String query = "select * from user where udid=boo and facebook_id = boo;";
			log.info("retrieving user by udid and facebook_id, with allow filtering absent" +
					" udid has index, but facebook_id does not have index");
			log.info("query=" + query);
			List<User> users = getUm().get().find(query);
			log.info("************************************users=" + users);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("3could not retrieve by udid and facebook_id with allow filtering absent", e);
		}
		
		
		try {
			String query = "select * from user where udid=boo and facebook_id=boo allow filtering;";
			log.info("retrieving user by udid and facebook_id, with allow filtering inserted" +
					" udid has index, but facebook_id does not have index");
			log.info("query=" + query);
			List<User> users = getUm().get().find(query);
			log.info("************************************users=" + users);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("4could not retrieve by udid and facebook_id with allow filtering inserted", e);
		}
		
	}
	
	//DO NOT QUOTE STRINGS
	@Test
	public void testRetrieveUserQuotedStrings() throws ConnectionException {
		/*
		//there is already a user with facebook_id and udid = boo
		try {
			log.info("retrieving user by facebook_id, with allow filtering absent" +
					" and facebook_id does not have index");
			List<User> fbUsers = getUserService().getUserByUDIDorFbId(null, "'boo'");
			log.info("-------------------------------------facebook users=" + fbUsers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("-------1could not retrieve by udid, with allow filtering absent", e);
		}
		
		try {
			String query = "select * from user where facebook_id='boo' allow filtering;";
			log.info("retrieving user by facebook_id, with allow filtering inserted" +
					" and facebook_id does not have index");
			log.info("query=" + query);
			List<User> fbUsers = getUm().get().find(query);
			log.info("-------------------------------------facebook users=" + fbUsers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("-------2could not retrieve by udid, with allow filtering inserted", e);
		}
		
		
		try {
			String query = "select * from user where udid='boo' and facebook_id = 'boo';";
			log.info("retrieving user by udid and facebook_id, with allow filtering absent" +
					" udid has index, but facebook_id does not have index");
			log.info("query=" + query);
			List<User> users = getUm().get().find(query);
			log.info("-------------------------------------users=" + users);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("-------3could not retrieve by udid and facebook_id with allow filtering absent", e);
		}
		
		
		try {
			String query = "select * from user where udid='boo' and facebook_id='boo' allow filtering;";
			log.info("retrieving user by udid and facebook_id, with allow filtering inserted" +
					" udid has index, but facebook_id does not have index");
			log.info("query=" + query);
			List<User> users = getUm().get().find(query);
			log.info("-------------------------------------users=" + users);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("-------4could not retrieve by udid and facebook_id with allow filtering inserted", e);
		}
		*/
	}
	
	
	public void deleteAllDataFromAllColumnFamilies() {
		
	}
}
