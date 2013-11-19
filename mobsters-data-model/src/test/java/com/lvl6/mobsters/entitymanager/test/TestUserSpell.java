package com.lvl6.mobsters.entitymanager.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dekayd.astyanax.cassandra.Cassandra;
import com.dekayd.astyanax.cassandra.widerow.WideRowValue;
import com.lvl6.mobsters.widerows.UserSpells;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-cassandra.xml")
public class TestUserSpell{

	

	private static final Logger log = LoggerFactory.getLogger(TestUserSpell.class);	
	
	@Autowired
	Cassandra cassandra;
	
	public Cassandra getCassandra() {
		return cassandra;
	}

	public void setCassandra(Cassandra cassandra) {
		this.cassandra = cassandra;
	}

	
	@Autowired
	UserSpells userSpells;
	
	
	@Test
	public void testUserSpell() throws ConnectionException {
		UUID userId = UUID.randomUUID();
		for(int i = 0; i < 10; i++) {
			UUID spellId = UUID.randomUUID();
			Date now = new Date();
			WideRowValue<UUID, UUID, Date> wrv = new WideRowValue<UUID, UUID, Date>(userId, spellId, now);
			userSpells.saveValue(wrv);
		}
		Collection<WideRowValue<UUID, UUID, Date>> values = new ArrayList<WideRowValue<UUID, UUID, Date>>();
		for(int i = 0; i < 10; i++) {
			UUID spellId = UUID.randomUUID();
			Date now = new Date();
			WideRowValue<UUID, UUID, Date> wrv = new WideRowValue<UUID, UUID, Date>(userId, spellId, now);
			values.add(wrv);
		}
		userSpells.saveValues(values);
		Collection<WideRowValue<UUID, UUID, Date>> savedValues = userSpells.getEntireRow(userId);
		Assert.assertTrue("All values saved and returned", savedValues.size() == 20);
	}

	public UserSpells getUserSpells() {
		return userSpells;
	}

	public void setUserSpells(UserSpells userSpells) {
		this.userSpells = userSpells;
	}
}
