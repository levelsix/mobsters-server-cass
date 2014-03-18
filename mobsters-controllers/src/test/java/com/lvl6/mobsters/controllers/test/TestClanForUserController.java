package com.lvl6.mobsters.controllers.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dekayd.astyanax.cassandra.Cassandra;
import com.lvl6.mobsters.entitymanager.nonstaticdata.ClanForUserEntityManager;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.services.clanforuser.ClanForUserService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-services.xml")
public class TestClanForUserController {

	
	
	private static final Logger log = LoggerFactory.getLogger(TestClanForUserController.class);
	
	@Autowired
	Cassandra cassandra;
	
	public Cassandra getCassandra() {
		return cassandra;
	}

	public void setCassandra(Cassandra cassandra) {
		this.cassandra = cassandra;
	}

	@Autowired
	protected ClanForUserService clanForUserService;

	public ClanForUserService getClanForUserService() {
		return clanForUserService;
	}

	public void setClanForUserService(ClanForUserService clanForUserService) {
		this.clanForUserService = clanForUserService;
	}
	
	@Autowired
	protected ClanForUserEntityManager clanForUserEntityManager;
	
	public ClanForUserEntityManager getClanForUserEntityManager() {
		return clanForUserEntityManager;
	}

	public void setClanForUserEntityManager(
			ClanForUserEntityManager clanForUserEntityManager) {
		this.clanForUserEntityManager = clanForUserEntityManager;
	}

	@Test
	public void testCreatingClanForUser() throws ConnectionException{
		String query = "select * from clanforuser;";
		log.info("seeing if id column is populated");
		List<ClanForUser> cfuList = getClanForUserEntityManager().get().find(query);
		log.info("cfuList=" + cfuList);
		
		UUID clanId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		
		ClanForUser cfu = new ClanForUser();
		//user.setEmail("anyone@anyserver.com");
		cfu.setClanId(clanId);
		cfu.setUserId(userId);
		cfu.setTimeOfEntry(new Date());
		cfu.setStatus("woof");
		
		
		log.info("Saving clanForUser: {}", cfu);
		getClanForUserService().saveClanForUser(cfu);
		
		ClanForUser cfu2 = getClanForUserService()
				.getUserClanForUserAndClanId(userId, clanId);
		
		assertNotNull(cfu2);
		assertTrue("time of entries equal", cfu.getTimeOfEntry().equals(cfu2.getTimeOfEntry()));
		
		log.info("cfu=" + cfu);
		log.info("cfu2=" + cfu2);
		log.info("cfu2 id=" + cfu2.getId());
//		try {
//			log.info("deleting by providing primary keyless object");
//			getClanForUserEntityManager().get().remove(cfu2);
//		} catch (Exception e) {
//			log.error("as expected, could not delete by providing keyless object", e);
//		}
//		
//		try {
//			log.info("deleting by column key");
//			//testing to see if this deleting works by not specifying a rowKeyId
//			getClanForUserService().deleteUserClansForUserProspective(userId);
//			log.info("deleting worked!.");
//		} catch (Exception e) {
//			log.error("as expected, could not delete by column key, need row key :(.", e);
//		}
		
		try {
			//delete via the regular way
			getClanForUserService().deleteUserClan(cfu2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("could not delete regular way by primary key", e);
		}
		
	}
	
	public void deleteAllDataFromAllColumnFamilies() {
		
	}
}
