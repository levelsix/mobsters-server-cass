package com.lvl6.mobsters.controllers.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dekayd.astyanax.cassandra.Cassandra;
import com.lvl6.mobsters.po.nonstaticdata.ClanForUser;
import com.lvl6.mobsters.services.clanforuser.ClanForUserService;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;


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

	
	@Test
	public void testCreatingClanForUser() throws ConnectionException{
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
		
		ColumnList<String> cl = cassandra.getKeyspace()
				.prepareQuery(
						getClanForUserService()
						.getClanForUserEntityManager()
						.getColumnFamily())
				.getKey(cfu.getId()).execute().getResult();
		Assert.assertNotSame(0, cl.size());
		for (Column<String> c : cl) {
			Log.info("Got column : " + c.getName());
		}
		ClanForUser cfu2 = getClanForUserService()
				.getUserClanForUserAndClanId(userId, clanId);
		
		assertNotNull(cfu2);
		assertTrue("time of entries equal", cfu.getTimeOfEntry().equals(cfu2.getTimeOfEntry()));
		
		log.info("cfu=" + cfu);
		log.info("cfu2=" + cfu2);
		
		try {
			log.info("deleting by column key");
			//testing to see if this deleting works by not specifying a rowKeyId
			getClanForUserService().deleteUserClansForUserProspective(userId);
			log.info("deleting worked!.");
		} catch (Exception e) {
			log.error("as expected, could not delete by column key :(.", e);
		}
		
		try {
			ClanForUser cfu3 = new ClanForUser();
			cfu3.setClanId(clanId);
			cfu3.setUserId(userId);
			cfu3.setId(null);
			log.info("cfu3=" + cfu3);
			log.info("deleting by column keys 2");
			getClanForUserService().deleteUserClan(cfu3);
			log.info("deleting worked! 2.");
		} catch (Exception e) {
			log.error("as expected, could not delete by specifying column keys only", e);
		}
		//delete via the regular way
		getClanForUserService().deleteUserClan(cfu2);
		
		List<ClanForUser> existingCfu = getClanForUserService().getAllUserClansForUser(userId);
		log.info("should be none, existingCfu=" + existingCfu);
		assertTrue("no existing cfus.", null == existingCfu);
		
		if (null != existingCfu) {
			assertTrue("no existing cfus2.", existingCfu.isEmpty());
		}
	}
	
	public void deleteAllDataFromAllColumnFamilies() {
		
	}
}
